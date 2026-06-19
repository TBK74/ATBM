package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.db.DBConnect;
import vn.edu.hcmuaf.fit.model.DocumentAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentAccessDAO {
    private static final DocumentAccessDAO instance = new DocumentAccessDAO();
    private DocumentAccessDAO() {}
    public static DocumentAccessDAO getInstance() { return instance; }

    public boolean grantAccess(int customerId, int documentId, int orderId) {
        if (hasAccess(customerId, documentId)) return true;
        String sql = "INSERT INTO document_access (CustomerID, DocumentID, OrderID, GrantedAt, DownloadCount, MaxDownloads) VALUES (?,?,?,NOW(),0,5)";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, documentId); ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean hasAccess(int customerId, int documentId) {
        String sql = "SELECT COUNT(*) FROM document_access da " +
                "LEFT JOIN orders o ON da.OrderID = o.OrderID " +
                "WHERE da.CustomerID=? AND da.DocumentID=? " +
                "AND (o.OrderID IS NULL OR o.Status != 'Cancelled')";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, documentId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean revokeAccessByOrder(int orderId) {
        String sql = "DELETE FROM document_access WHERE OrderID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean canDownload(int customerId, int documentId) {
        String sql = "SELECT DownloadCount, MaxDownloads FROM document_access WHERE CustomerID=? AND DocumentID=?";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("DownloadCount") < rs.getInt("MaxDownloads");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean incrementDownload(int customerId, int documentId) {
        String sql = "UPDATE document_access SET DownloadCount = DownloadCount + 1 WHERE CustomerID=? AND DocumentID=? AND DownloadCount < MaxDownloads";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId); ps.setInt(2, documentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<DocumentAccess> getByCustomer(int customerId) {
        List<DocumentAccess> list = new ArrayList<>();
        String sql = "SELECT da.*, d.Title AS DocumentTitle, d.ThumbnailUrl AS DocumentThumbnail, d.FileType, d.PageCount " +
                     "FROM document_access da JOIN documents d ON da.DocumentID = d.DocumentID " +
                     "WHERE da.CustomerID = ? ORDER BY da.GrantedAt DESC";
        try (Connection conn = DBConnect.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentAccess da = new DocumentAccess();
                    da.setAccessId(rs.getInt("AccessID"));
                    da.setCustomerId(rs.getInt("CustomerID"));
                    da.setDocumentId(rs.getInt("DocumentID"));
                    da.setOrderId(rs.getInt("OrderID"));
                    da.setGrantedAt(rs.getTimestamp("GrantedAt"));
                    da.setDownloadCount(rs.getInt("DownloadCount"));
                    da.setMaxDownloads(rs.getInt("MaxDownloads"));
                    da.setDocumentTitle(rs.getString("DocumentTitle"));
                    da.setDocumentThumbnail(rs.getString("DocumentThumbnail"));
                    da.setFileType(rs.getString("FileType"));
                    da.setPageCount(rs.getInt("PageCount"));
                    list.add(da);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}