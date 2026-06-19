package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.AuditDAO;
import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.dao.OrderEditRequestDAO;
import vn.edu.hcmuaf.fit.model.AdminAlert;
import vn.edu.hcmuaf.fit.model.Order;
import vn.edu.hcmuaf.fit.util.EmailUtils;
import vn.edu.hcmuaf.fit.service.OrderSignatureService;

import java.util.List;

/**
 * AuditService — Person C
 *
 * Đây là tầng service trung tâm của Person C.
 * Đã gộp với code thật của B (OrderSignatureService) để hoàn thành luồng:
 *
 *   1. beforeAdminEdit()  — kiểm tra chữ ký trước khi admin ghi đè, chặn nếu lệch hash
 *   2. scanRevokedKey()   — khi A revoke key, quét các đơn ký bằng key đó, tạo alert
 *   3. sendAlertEmail()   — gửi mail tới admin khi có cảnh báo critical
 *
 * Dùng từ OrderSignatureService (B):
 *    .isSigned(int orderId)            → boolean (đã ký hay chưa)
 *    .verify(int orderId)              → String ("verified"/"tampered"/"unsigned"/"error")
 *    .getStoredHash(int orderId)       → String (hash đang lưu)
 *    .getOrdersSignedByKey(int keyId)  → List<Integer> (danh sách orderId)
 *    .invalidateSignature(int orderId) → boolean (đánh dấu need_reconfirm)
 *
 * Từ A vẫn còn STUB (KeyService.getInstance().getAdminEmail() chưa có):
 *    stubGetAdminEmail() → String (email admin nhận cảnh báo, fallback hardcode)
 */
public class AuditService {

    private static volatile AuditService instance;
    public static AuditService getInstance() {
        if (instance == null) {
            synchronized (AuditService.class) {
                if (instance == null) instance = new AuditService();
            }
        }
        return instance;
    }

    private final AuditDAO         auditDAO   = AuditDAO.getInstance();
    private final OrderEditRequestDAO editDAO = OrderEditRequestDAO.getInstance();

