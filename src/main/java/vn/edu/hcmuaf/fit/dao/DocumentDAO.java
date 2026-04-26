package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.Document;
import vn.edu.hcmuaf.fit.util.HtmlSanitizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {
    private static final DocumentDAO instance = new DocumentDAO();
    private DocumentDAO() {}
    public static DocumentDAO getInstance() { return instance; }

    private Document map(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setId(rs.getInt("DocumentID"));
        d.setTitle(rs.getString("Title"));
        d.setAuthor(rs.getString("Author"));
        d.setThumbnailUrl(rs.getString("ThumbnailUrl"));
        d.setPrice(rs.getDouble("Price"));
        d.setOldPrice(rs.getDouble("OldPrice"));
        d.setFileType(rs.getString("FileType"));
        d.setPageCount(rs.getInt("PageCount"));
        d.setFileSizeKb(rs.getInt("FileSizeKb"));
        d.setRating(rs.getDouble("Rating"));
        d.setReviewCount(rs.getInt("ReviewCount"));
        d.setSoldCount(rs.getInt("SoldCount"));
        d.setCategoryId(rs.getInt("CategoryID"));
        d.setBadge(rs.getString("Badge"));
        d.setActive(rs.getBoolean("IsActive"));
        try { d.setCategoryName(rs.getString("CategoryName")); } catch (SQLException ignored) {}
        try { d.setDescription(rs.getString("Description")); } catch (SQLException ignored) {}
        try { d.setCreatedAt(rs.getTimestamp("CreatedAt")); } catch (SQLException ignored) {}
        return d;
    }

    public List<Document> getAll(Integer categoryId, String fileType, String priceRange, String sort, String search, int offset, int limit) {
        List<Document> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT d.*, cat.CategoryName FROM documents d " +
            "LEFT JOIN categories cat ON d.CategoryID = cat.CategoryID " +
            "WHERE d.IsActive = 1 ");
        List<Object> params = new ArrayList<>();
        if (categoryId != null) { sql.append("AND d.CategoryID = ? "); params.add(categoryId); }
        if (search != null && !search.isBlank()) { sql.append("AND d.Title LIKE ? "); params.add("%" + search + "%"); }
        if (fileType != null && !fileType.isBlank()) { sql.append("AND d.FileType = ? "); params.add(fileType); }
        if (priceRange != null) {
            switch (priceRange) {
                case "free": sql.append("AND d.Price = 0 "); break;
                case "p1":   sql.append("AND d.Price > 0 AND d.Price < 50000 "); break;
                case "p2":   sql.append("AND d.Price >= 50000 AND d.Price < 150000 "); break;
                case "p3":   sql.append("AND d.Price >= 150000 "); break;
            }
        }
        if (sort != null) {
            switch (sort) {
                case "priceAsc":  sql.append("ORDER BY d.Price ASC "); break;
                case "priceDesc": sql.append("ORDER BY d.Price DESC "); break;
                case "newest":    sql.append("ORDER BY d.CreatedAt DESC "); break;
                case "best":      sql.append("ORDER BY d.SoldCount DESC "); break;
                default:          sql.append("ORDER BY d.DocumentID DESC ");
            }
        } else { sql.append("ORDER BY d.SoldCount DESC "); }
        sql.append("LIMIT ? OFFSET ?");
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

    public int count(Integer categoryId, String fileType, String priceRange, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM documents d WHERE d.IsActive = 1 ");
        List<Object> params = new ArrayList<>();
        if (categoryId != null) { sql.append("AND d.CategoryID = ? "); params.add(categoryId); }
        if (search != null && !search.isBlank()) { sql.append("AND d.Title LIKE ? "); params.add("%" + search + "%"); }
        if (fileType != null && !fileType.isBlank()) { sql.append("AND d.FileType = ? "); params.add(fileType); }
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i+1, (Integer)p);
                else ps.setString(i+1, (String)p);
            }
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Document getById(int id) {
        String sql = "SELECT d.*, cat.CategoryName FROM documents d " +
                     "LEFT JOIN categories cat ON d.CategoryID = cat.CategoryID " +
                     "WHERE d.DocumentID = ? AND d.IsActive = 1";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Document> getNewest(int limit) {
        return getAll(null, null, null, "newest", null, 0, limit);
    }

    public List<Document> getRelated(int docId, int categoryId, int limit) {
        String sql = "SELECT d.*, cat.CategoryName FROM documents d " +
                     "LEFT JOIN categories cat ON d.CategoryID = cat.CategoryID " +
                     "WHERE d.CategoryID = ? AND d.DocumentID != ? AND d.IsActive = 1 " +
                     "ORDER BY d.SoldCount DESC LIMIT ?";
        List<Document> list = new ArrayList<>();
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId); ps.setInt(2, docId); ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Document d) {
        String sql = "INSERT INTO documents (Title, Author, CategoryID, Price, OldPrice, ThumbnailUrl, Description, FileType, PageCount, FileSizeKb, Badge, IsActive, CreatedAt) VALUES (?,?,?,?,?,?,?,?,?,?,?,1,NOW())";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getTitle()); ps.setString(2, d.getAuthor());
            ps.setInt(3, d.getCategoryId()); ps.setDouble(4, d.getPrice());
            ps.setDouble(5, d.getOldPrice()); ps.setString(6, d.getThumbnailUrl());
            ps.setString(7, HtmlSanitizer.sanitize(d.getDescription())); ps.setString(8, d.getFileType());
            ps.setInt(9, d.getPageCount()); ps.setInt(10, d.getFileSizeKb()); ps.setString(11, d.getBadge());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Document d) {
        String sql = "UPDATE documents SET Title=?, Author=?, CategoryID=?, Price=?, OldPrice=?, ThumbnailUrl=?, Description=?, FileType=?, PageCount=?, FileSizeKb=?, Badge=? WHERE DocumentID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getTitle()); ps.setString(2, d.getAuthor());
            ps.setInt(3, d.getCategoryId()); ps.setDouble(4, d.getPrice());
            ps.setDouble(5, d.getOldPrice()); ps.setString(6, d.getThumbnailUrl());
            ps.setString(7, HtmlSanitizer.sanitize(d.getDescription())); ps.setString(8, d.getFileType());
            ps.setInt(9, d.getPageCount()); ps.setInt(10, d.getFileSizeKb());
            ps.setString(11, d.getBadge()); ps.setInt(12, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean softDelete(int id) {
        String sql = "UPDATE documents SET IsActive = 0 WHERE DocumentID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM documents WHERE IsActive = 1";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public String getFilePath(int documentId) {
        String sql = "SELECT FilePath FROM documents WHERE DocumentID = ?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, documentId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("FilePath"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
