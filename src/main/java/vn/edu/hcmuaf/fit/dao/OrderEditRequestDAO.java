package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.OrderEditRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderEditRequestDAO — Person C
 * Quản lý bảng order_edit_requests:
 *   - Tạo yêu cầu khi admin muốn sửa đơn đã có chữ ký
 *   - Duyệt / Từ chối yêu cầu
 */
public class OrderEditRequestDAO {

    private static volatile OrderEditRequestDAO instance;
    public static OrderEditRequestDAO getInstance() {
        if (instance == null) {
            synchronized (OrderEditRequestDAO.class) {
                if (instance == null) instance = new OrderEditRequestDAO();
            }
        }
        return instance;
    }

    /** Tạo yêu cầu sửa mới */
    public int create(int orderId, int requestedBy, String changeSummary,
                      String oldStatus, String newStatus) {
        String sql = "INSERT INTO order_edit_requests " +
                "(order_id, requested_by, change_summary, old_status, new_status) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderId);
            ps.setInt(2, requestedBy);
            ps.setString(3, changeSummary);
            ps.setString(4, oldStatus);
            ps.setString(5, newStatus);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Duyệt yêu cầu */
    public boolean approve(int requestId, int reviewedBy) {
        return updateStatus(requestId, "approved", reviewedBy, null);
    }

    /** Từ chối yêu cầu */
    public boolean reject(int requestId, int reviewedBy, String reason) {
        return updateStatus(requestId, "rejected", reviewedBy, reason);
    }

    private boolean updateStatus(int requestId, String status, int reviewedBy, String reason) {
        String sql = "UPDATE order_edit_requests " +
                "SET status=?, reviewed_at=NOW(), reviewed_by=?, reject_reason=? " +
                "WHERE request_id=? AND status='pending'";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewedBy);
            ps.setString(3, reason);
            ps.setInt(4, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Lấy yêu cầu theo ID (dùng khi cần áp dụng thay đổi sau khi approve) */
    public OrderEditRequest getById(int requestId) {
        String sql = "SELECT r.*, " +
                "  a1.Username AS requested_by_username, " +
                "  a2.Username AS reviewed_by_username " +
                "FROM order_edit_requests r " +
                "LEFT JOIN accounts a1 ON r.requested_by = a1.AccountID " +
                "LEFT JOIN accounts a2 ON r.reviewed_by  = a2.AccountID " +
                "WHERE r.request_id = ?";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lấy danh sách yêu cầu pending cho 1 đơn */
    public List<OrderEditRequest> getPendingByOrder(int orderId) {
        return fetchList("WHERE r.order_id = ? AND r.status = 'pending'", orderId);
    }

    /** Lấy tất cả yêu cầu (cho trang admin) */
    public List<OrderEditRequest> getAll() {
        return fetchList("ORDER BY r.created_at DESC", null);
    }

    /** Lấy yêu cầu pending để admin xem xét */
    public List<OrderEditRequest> getAllPending() {
        return fetchList("WHERE r.status = 'pending' ORDER BY r.created_at ASC", null);
    }

    /** Kiểm tra đơn đang có yêu cầu pending không */
    public boolean hasPendingRequest(int orderId) {
        String sql = "SELECT 1 FROM order_edit_requests WHERE order_id=? AND status='pending' LIMIT 1";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private List<OrderEditRequest> fetchList(String whereClause, Integer paramInt) {
        List<OrderEditRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, " +
                "  a1.Username AS requested_by_username, " +
                "  a2.Username AS reviewed_by_username " +
                "FROM order_edit_requests r " +
                "LEFT JOIN accounts a1 ON r.requested_by = a1.AccountID " +
                "LEFT JOIN accounts a2 ON r.reviewed_by  = a2.AccountID " +
                whereClause;
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (paramInt != null) ps.setInt(1, paramInt);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private OrderEditRequest mapRow(ResultSet rs) throws SQLException {
        OrderEditRequest r = new OrderEditRequest();
        r.setRequestId(rs.getInt("request_id"));
        r.setOrderId(rs.getInt("order_id"));
        r.setRequestedBy(rs.getInt("requested_by"));
        r.setChangeSummary(rs.getString("change_summary"));
        r.setOldStatus(rs.getString("old_status"));
        r.setNewStatus(rs.getString("new_status"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setReviewedAt(rs.getTimestamp("reviewed_at"));
        int rb = rs.getInt("reviewed_by");
        if (!rs.wasNull()) r.setReviewedBy(rb);
        r.setRejectReason(rs.getString("reject_reason"));
        r.setRequestedByUsername(rs.getString("requested_by_username"));
        r.setReviewedByUsername(rs.getString("reviewed_by_username"));
        return r;
    }
}