    /**
     * Gọi TRƯỚC khi AdminOrderController lưu thay đổi.
     *
     * Nếu đơn chưa có chữ ký → cho phép sửa bình thường (return ALLOW).
     * Nếu đơn đã ký và hash khớp → tạo edit_request, CHẶN sửa trực tiếp (return BLOCKED).
     * Nếu đơn đã ký nhưng hash lệch → log + alert critical + CHẶN (return TAMPERED).
     *
     * @param orderId      đơn cần sửa
     * @param actorAccount AccountID của admin đang thao tác
     * @param newStatus    trạng thái muốn chuyển sang
     * @return EditCheckResult với trạng thái và thông báo lỗi nếu có
     */
    public EditCheckResult beforeAdminEdit(int orderId, int actorAccount, String newStatus) {

        boolean isSigned = OrderSignatureService.getInstance().isSigned(orderId);

        if (!isSigned) {
            // Đơn chưa ký, cho phép sửa, log lại hành động
            Order order = OrderDAO.getInstance().getById(orderId);
            String oldStatus = (order != null) ? order.getStatus() : "unknown";
            auditDAO.logAction(orderId, "status_change", actorAccount,
                    oldStatus, newStatus, null, null,
                    "Đơn chưa ký — admin cập nhật trực tiếp");
            return EditCheckResult.allow();
        }

        // verify() trả về String trạng thái ("verified" | "tampered" | "unsigned" | "error")
        String verifyResult = OrderSignatureService.getInstance().verify(orderId);
        boolean hashOk = "verified".equals(verifyResult);
        String storedHash = OrderSignatureService.getInstance().getStoredHash(orderId);

        if (!hashOk) {
            // Hash lệch → đơn có thể bị can thiệp
            auditDAO.logAction(orderId, "signature_mismatch", actorAccount,
                    storedHash, "MISMATCH", storedHash, null,
                    "Hash chữ ký không khớp — chặn thao tác");

            int alertId = auditDAO.createAlert(
                    "signature_mismatch", "critical", orderId,
                    "Đơn DH" + orderId + " có hash chữ ký không khớp. Admin " +
                            actorAccount + " vừa cố sửa trạng thái. Cần kiểm tra ngay.");

            sendAlertEmailToAdmin(orderId, "Cảnh báo: Chữ ký đơn hàng DH" + orderId + " không hợp lệ",
                    "Hash không khớp khi admin (AccountID=" + actorAccount +
                            ") cố cập nhật trạng thái đơn DH" + orderId + ". Vui lòng kiểm tra ngay.");

            return EditCheckResult.tampered(
                    "Không thể sửa: chữ ký đơn hàng DH" + orderId +
                            " không hợp lệ. Cảnh báo đã được ghi nhận và gửi tới quản trị viên.");
        }

        // Hash khớp nhưng đơn đã ký → tạo edit_request, chờ xác nhận
        Order order = OrderDAO.getInstance().getById(orderId);
        String oldStatus = (order != null) ? order.getStatus() : "unknown";

        // Tránh tạo duplicate request
        if (editDAO.hasPendingRequest(orderId)) {
            return EditCheckResult.blocked(
                    "Đơn DH" + orderId + " đang có yêu cầu chỉnh sửa chờ duyệt. " +
                            "Vui lòng đợi yêu cầu hiện tại được xử lý.");
        }

        String summary = String.format("Admin (AccountID=%d) muốn đổi trạng thái từ '%s' → '%s'",
                actorAccount, oldStatus, newStatus);
        int reqId = editDAO.create(orderId, actorAccount, summary, oldStatus, newStatus);

        auditDAO.logAction(orderId, "edit_blocked", actorAccount,
                oldStatus, newStatus, storedHash, storedHash,
                "Đơn đã ký — tạo edit_request #" + reqId);

        auditDAO.createAlert("edit_blocked", "warning", orderId,
                "Yêu cầu sửa đơn DH" + orderId + " (#" + reqId + ") đang chờ duyệt.");

        return EditCheckResult.blocked(
                "Đơn DH" + orderId + " đã được ký xác nhận. " +
                        "Yêu cầu chỉnh sửa #" + reqId + " đã được tạo và cần xác nhận lại.");
    }

    /**
     * Khi super-admin duyệt edit_request:
     *   - Áp dụng trạng thái mới vào bảng orders
     *   - Invalidate chữ ký (yêu cầu ký lại)
     *   - Ghi log
     */
    public boolean applyApprovedEdit(int requestId, int reviewedBy) {
        var req = editDAO.getById(requestId);
        if (req == null || !"pending".equals(req.getStatus())) return false;

        boolean approved = editDAO.approve(requestId, reviewedBy);
        if (!approved) return false;

        // Áp dụng thay đổi trạng thái
        OrderDAO.getInstance().updateStatus(req.getOrderId(), req.getNewStatus());

        // Invalidate chữ ký — đơn vừa đổi trạng thái cần được ký lại
        OrderSignatureService.getInstance().invalidateSignature(req.getOrderId());

        auditDAO.logAction(req.getOrderId(), "status_change", reviewedBy,
                req.getOldStatus(), req.getNewStatus(), null, null,
                "Edit request #" + requestId + " được duyệt — chữ ký cần ký lại");

        return true;
    }

    /** Từ chối yêu cầu */
    public boolean rejectEdit(int requestId, int reviewedBy, String reason) {
        boolean ok = editDAO.reject(requestId, reviewedBy, reason);
        if (ok) {
            var req = editDAO.getById(requestId);
            if (req != null) {
                auditDAO.logAction(req.getOrderId(), "edit_blocked", reviewedBy,
                        null, null, null, null,
                        "Edit request #" + requestId + " bị từ chối: " + reason);
            }
        }
        return ok;
    }

