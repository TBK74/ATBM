package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private static final EnrollmentDAO instance = new EnrollmentDAO();
    private EnrollmentDAO() {}
    public static EnrollmentDAO getInstance() { return instance; }

    public boolean enroll(int customerId, int courseId, int orderId) {
        if (isEnrolled(customerId, courseId)) return true;
        String sql = "INSERT INTO enrollments (CustomerID, CourseID, OrderID, EnrolledAt, ProgressPercent) VALUES (?,?,?,NOW(),0)";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, courseId); ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean isEnrolled(int customerId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments e " +
                "LEFT JOIN orders o ON e.OrderID = o.OrderID " +
                "WHERE e.CustomerID=? AND e.CourseID=? " +
                "AND (o.OrderID IS NULL OR o.Status != 'Cancelled')";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean revokeEnrollmentByOrder(int orderId) {
        String sql = "DELETE FROM enrollments WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Enrollment> getByCustomer(int customerId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.*, c.Title AS CourseTitle, c.ThumbnailUrl AS CourseThumbnail, " +
                     "COALESCE(cu.FullName, a.Username) AS InstructorName " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.CourseID = c.CourseID " +
                     "LEFT JOIN accounts a ON c.InstructorID = a.AccountID " +
                     "LEFT JOIN customers cu ON a.AccountID = cu.AccountID " +
                     "WHERE e.CustomerID = ? ORDER BY e.EnrolledAt DESC";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentId(rs.getInt("EnrollmentID"));
                    e.setCustomerId(rs.getInt("CustomerID"));
                    e.setCourseId(rs.getInt("CourseID"));
                    e.setOrderId(rs.getInt("OrderID"));
                    e.setEnrolledAt(rs.getTimestamp("EnrolledAt"));
                    e.setProgressPercent(rs.getInt("ProgressPercent"));
                    e.setCompletedAt(rs.getTimestamp("CompletedAt"));
                    e.setCourseTitle(rs.getString("CourseTitle"));
                    e.setCourseThumbnail(rs.getString("CourseThumbnail"));
                    e.setInstructorName(rs.getString("InstructorName"));
                    list.add(e);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateProgress(int customerId, int courseId, int percent) {
        String sql = percent >= 100
            ? "UPDATE enrollments SET ProgressPercent=100, CompletedAt=NOW() WHERE CustomerID=? AND CourseID=?"
            : "UPDATE enrollments SET ProgressPercent=? WHERE CustomerID=? AND CourseID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (percent >= 100) { ps.setInt(1, customerId); ps.setInt(2, courseId); }
            else { ps.setInt(1, percent); ps.setInt(2, customerId); ps.setInt(3, courseId); }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}