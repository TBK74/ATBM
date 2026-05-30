package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.KeyDAO;
import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.dao.OrderSignatureDAO;
import vn.edu.hcmuaf.fit.model.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.*;
import java.util.Base64;

/**
 * Lõi nghiệp vụ ký số đơn hàng.
 *
 * Luồng:
 *   1. Sau khi đơn được tạo → gọi initHash(orderId)
 *   2. Server trả hash cho client → client dùng tool ký (sign_order.jsp)
 *   3. Client upload signature + keyId → gọi submitSignature(orderId, sig, keyId)
 *   4. Bất kỳ lúc nào (load trang, admin xem) → gọi verify(orderId)
 *   5. Khi đơn bị sửa → gọi invalidateSignature(orderId)
 */
public class OrderSignatureService {
    private static volatile OrderSignatureService instance;
    public static OrderSignatureService getInstance() {
        if (instance == null) {
            synchronized (OrderSignatureService.class) {
                if (instance == null) instance = new OrderSignatureService();
            }
        }
        return instance;
    }

    private final OrderSignatureDAO sigDao = OrderSignatureDAO.getInstance();
    private final OrderDAO          ordDao = OrderDAO.getInstance();
    private final KeyDAO            keyDao = KeyDAO.getInstance();

    /**
     * Build canonical JSON, tính SHA-256, lưu vào order_signatures.
     * @return SHA-256 hex string hoặc null nếu lỗi
     */
    public String initHash(int orderId) {
        Order order = ordDao.getById(orderId);
        if (order == null) return null;
        List<OrderItem> items = ordDao.getItems(orderId);

        String canonical = buildCanonicalJson(order, items);
        String hash = sha256Hex(canonical);
        if (hash == null) return null;

        sigDao.initSignature(orderId, hash, canonical);
        return hash;
    }

    /** Lấy hash hiện tại của đơn hàng để hiển thị trong tool ký */
    public OrderSignature getSignatureInfo(int orderId) {
        return sigDao.getByOrderId(orderId);
    }

    /**
     * Lưu chữ ký do client gửi lên, sau đó verify ngay.
     * @param orderId  ID đơn hàng
     * @param sigBase64 Chữ ký RSA dạng Base64 (output của Web Crypto API)
     * @param keyId    ID của key dùng để ký (từ user_keys)
     * @return "verified" | "tampered" | "error"
     */
    public String submitSignature(int orderId, String sigBase64, int keyId) {
        boolean saved = sigDao.saveSignature(orderId, sigBase64, keyId);
        if (!saved) return "error";
        return verify(orderId);
    }

    /**
     * Verify chữ ký của một đơn:
     * 1. Tính lại hash từ dữ liệu hiện tại
     * 2. So sánh với hash gốc (phát hiện dữ liệu bị sửa)
     * 3. Kiểm tra chữ ký RSA bằng public key
     * 4. Kiểm tra key có hợp lệ tại thời điểm ký không (revoke check)
     *
     * @return "verified" | "tampered" | "unsigned" | "error"
     */
    public String verify(int orderId) {
        OrderSignature sig = sigDao.getByOrderId(orderId);
        if (sig == null) return "error";
        if ("unsigned".equals(sig.getSignStatus())) return "unsigned";
        if (sig.getSignature() == null || sig.getSignature().isBlank()) {
            sigDao.updateStatus(orderId, "unsigned");
            return "unsigned";
        }

        // --- Kiểm tra tính toàn vẹn dữ liệu ---
        Order order = ordDao.getById(orderId);
        if (order == null) return "error";
        List<OrderItem> items = ordDao.getItems(orderId);
        String currentHash = sha256Hex(buildCanonicalJson(order, items));

        if (!sig.getDataHash().equals(currentHash)) {
            // Dữ liệu đơn bị thay đổi sau khi ký → tampered
            sigDao.updateStatus(orderId, "tampered");
            return "tampered";
        }

        // --- Kiểm tra chữ ký RSA ---
        if (sig.getSigningKeyId() == null) {
            sigDao.updateStatus(orderId, "tampered");
            return "tampered";
        }

        UserKey key = keyDao.getById(sig.getSigningKeyId());
        if (key == null) { sigDao.updateStatus(orderId, "tampered"); return "tampered"; }

        // Kiểm tra key có hợp lệ tại thời điểm ký không
        if (!keyDao.isKeyValidAt(key.getKeyId(), sig.getSignedAt())) {
            sigDao.updateStatus(orderId, "tampered");
            return "tampered"; // Key đã bị revoke trước khi ký → đơn không đáng tin
        }

        boolean valid = verifyRsaSignature(
                sig.getDataHash(),          // Data được ký là hash hex (UTF-8 bytes)
                sig.getSignature(),         // Base64 signature
                key.getPublicKey()          // PEM public key
        );

        String result = valid ? "verified" : "tampered";
        sigDao.updateStatus(orderId, result);
        return result;
    }

    /**
     * Quét và verify toàn bộ danh sách đơn hàng (theo yêu cầu: mỗi lần load trang).
     * @param orderIds Danh sách orderId cần scan
     * @return Map<orderId, signStatus>
     */
    public Map<Integer, String> verifyAll(List<Integer> orderIds) {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (int orderId : orderIds) {
            result.put(orderId, verify(orderId));
        }
        return result;
    }

