package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Course;
import vn.edu.hcmuaf.fit.util.HtmlSanitizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final CourseDAO instance = new CourseDAO();
    private CourseDAO() {}
    public static CourseDAO getInstance() { return instance; }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("CourseID"));
        c.setTitle(rs.getString("Title"));
        c.setThumbnailUrl(rs.getString("ThumbnailUrl"));
        c.setShortDesc(rs.getString("ShortDesc"));
        c.setPrice(rs.getDouble("Price"));
        c.setOldPrice(rs.getDouble("OldPrice"));
        c.setLevel(rs.getString("Level"));
        c.setLanguage(rs.getString("Language"));
        c.setDurationHours(rs.getDouble("DurationHours"));
        c.setRating(rs.getDouble("Rating"));
        c.setReviewCount(rs.getInt("ReviewCount"));
        c.setSoldCount(rs.getInt("SoldCount"));
        c.setCategoryId(rs.getInt("CategoryID"));
        c.setBadge(rs.getString("Badge"));
        c.setActive(rs.getBoolean("IsActive"));
        try { c.setInstructor(rs.getString("InstructorName")); } catch (SQLException ignored) {}
        try { c.setCategoryName(rs.getString("CategoryName")); } catch (SQLException ignored) {}
        try { c.setDescription(rs.getString("Description")); } catch (SQLException ignored) {}
        try { c.setCreatedAt(rs.getTimestamp("CreatedAt")); } catch (SQLException ignored) {}
        return c;
    }

    public List<Course> getAll(Integer categoryId, String level, String priceRange, String sort, String search, int offset, int limit) {
        List<Course> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, cat.CategoryName, " +
                        "COALESCE(cu.FullName, a.Username) AS InstructorName " +
                        "FROM courses c " +
                        "LEFT JOIN categories cat ON c.CategoryID = cat.CategoryID " +
                        "LEFT JOIN accounts a ON c.InstructorID = a.AccountID " +
                        "LEFT JOIN customers cu ON a.AccountID = cu.AccountID " +
                        "WHERE c.IsActive = 1 ");

        List<Object> params = new ArrayList<>();
        if (categoryId != null) { sql.append("AND c.CategoryID = ? "); params.add(categoryId); }
        if (search != null && !search.isBlank()) { sql.append("AND c.Title LIKE ? "); params.add("%" + search + "%"); }
        if (level != null && !level.isBlank()) { sql.append("AND c.Level = ? "); params.add(level); }
        if (priceRange != null) {
            switch (priceRange) {
                case "free":  sql.append("AND c.Price = 0 "); break;
                case "p1":    sql.append("AND c.Price > 0 AND c.Price < 200000 "); break;
                case "p2":    sql.append("AND c.Price >= 200000 AND c.Price < 500000 "); break;
                case "p3":    sql.append("AND c.Price >= 500000 "); break;
            }
        }
        if (sort != null) {
            switch (sort) {
                case "priceAsc":  sql.append("ORDER BY c.Price ASC "); break;
                case "priceDesc": sql.append("ORDER BY c.Price DESC "); break;
                case "newest":    sql.append("ORDER BY c.CreatedAt DESC "); break;
                case "best":      sql.append("ORDER BY c.SoldCount DESC "); break;
                default:          sql.append("ORDER BY c.CourseID DESC ");
            }
        } else { sql.append("ORDER BY c.SoldCount DESC "); }
        sql.append("LIMIT ? OFFSET ?");
        params.add(limit); params.add(offset);

        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) ps.setInt(i+1, (Integer)params.get(i));
                else if (params.get(i) instanceof String) ps.setString(i+1, (String)params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int count(Integer categoryId, String level, String priceRange, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM courses c WHERE c.IsActive = 1 ");
        List<Object> params = new ArrayList<>();
        if (categoryId != null) { sql.append("AND c.CategoryID = ? "); params.add(categoryId); }
        if (search != null && !search.isBlank()) { sql.append("AND c.Title LIKE ? "); params.add("%" + search + "%"); }
        if (level != null && !level.isBlank()) { sql.append("AND c.Level = ? "); params.add(level); }
        try (Connection conn = DBConnect.get();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) ps.setInt(i+1, (Integer)params.get(i));
                else ps.setString(i+1, (String)params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Course getById(int id) {
        String sql = "SELECT c.*, cat.CategoryName, " +
                "COALESCE(cu.FullName, a.Username) AS InstructorName " +
                "FROM courses c " +
                "LEFT JOIN categories cat ON c.CategoryID = cat.CategoryID " +
                "LEFT JOIN accounts a ON c.InstructorID = a.AccountID " +
                "LEFT JOIN customers cu ON a.AccountID = cu.AccountID " +
                "WHERE c.CourseID = ? AND c.IsActive = 1";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Course> getFeatured(int limit) {
        return getAll(null, null, null, "best", null, 0, limit);
    }

    public List<Course> getRelated(int courseId, int categoryId, int limit) {
        String sql = "SELECT c.*, cat.CategoryName, COALESCE(cu.FullName, a.Username) AS InstructorName " +
                "FROM courses c LEFT JOIN categories cat ON c.CategoryID = cat.CategoryID " +
                "LEFT JOIN accounts a ON c.InstructorID = a.AccountID " +
                "LEFT JOIN customers cu ON a.AccountID = cu.AccountID " +
                "WHERE c.CategoryID = ? AND c.CourseID != ? AND c.IsActive = 1 " +
                "ORDER BY c.SoldCount DESC LIMIT ?";
        List<Course> list = new ArrayList<>();
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId); ps.setInt(2, courseId); ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Course c) {
        String sql = "INSERT INTO courses (Title, InstructorID, CategoryID, Price, OldPrice, ThumbnailUrl, ShortDesc, Description, Level, Language, DurationHours, Badge, IsActive, CreatedAt) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,1,NOW())";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitle()); ps.setInt(2, c.getInstructorId());
            ps.setInt(3, c.getCategoryId()); ps.setDouble(4, c.getPrice());
            ps.setDouble(5, c.getOldPrice()); ps.setString(6, c.getThumbnailUrl());
            ps.setString(7, c.getShortDesc()); ps.setString(8, HtmlSanitizer.sanitize(c.getDescription()));
            ps.setString(9, c.getLevel()); ps.setString(10, c.getLanguage());
            ps.setDouble(11, c.getDurationHours()); ps.setString(12, c.getBadge());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Course c) {
        String sql = "UPDATE courses SET Title=?, CategoryID=?, Price=?, OldPrice=?, ThumbnailUrl=?, ShortDesc=?, Description=?, Level=?, Language=?, DurationHours=?, Badge=?, InstructorID=? WHERE CourseID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitle()); ps.setInt(2, c.getCategoryId());
            ps.setDouble(3, c.getPrice()); ps.setDouble(4, c.getOldPrice());
            ps.setString(5, c.getThumbnailUrl()); ps.setString(6, c.getShortDesc());
            ps.setString(7, HtmlSanitizer.sanitize(c.getDescription())); ps.setString(8, c.getLevel());
            ps.setString(9, c.getLanguage()); ps.setDouble(10, c.getDurationHours());
            ps.setString(11, c.getBadge()); ps.setInt(12, c.getInstructorId()); ps.setInt(13, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean softDelete(int id) {
        String sql = "UPDATE courses SET IsActive = 0 WHERE CourseID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM courses WHERE IsActive = 1";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Course> getAdminList(String search, Integer categoryId, String level, int offset, int limit) {
        List<Course> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, cat.CategoryName, COALESCE(cu.FullName, a.Username) AS InstructorName " +
                        "FROM courses c LEFT JOIN categories cat ON c.CategoryID=cat.CategoryID " +
                        "LEFT JOIN accounts a ON c.InstructorID=a.AccountID " +
                        "LEFT JOIN customers cu ON a.AccountID=cu.AccountID WHERE c.IsActive=1 ");
        List<Object> params = new ArrayList<>();
        if (search != null && !search.isBlank()) { sql.append("AND (c.Title LIKE ? OR c.CourseID = ?) "); params.add("%" + search + "%"); try { params.add(Integer.parseInt(search)); } catch (Exception e) { params.add(-1); } }
        if (categoryId != null) { sql.append("AND c.CategoryID = ? "); params.add(categoryId); }
        if (level != null && !level.isBlank()) { sql.append("AND c.Level = ? "); params.add(level); }
        sql.append("ORDER BY c.CourseID DESC LIMIT ? OFFSET ?");
        params.add(limit); params.add(offset);
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i+1, (Integer)p);
                else ps.setString(i+1, (String)p);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}