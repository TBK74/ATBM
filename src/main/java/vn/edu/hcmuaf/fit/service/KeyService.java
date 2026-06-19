package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.KeyDAO;
import vn.edu.hcmuaf.fit.model.UserKey;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service quản lý khóa công khai người dùng.
 * Hai hàm expose ra ngoài để Person B dùng:
 *   - getActivePublicKey(accountId) → PEM string
 *   - isKeyValidAt(keyId, signedAt) → boolean
 */
public class KeyService {
    private static volatile KeyService instance;
    public static KeyService getInstance() {
        if (instance == null) { synchronized (KeyService.class) { if (instance == null) instance = new KeyService(); } }
        return instance;
    }

    private final KeyDAO dao = KeyDAO.getInstance();

    /** Sinh/lưu public key mới. Trả về KeyID mới, -1 nếu lỗi. */
    public int registerKey(int accountId, String publicKeyPem) {
        if (publicKeyPem == null || publicKeyPem.isBlank()) return -1;
        return dao.saveKey(accountId, publicKeyPem.trim(), "RSA-2048");
    }

    /**
     * Báo mất khóa: thu hồi key hiện tại, ghi nhận thời điểm nghi ngờ bị lộ.
     * @param keyId       ID của key cần thu hồi
     * @param suspectedAt Thời điểm user khai (có thể null → dùng NOW)
     */
    public boolean revokeKey(int keyId, Timestamp suspectedAt) {
        if (suspectedAt == null) suspectedAt = new Timestamp(System.currentTimeMillis());
        return dao.revokeKey(keyId, suspectedAt);
    }

    /** Lịch sử toàn bộ key của tài khoản (hiển thị trên trang quản lý khóa) */
    public List<UserKey> getKeyHistory(int accountId) {
        return dao.getByAccount(accountId);
    }

    /** Key active hiện tại của tài khoản (để hiển thị trên UI) */
    public UserKey getActiveKey(int accountId) {
        return dao.getActiveKey(accountId);
    }

    /**
     * Lấy PEM public key đang active của tài khoản.
     * Person B gọi để verify chữ ký mới nhất.
     * @return PEM string hoặc null nếu chưa có key
     */
    public String getActivePublicKey(int accountId) {
        UserKey k = dao.getActiveKey(accountId);
        return k != null ? k.getPublicKey() : null;
    }

    /**
     * Kiểm tra key `keyId` có hợp lệ tại thời điểm `signedAt` không.
     * Person B gọi trong quá trình verify đơn hàng cũ.
     */
    public boolean isKeyValidAt(int keyId, Timestamp signedAt) {
        return dao.isKeyValidAt(keyId, signedAt);
    }

    /**
     * Trạng thái key để UI badge đọc (expose thêm theo yêu cầu).
     * Trả về "active", "revoked", hoặc "none" nếu chưa có key.
     */
    public String getKeyStatus(int accountId) {
        UserKey k = dao.getActiveKey(accountId);
        if (k == null) {
            List<UserKey> history = dao.getByAccount(accountId);
            return history.isEmpty() ? "none" : "revoked";
        }
        return k.getStatus(); // "active"
    }
}