    /**
     * Gọi khi đơn hàng bị sửa (trước hoặc sau khi đóng gói).
     * Reset chữ ký về unsigned và cập nhật hash theo dữ liệu mới.
     * Person C gọi hàm này trong luồng admin/user sửa đơn.
     */
    public boolean invalidateSignature(int orderId) {
        Order order = ordDao.getById(orderId);
        if (order == null) return false;
        List<OrderItem> items = ordDao.getItems(orderId);
        String newCanonical = buildCanonicalJson(order, items);
        String newHash = sha256Hex(newCanonical);
        if (newHash == null) return false;
        return sigDao.invalidate(orderId, newHash, newCanonical);
    }

    /**
     * Kiểm tra đơn đã có chữ ký hợp lệ (signed/verified/tampered) hay vẫn "unsigned".
     * Dùng bởi AuditService.beforeAdminEdit() để quyết định có cần chặn sửa hay không.
     */
    public boolean isSigned(int orderId) {
        OrderSignature sig = sigDao.getByOrderId(orderId);
        if (sig == null) return false;
        return !"unsigned".equals(sig.getSignStatus()) && sig.getSignature() != null && !sig.getSignature().isBlank();
    }

    /** Lấy hash hiện đang lưu trong order_signatures (dùng để log/so sánh, không tự verify lại) */
    public String getStoredHash(int orderId) {
        OrderSignature sig = sigDao.getByOrderId(orderId);
        return sig != null ? sig.getDataHash() : null;
    }

    /** Lấy danh sách orderId đã từng được ký bằng một key cụ thể (dùng khi key bị revoke) */
    public List<Integer> getOrdersSignedByKey(int keyId) {
        return sigDao.getOrderIdsBySigningKey(keyId);
    }

    /** Lấy Map<orderId, OrderSignature> để hiển thị batch trên danh sách
     * (tránh N+1 query trong PurchaseHistoryController).
     */
    public Map<Integer, OrderSignature> getSignatureMap(List<Integer> orderIds) {
        List<OrderSignature> list = sigDao.getByOrderIds(orderIds);
        Map<Integer, OrderSignature> map = new HashMap<>();
        for (OrderSignature s : list) map.put(s.getOrderId(), s);
        return map;
    }

    // CANONICAL JSON — "Hợp đồng dữ liệu" bất biến
    // Tất cả field sorted theo alphabet, số nguyên (không decimal).
    // Format này PHẢI khớp với hàm buildCanonicalJson() trong JS (sign_order.jsp).

    /**
     * Build JSON chuẩn hóa cho đơn hàng.
     * Bao gồm: orderId, customerId, recipientName, paymentMethod,
     *          promoCode, discountAmount, totalAmount, orderDate,
     *          items (mỗi item: itemId, itemTitle, itemType, priceAtOrder, quantity)
     */
    public String buildCanonicalJson(Order order, List<OrderItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"customerId\":").append(order.getCustomerId()).append(',');
        sb.append("\"discountAmount\":").append((long) order.getDiscountAmount()).append(',');
        sb.append("\"items\":[");
        // Sort items by itemId để đảm bảo thứ tự nhất quán
        items.sort(Comparator.comparingInt(OrderItem::getItemId));
        for (int i = 0; i < items.size(); i++) {
            OrderItem it = items.get(i);
            if (i > 0) sb.append(',');
            sb.append('{');
            sb.append("\"itemId\":").append(it.getItemId()).append(',');
            sb.append("\"itemTitle\":\"").append(escapeJson(it.getItemTitle())).append("\",");
            sb.append("\"itemType\":\"").append(escapeJson(it.getItemType())).append("\",");
            sb.append("\"priceAtOrder\":").append((long) it.getPriceAtOrder()).append(',');
            sb.append("\"quantity\":1");  // Sản phẩm số: luôn = 1
            sb.append('}');
        }
        sb.append("],");
        sb.append("\"orderId\":").append(order.getOrderId()).append(',');
        sb.append("\"orderDate\":\"").append(order.getOrderDate() != null ? order.getOrderDate().toString() : "").append("\",");
        sb.append("\"paymentMethod\":\"").append(escapeJson(order.getPaymentMethod())).append("\",");
        sb.append("\"promoCode\":\"").append(escapeJson(order.getPromoCode())).append("\",");
        sb.append("\"recipientName\":\"").append(escapeJson(order.getRecipientName())).append("\",");
        sb.append("\"totalAmount\":").append((long) order.getTotalAmount());
        sb.append('}');
        return sb.toString();
    }

    // ================================================================
    // PRIVATE HELPERS
    // ================================================================

    private String sha256Hex(String input) {
        if (input == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); return null;
        }
    }

    /**
     * Verify chữ ký RSA-SHA256.
     * @param dataToVerify  Chuỗi hex hash đã ký (UTF-8)
     * @param signatureB64  Base64 chữ ký
     * @param publicKeyPem  PEM SPKI public key
     */
    private boolean verifyRsaSignature(String dataToVerify, String signatureB64, String publicKeyPem) {
        try {
            // Parse PEM → PublicKey
            String pemStripped = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(pemStripped);
            PublicKey pubKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(keyBytes));

            // Verify signature
            byte[] sigBytes = Base64.getDecoder().decode(signatureB64);
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pubKey);
            sig.update(dataToVerify.getBytes(StandardCharsets.UTF_8));
            return sig.verify(sigBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}