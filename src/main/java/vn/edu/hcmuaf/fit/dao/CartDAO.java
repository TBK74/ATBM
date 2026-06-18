package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Cart;
import vn.edu.hcmuaf.fit.model.CartItem;

import java.sql.*;

public class CartDAO {
    private static final CartDAO instance = new CartDAO();
    private CartDAO() {}
    public static CartDAO getInstance() { return instance; }

    public int getOrCreateCartId(int customerId) {
        String sql = "SELECT CartID FROM carts WHERE CustomerID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("CartID");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        // create
        String ins = "INSERT INTO carts (CustomerID) VALUES (?)";
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public Cart getCartByCustomerId(int customerId) {
        Cart cart = new Cart();
        String sql = "SELECT ci.ItemType, ci.ItemID, ci.PriceAtAdd, " +
                     "CASE WHEN ci.ItemType='course' THEN c.Title ELSE d.Title END AS Title, " +
                     "CASE WHEN ci.ItemType='course' THEN c.ThumbnailUrl ELSE d.ThumbnailUrl END AS Thumbnail, " +
                     "CASE WHEN ci.ItemType='course' THEN c.OldPrice ELSE d.OldPrice END AS OldPrice " +
                     "FROM carts ca " +
                     "JOIN cart_items ci ON ca.CartID = ci.CartID " +
                     "LEFT JOIN courses c ON ci.ItemType='course' AND ci.ItemID = c.CourseID " +
                     "LEFT JOIN documents d ON ci.ItemType='document' AND ci.ItemID = d.DocumentID " +
                     "WHERE ca.CustomerID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setItemType(rs.getString("ItemType"));
                    item.setItemId(rs.getInt("ItemID"));
                    item.setTitle(rs.getString("Title"));
                    item.setThumbnailUrl(rs.getString("Thumbnail"));
                    item.setPrice(rs.getDouble("PriceAtAdd"));
                    item.setOldPrice(rs.getDouble("OldPrice"));
                    cart.getData().put(cart.buildKey(item.getItemType(), item.getItemId()), item);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cart;
    }

    public boolean addItem(int customerId, String itemType, int itemId, double price) {
        int cartId = getOrCreateCartId(customerId);
        if (cartId == -1) return false;
        // check duplicate
        String chk = "SELECT COUNT(*) FROM cart_items WHERE CartID=? AND ItemType=? AND ItemID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(chk)) {
            ps.setInt(1, cartId); ps.setString(2, itemType); ps.setInt(3, itemId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next() && rs.getInt(1) > 0) return false; }
        } catch (SQLException e) { e.printStackTrace(); }
        String sql = "INSERT INTO cart_items (CartID, ItemType, ItemID, PriceAtAdd) VALUES (?,?,?,?)";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId); ps.setString(2, itemType); ps.setInt(3, itemId); ps.setDouble(4, price);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean removeItem(int customerId, String itemType, int itemId) {
        String sql = "DELETE ci FROM cart_items ci JOIN carts ca ON ci.CartID=ca.CartID WHERE ca.CustomerID=? AND ci.ItemType=? AND ci.ItemID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setString(2, itemType); ps.setInt(3, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean clearCart(int customerId) {
        String sql = "DELETE ci FROM cart_items ci JOIN carts ca ON ci.CartID=ca.CartID WHERE ca.CustomerID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
