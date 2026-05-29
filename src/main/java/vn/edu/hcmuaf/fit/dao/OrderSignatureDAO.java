package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.OrderSignature;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderSignatureDAO {
    private static volatile OrderSignatureDAO instance;
    public static OrderSignatureDAO getInstance() {
        if (instance == null) {
            synchronized (OrderSignatureDAO.class) {
                if (instance == null) instance = new OrderSignatureDAO();
            }
        }
        return instance;
    }

    /** Tạo bản ghi signature ban đầu ngay sau khi đơn hàng được tạo (status = unsigned) */
    public boolean initSignature(int orderId, String dataHash, String canonicalJson) {
        String sql = "INSERT INTO order_signatures (OrderID, DataHash, CanonicalJson, SignStatus) " +
                "VALUES (?,?,?,'unsigned') " +
                "ON DUPLICATE KEY UPDATE DataHash=VALUES(DataHash), CanonicalJson=VALUES(CanonicalJson), " +
                "Signature=NULL, SigningKeyID=NULL, SignedAt=NULL, SignStatus='unsigned'";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, dataHash);
            ps.setString(3, canonicalJson);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lưu chữ ký do user upload lên (chuyển sang status "signed") */
    public boolean saveSignature(int orderId, String signature, int keyId) {
        String sql = "UPDATE order_signatures SET Signature=?, SigningKeyID=?, SignedAt=NOW(), " +
                "SignStatus='signed', LastCheckedAt=NOW() WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, signature);
            ps.setInt(2, keyId);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Cập nhật kết quả verify (verified / tampered) */
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE order_signatures SET SignStatus=?, LastCheckedAt=NOW() WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Invalidate chữ ký khi đơn hàng bị sửa (reset về unsigned + cập nhật hash mới).
     * Được gọi bởi Person C khi admin/user sửa đơn.
     */
    public boolean invalidate(int orderId, String newHash, String newCanonical) {
        String sql = "UPDATE order_signatures SET DataHash=?, CanonicalJson=?, " +
                "Signature=NULL, SigningKeyID=NULL, SignedAt=NULL, " +
                "SignStatus='unsigned', LastCheckedAt=NOW() WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, newCanonical);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Lấy thông tin signature của một đơn hàng */
    public OrderSignature getByOrderId(int orderId) {
        String sql = "SELECT * FROM order_signatures WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lấy toàn bộ signatures của danh sách đơn hàng (để tránh N+1 query trong list view) */
    public List<OrderSignature> getByOrderIds(List<Integer> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) return new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM order_signatures WHERE OrderID IN (");
        for (int i = 0; i < orderIds.size(); i++) { if (i > 0) sb.append(','); sb.append('?'); }
        sb.append(')');
        List<OrderSignature> list = new ArrayList<>();
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < orderIds.size(); i++) ps.setInt(i + 1, orderIds.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Lấy danh sách OrderID đã từng được ký bằng một key cụ thể (dùng khi key bị revoke) */
    public List<Integer> getOrderIdsBySigningKey(int keyId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT OrderID FROM order_signatures WHERE SigningKeyID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, keyId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(rs.getInt("OrderID")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private OrderSignature map(ResultSet rs) throws SQLException {
        OrderSignature s = new OrderSignature();
        s.setSigId(rs.getInt("SigID"));
        s.setOrderId(rs.getInt("OrderID"));
        s.setDataHash(rs.getString("DataHash"));
        s.setCanonicalJson(rs.getString("CanonicalJson"));
        s.setSignature(rs.getString("Signature"));
        int keyId = rs.getInt("SigningKeyID");
        if (!rs.wasNull()) s.setSigningKeyId(keyId);
        s.setSignedAt(rs.getTimestamp("SignedAt"));
        s.setSignStatus(rs.getString("SignStatus"));
        s.setLastCheckedAt(rs.getTimestamp("LastCheckedAt"));
        return s;
    }
}