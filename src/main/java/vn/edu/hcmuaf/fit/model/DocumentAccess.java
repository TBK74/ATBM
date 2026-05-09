package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class DocumentAccess {
    private int accessId;
    private int customerId;
    private int documentId;
    private int orderId;
    private Timestamp grantedAt;
    private int downloadCount;
    private int maxDownloads;

    // Thông tin hiển thị (JOIN)
    private String documentTitle;
    private String documentThumbnail;
    private String fileType;
    private int pageCount;

    public DocumentAccess() {}

    public int getAccessId() { return accessId; }
    public void setAccessId(int accessId) { this.accessId = accessId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Timestamp getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Timestamp grantedAt) { this.grantedAt = grantedAt; }

    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }

    public int getMaxDownloads() { return maxDownloads; }
    public void setMaxDownloads(int maxDownloads) { this.maxDownloads = maxDownloads; }

    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }

    public String getDocumentThumbnail() { return documentThumbnail; }
    public void setDocumentThumbnail(String documentThumbnail) { this.documentThumbnail = documentThumbnail; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public int getRemainingDownloads() { return maxDownloads - downloadCount; }
    public boolean canDownload() { return downloadCount < maxDownloads; }
}