    /**
     * Khi Person A revoke một key, Person C:
     *   - Lấy danh sách các đơn đã được ký bằng key đó (stub từ B)
     *   - Đánh dấu chúng là "cần xem lại" (invalidate signature)
     *   - Tạo alert cho admin
     *   - Gửi email cảnh báo
     *
     * @param keyId    ID của key vừa bị revoke (từ bảng user_keys của A)
     * @param revoker  AccountID của người revoke
     */
    public void onKeyRevoked(int keyId, int revoker) {
        List<Integer> affectedOrders = OrderSignatureService.getInstance().getOrdersSignedByKey(keyId);

        if (affectedOrders.isEmpty()) {
            auditDAO.logAction(-1, "key_revoke_scan", revoker,
                    "keyId=" + keyId, "no_affected_orders", null, null,
                    "Key #" + keyId + " bị revoke nhưng không có đơn nào liên quan");
            return;
        }

        for (int orderId : affectedOrders) {
            OrderSignatureService.getInstance().invalidateSignature(orderId);

            auditDAO.logAction(orderId, "key_revoke_scan", revoker,
                    "key_id=" + keyId, "signature_invalidated", null, null,
                    "Key #" + keyId + " bị revoke — chữ ký đơn này cần xem lại");

            auditDAO.createAlert("key_revoked_orders", "critical", orderId,
                    "Key #" + keyId + " bị thu hồi. Đơn DH" + orderId +
                            " đã được ký bằng key này — cần xem lại và ký lại.");
        }

        // Gửi 1 email tổng hợp
        String subject = "[Cảnh báo] Key #" + keyId + " bị thu hồi — " +
                affectedOrders.size() + " đơn cần xem lại";
        String body = "<h3>Thông báo thu hồi khóa ký</h3>" +
                "<p>Key <strong>#" + keyId + "</strong> vừa bị thu hồi bởi AccountID " + revoker + ".</p>" +
                "<p>Các đơn hàng sau đây đã được ký bằng key này và cần được xem lại:</p>" +
                "<ul>" +
                affectedOrders.stream()
                        .map(id -> "<li>Đơn <strong>DH" + id + "</strong></li>")
                        .reduce("", String::concat) +
                "</ul>" +
                "<p>Vui lòng kiểm tra trang <a href='/admin/alerts'>Admin → Cảnh báo</a>.</p>";

        sendAlertEmailToAdmin(-1, subject, body);
    }

    /**
     * Gửi email đến địa chỉ admin đã cấu hình.
     * Admin email lấy từ KeyService của A (hoặc fallback về hardcode).
     */
    private void sendAlertEmailToAdmin(int orderId, String subject, String htmlBody) {
        // ── STUB: thay bằng KeyService.getInstance().getAdminEmail() ──
        String adminEmail = stubGetAdminEmail();

        try {
            EmailUtils.sendEmail(adminEmail, subject, htmlBody);
        } catch (Exception e) {
            // Email không ảnh hưởng luồng chính
            System.err.println("[AuditService] Không gửi được email cảnh báo: " + e.getMessage());
        }
    }

    /** Gửi email cảnh báo dùng KeyService.getInstance().getAdminEmail(), fallback hardcode nếu A chưa có hàm */
    private String stubGetAdminEmail() {
        // return KeyService.getInstance().getAdminEmail();
        return "23130130@st.hcmuaf.edu.vn"; // fallback
    }

    public static class EditCheckResult {
        public enum Decision { ALLOW, BLOCKED, TAMPERED }

        private final Decision decision;
        private final String message;

        private EditCheckResult(Decision d, String msg) {
            this.decision = d;
            this.message  = msg;
        }

        public static EditCheckResult allow()                  { return new EditCheckResult(Decision.ALLOW, null); }
        public static EditCheckResult blocked(String msg)      { return new EditCheckResult(Decision.BLOCKED, msg); }
        public static EditCheckResult tampered(String msg)     { return new EditCheckResult(Decision.TAMPERED, msg); }

        public boolean isAllowed()   { return decision == Decision.ALLOW; }
        public boolean isBlocked()   { return decision == Decision.BLOCKED || decision == Decision.TAMPERED; }
        public boolean isTampered()  { return decision == Decision.TAMPERED; }
        public String getMessage()   { return message; }
        public Decision getDecision(){ return decision; }
    }
}