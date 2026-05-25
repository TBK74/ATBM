package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private Customer map(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("CustomerID"),
                rs.getInt("AccountID"),
                rs.getString("FullName"),
                rs.getString("Phone"),
                rs.getString("AvatarUrl"));
    }

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Customer getById(int id) {
        String query = "SELECT * FROM customers WHERE CustomerID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Cập nhật thông tin khách hàng. Chỉ cập nhật field nào được truyền vào (không null),
     * cho phép sửa từng trường riêng lẻ thay vì bắt buộc điền đủ tất cả.
     */
    public boolean updateCustomerInfo(int accountId, String fullName, String phone) {
        String query = "UPDATE customers SET FullName = ?, Phone = ? WHERE AccountID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setInt(3, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateAvatar(int accountId, String avatarUrl) {
        String query = "UPDATE customers SET AvatarUrl = ? WHERE AccountID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, avatarUrl);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Customer getCustomerByAccountId(int accountId) {
        String query = "SELECT * FROM customers WHERE AccountID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean createCustomerRecord(int accountId) {
        String query = "INSERT IGNORE INTO customers (AccountID) VALUES (?)";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}