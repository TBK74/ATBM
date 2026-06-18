package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private static volatile OrderDAO instance;
    public static OrderDAO getInstance() {
        if (instance == null) { synchronized (OrderDAO.class) { if (instance == null) instance = new OrderDAO(); } }
        return instance;
    }

    public int createOrder(Order order, Cart cart) {
        int orderId = -1;
        try (Connection conn = DBConnect.get()) {
            if (conn == null) return -1;
            conn.setAutoCommit(false);
            try {
                String sqlOrder = "INSERT INTO orders (CustomerID, TotalAmount, Status, PaymentMethod, RecipientName, PromoCode, DiscountAmount) VALUES (?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, order.getCustomerId());
                    ps.setDouble(2, order.getTotalAmount());
                    ps.setString(3, order.getStatus());
                    ps.setString(4, order.getPaymentMethod());
                    ps.setString(5, order.getRecipientName());
                    ps.setString(6, order.getPromoCode());
                    ps.setDouble(7, order.getDiscountAmount());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) orderId = rs.getInt(1); }
                }

                String sqlItem = "INSERT INTO orderitems (OrderID, ItemType, ItemID, PriceAtOrder) VALUES (?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                    for (CartItem item : cart.getData().values()) {
                        ps.setInt(1, orderId);
                        ps.setString(2, item.getItemType());
                        ps.setInt(3, item.getItemId());
                        ps.setDouble(4, item.getPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); e.printStackTrace(); return -1;
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { e.printStackTrace(); }
        return orderId;
    }

    public Order getById(int id) {
        String sql = "SELECT * FROM orders WHERE OrderID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapOrder(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Order> getByCustomerId(int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE CustomerID = ? ORDER BY OrderDate DESC";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapOrder(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<OrderItem> getItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, " +
                     "CASE WHEN oi.ItemType='course' THEN c.Title ELSE d.Title END AS ItemTitle, " +
                     "CASE WHEN oi.ItemType='course' THEN c.ThumbnailUrl ELSE d.ThumbnailUrl END AS ItemThumbnail " +
                     "FROM orderitems oi " +
                     "LEFT JOIN courses c ON oi.ItemType='course' AND oi.ItemID=c.CourseID " +
                     "LEFT JOIN documents d ON oi.ItemType='document' AND oi.ItemID=d.DocumentID " +
                     "WHERE oi.OrderID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("OrderItemID"));
                    item.setOrderId(rs.getInt("OrderID"));
                    item.setItemType(rs.getString("ItemType"));
                    item.setItemId(rs.getInt("ItemID"));
                    item.setPriceAtOrder(rs.getDouble("PriceAtOrder"));
                    item.setItemTitle(rs.getString("ItemTitle"));
                    item.setItemThumbnail(rs.getString("ItemThumbnail"));
                    list.add(item);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY OrderDate DESC";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapOrder(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Order> filter(String q, String status, String dateFrom, String dateTo, String priceMin, String priceMax) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT o.* FROM orders o WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append("AND (o.RecipientName LIKE ? OR o.OrderID = ?) ");
            params.add("%" + q + "%");
            try { params.add(Integer.parseInt(q.trim())); } catch (Exception e) { params.add(-1); }
        }
        if (status != null && !status.isBlank()) { sql.append("AND o.Status = ? "); params.add(status); }
        if (dateFrom != null && !dateFrom.isBlank()) { sql.append("AND o.OrderDate >= ? "); params.add(dateFrom + " 00:00:00"); }
        if (dateTo != null && !dateTo.isBlank()) { sql.append("AND o.OrderDate <= ? "); params.add(dateTo + " 23:59:59"); }
        if (priceMin != null && !priceMin.isBlank()) { sql.append("AND o.TotalAmount >= ? "); params.add(Double.parseDouble(priceMin)); }
        if (priceMax != null && !priceMax.isBlank()) { sql.append("AND o.TotalAmount <= ? "); params.add(Double.parseDouble(priceMax)); }
        sql.append("ORDER BY o.OrderDate DESC");

        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i+1, (Integer)p);
                else if (p instanceof Double) ps.setDouble(i+1, (Double)p);
                else ps.setString(i+1, (String)p);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapOrder(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET Status=? WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, orderId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean cancelOrder(int orderId) {
        String sql = "UPDATE orders SET Status='Cancelled' WHERE OrderID=? AND Status='Pending'";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean isOwned(int orderId, int customerId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE OrderID=? AND CustomerID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId); ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int countTotal() {
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM orders");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order(rs.getInt("OrderID"), rs.getInt("CustomerID"),
                rs.getTimestamp("OrderDate"), rs.getDouble("TotalAmount"),
                rs.getString("Status"), rs.getString("PaymentMethod"), rs.getString("RecipientName"));
        o.setPromoCode(rs.getString("PromoCode"));
        o.setDiscountAmount(rs.getDouble("DiscountAmount"));
        return o;
    }
}
