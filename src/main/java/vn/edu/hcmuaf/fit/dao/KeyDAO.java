package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.UserKey;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KeyDAO {
    private static volatile KeyDAO instance;
    public static KeyDAO getInstance() {
        if (instance == null) { synchronized (KeyDAO.class) { if (instance == null) instance = new KeyDAO(); } }
        return instance;
    }

    /** Lưu public key mới cho tài khoản, tự động thu hồi key active cũ */
    public int saveKey(int accountId, String publicKeyPem, String algorithm) {
        int newKeyId = -1;
        try (Connection conn = DBConnect.get()) {
            if (conn == null) return -1;
            conn.setAutoCommit(false);
            try {
                // Thu hồi key active cũ
                String revoke = "UPDATE user_keys SET Status='revoked', RevokedAt=NOW() WHERE AccountID=? AND Status='active'";
                try (PreparedStatement ps = conn.prepareStatement(revoke)) {
                    ps.setInt(1, accountId);
                    ps.executeUpdate();
                }
                // Thêm key mới
                String insert = "INSERT INTO user_keys (AccountID, PublicKey, Algorithm, Status) VALUES (?,?,?,'active')";
                try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, accountId);
                    ps.setString(2, publicKeyPem);
                    ps.setString(3, algorithm != null ? algorithm : "RSA-2048");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) newKeyId = rs.getInt(1); }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); throw e;
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { e.printStackTrace(); }
        return newKeyId;
    }

    /** Thu hồi key và lưu thời điểm user khai bị lộ */
    public boolean revokeKey(int keyId, Timestamp suspectedAt) {
        String sql = "UPDATE user_keys SET Status='revoked', RevokedAt=NOW(), SuspectedAt=? WHERE KeyID=? AND Status='active'";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, suspectedAt);
            ps.setInt(2, keyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lấy key active hiện tại của tài khoản (để verify chữ ký MỚI) */
    public UserKey getActiveKey(int accountId) {
        String sql = "SELECT * FROM user_keys WHERE AccountID=? AND Status='active' ORDER BY CreatedAt DESC LIMIT 1";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lấy key theo ID (dùng khi verify đơn hàng cũ đã ký bằng key cụ thể) */
    public UserKey getById(int keyId) {
        String sql = "SELECT * FROM user_keys WHERE KeyID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, keyId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Toàn bộ lịch sử key của tài khoản */
    public List<UserKey> getByAccount(int accountId) {
        List<UserKey> list = new ArrayList<>();
        String sql = "SELECT * FROM user_keys WHERE AccountID=? ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Kiểm tra: tại thời điểm `signedAt`, key `keyId` có đang hợp lệ không?
     * Quy tắc: key phải đang active HOẶC (đã revoke nhưng signedAt < suspectedAt).
     * Expose cho Person B dùng trong OrderSignatureService.verify().
     */
    public boolean isKeyValidAt(int keyId, Timestamp signedAt) {
        if (signedAt == null) return false;
        String sql = "SELECT Status, SuspectedAt, RevokedAt FROM user_keys WHERE KeyID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, keyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String status = rs.getString("Status");
                if ("active".equals(status)) return true;
                Timestamp suspected = rs.getTimestamp("SuspectedAt");
                if (suspected == null) suspected = rs.getTimestamp("RevokedAt");
                return suspected != null && signedAt.before(suspected);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private UserKey map(ResultSet rs) throws SQLException {
        UserKey k = new UserKey();
        k.setKeyId(rs.getInt("KeyID"));
        k.setAccountId(rs.getInt("AccountID"));
        k.setPublicKey(rs.getString("PublicKey"));
        k.setAlgorithm(rs.getString("Algorithm"));
        k.setStatus(rs.getString("Status"));
        k.setCreatedAt(rs.getTimestamp("CreatedAt"));
        k.setRevokedAt(rs.getTimestamp("RevokedAt"));
        k.setSuspectedAt(rs.getTimestamp("SuspectedAt"));
        return k;
    }
}