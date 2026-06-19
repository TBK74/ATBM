package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.AdminAlert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AuditDAO — Person C
 * Ghi order_audit_log và admin_alerts.
 * Không phụ thuộc vào code A hoặc B ở tầng DAO.
 */
public class AuditDAO {

    private static volatile AuditDAO instance;
    public static AuditDAO getInstance() {
        if (instance == null) {
            synchronized (AuditDAO.class) {
                if (instance == null) instance = new AuditDAO();
            }
        }
        return instance;
    }

    /**
     * Ghi một dòng vào order_audit_log.
     *
     * @param orderId       ID đơn hàng
     * @param action        tên hành động (status_change / edit_attempt / signature_mismatch / key_revoke_scan)
     * @param actorAccount  AccountID của người thực hiện (null = hệ thống tự động)
     * @param oldValue      giá trị cũ (JSON string hoặc text)
     * @param newValue      giá trị mới
     * @param hashBefore    hash chữ ký trước khi thao tác (từ B)
     * @param hashAfter     hash chữ ký sau khi thao tác (từ B)
     * @param note          ghi chú thêm
     */
    public void logAction(int orderId, String action, Integer actorAccount,
                          String oldValue, String newValue,
                          String hashBefore, String hashAfter, String note) {
        String sql = "INSERT INTO order_audit_log " +
                "(order_id, action, actor_account, old_value, new_value, hash_before, hash_after, note) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, action);
            if (actorAccount != null) ps.setInt(3, actorAccount);
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, oldValue);
            ps.setString(5, newValue);
            ps.setString(6, hashBefore);
            ps.setString(7, hashAfter);
            ps.setString(8, note);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tạo một cảnh báo mới cho admin.
     *
     * @param alertType  loại cảnh báo
     * @param severity   info | warning | critical
     * @param orderId    ID đơn liên quan (null nếu không áp dụng)
     * @param message    nội dung cảnh báo
     * @return alert_id vừa tạo, hoặc -1 nếu lỗi
     */
    public int createAlert(String alertType, String severity, Integer orderId, String message) {
        String sql = "INSERT INTO admin_alerts (alert_type, severity, order_id, message) VALUES (?,?,?,?)";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, alertType);
            ps.setString(2, severity);
            if (orderId != null) ps.setInt(3, orderId);
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, message);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Lấy tất cả cảnh báo chưa đọc, mới nhất trước */
    public List<AdminAlert> getUnread() {
        return fetchAlerts("WHERE is_read = 0 ORDER BY created_at DESC");
    }

    /** Lấy tất cả cảnh báo (cả đã đọc), dùng cho trang quản lý */
    public List<AdminAlert> getAll(int limit) {
        return fetchAlerts("ORDER BY created_at DESC LIMIT " + limit);
    }

    /** Đánh dấu đã đọc */
    public void markRead(int alertId, int resolvedBy) {
        String sql = "UPDATE admin_alerts SET is_read=1, resolved_at=NOW(), resolved_by=? WHERE alert_id=?";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolvedBy);
            ps.setInt(2, alertId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Đánh dấu tất cả đã đọc */
    public void markAllRead(int resolvedBy) {
        String sql = "UPDATE admin_alerts SET is_read=1, resolved_at=NOW(), resolved_by=? WHERE is_read=0";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolvedBy);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Đếm số cảnh báo chưa đọc (dùng cho badge trên sidebar) */
    public int countUnread() {
        String sql = "SELECT COUNT(*) FROM admin_alerts WHERE is_read = 0";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private List<AdminAlert> fetchAlerts(String whereClause) {
        List<AdminAlert> list = new ArrayList<>();
        String sql = "SELECT * FROM admin_alerts " + whereClause;
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AdminAlert a = new AdminAlert();
                a.setAlertId(rs.getInt("alert_id"));
                a.setAlertType(rs.getString("alert_type"));
                a.setSeverity(rs.getString("severity"));
                int oid = rs.getInt("order_id");
                if (!rs.wasNull()) a.setOrderId(oid);
                a.setMessage(rs.getString("message"));
                a.setRead(rs.getBoolean("is_read"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                a.setResolvedAt(rs.getTimestamp("resolved_at"));
                int rb = rs.getInt("resolved_by");
                if (!rs.wasNull()) a.setResolvedBy(rb);
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}