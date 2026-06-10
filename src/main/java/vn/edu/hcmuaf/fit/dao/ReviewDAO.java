package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Review;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private static volatile ReviewDAO instance;

    public static ReviewDAO getInstance() {
        if (instance == null) {
            synchronized (ReviewDAO.class) {
                if (instance == null) {
                    instance = new ReviewDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Lấy tất cả review của một tài liệu (document)
     */
    public List<Review> getReviewsByProductId(int productId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT r.*, c.FullName as CustomerName " +
                "FROM reviews r " +
                "JOIN customers c ON r.CustomerID = c.CustomerID " +
                "WHERE r.ItemType = 'document' AND r.ItemID = ? " +
                "ORDER BY r.CreatedAt DESC";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("ReviewID"),
                            rs.getInt("ItemID"),
                            rs.getInt("CustomerID"),
                            rs.getInt("Rating"),
                            rs.getString("Content"),
                            rs.getTimestamp("CreatedAt")
                    );
                    review.setCustomerName(rs.getString("CustomerName"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * Kiểm tra khách hàng đã mua sản phẩm này chưa
     * CHỈ cho phép đánh giá khi đơn hàng đã giao thành công (Completed)
     */
    public boolean hasCustomerPurchasedProduct(int customerId, int productId) {
        String query = "SELECT COUNT(*) FROM orders o " +
                "JOIN orderitems oi ON o.OrderID = oi.OrderID " +
                "WHERE o.CustomerID = ? AND oi.ItemType = 'document' AND oi.ItemID = ? " +
                "AND o.Status = 'Completed'";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra khách hàng đã đánh giá sản phẩm này chưa
     */
    public boolean hasCustomerReviewedProduct(int customerId, int productId) {
        String query = "SELECT COUNT(*) FROM reviews " +
                "WHERE CustomerID = ? AND ItemType = 'document' AND ItemID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm review mới
     */
    public boolean addReview(Review review) {
        if (review.getRating() == null) {
            // Cột Rating trong bảng reviews là NOT NULL — không cho insert thiếu rating
            return false;
        }
        String query = "INSERT INTO reviews (ItemType, ItemID, CustomerID, Rating, Content) " +
                "VALUES ('document', ?, ?, ?, ?)";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, review.getProductId());
            ps.setInt(2, review.getCustomerId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getContent());

            int result = ps.executeUpdate();

            // Cập nhật rating và review count của tài liệu
            if (result > 0) {
                updateProductRating(review.getProductId());
            }

            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật rating trung bình và số lượng review của sản phẩm
     */
    private void updateProductRating(int productId) {
        String query = "UPDATE documents SET " +
                "Rating = (SELECT AVG(Rating) FROM reviews WHERE ItemType='document' AND ItemID = ?), " +
                "ReviewCount = (SELECT COUNT(*) FROM reviews WHERE ItemType='document' AND ItemID = ?) " +
                "WHERE DocumentID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            ps.setInt(2, productId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa review (chỉ admin hoặc chính khách hàng đó)
     */
    public boolean deleteReview(int reviewId, int customerId) {
        String query = "DELETE FROM reviews WHERE ReviewID = ? AND CustomerID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, reviewId);
            ps.setInt(2, customerId);

            int result = ps.executeUpdate();

            // Cập nhật lại rating của sản phẩm
            if (result > 0) {
                Review review = getReviewById(reviewId);
                if (review != null) {
                    updateProductRating(review.getProductId());
                }
            }

            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy review theo ID
     */
    public Review getReviewById(int reviewId) {
        String query = "SELECT * FROM reviews WHERE ReviewID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Review(
                            rs.getInt("ReviewID"),
                            rs.getInt("ItemID"),
                            rs.getInt("CustomerID"),
                            rs.getInt("Rating"),
                            rs.getString("Content"),
                            rs.getTimestamp("CreatedAt")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả review của một khách hàng
     */
    public List<Review> getReviewsByCustomerId(int customerId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT r.*, d.Title as ProductName " +
                "FROM reviews r " +
                "JOIN documents d ON r.ItemType = 'document' AND r.ItemID = d.DocumentID " +
                "WHERE r.CustomerID = ? " +
                "ORDER BY r.CreatedAt DESC";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                            rs.getInt("ReviewID"),
                            rs.getInt("ItemID"),
                            rs.getInt("CustomerID"),
                            rs.getInt("Rating"),
                            rs.getString("Content"),
                            rs.getTimestamp("CreatedAt")
                    );
                    review.setProductName(rs.getString("ProductName"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    /**
     * Đếm số review của sản phẩm
     */
    public int countReviewsByProductId(int productId) {
        String query = "SELECT COUNT(*) FROM reviews WHERE ItemType = 'document' AND ItemID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy rating trung bình của sản phẩm
     */
    public double getAverageRating(int productId) {
        String query = "SELECT AVG(Rating) FROM reviews WHERE ItemType = 'document' AND ItemID = ?";

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}