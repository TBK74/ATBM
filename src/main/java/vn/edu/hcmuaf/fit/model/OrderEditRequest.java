package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class OrderEditRequest {
    private int requestId;
    private int orderId;
    private int requestedBy;
    private String changeSummary;
    private String oldStatus;
    private String newStatus;
    private String status;          // pending | approved | rejected
    private Timestamp createdAt;
    private Timestamp reviewedAt;
    private Integer reviewedBy;
    private String rejectReason;

    private String requestedByUsername;
    private String reviewedByUsername;

    public OrderEditRequest() {}

    public int getRequestId()                         { return requestId; }
    public void setRequestId(int v)                   { this.requestId = v; }

    public int getOrderId()                           { return orderId; }
    public void setOrderId(int v)                     { this.orderId = v; }

    public int getRequestedBy()                       { return requestedBy; }
    public void setRequestedBy(int v)                 { this.requestedBy = v; }

    public String getChangeSummary()                  { return changeSummary; }
    public void setChangeSummary(String v)            { this.changeSummary = v; }

    public String getOldStatus()                      { return oldStatus; }
    public void setOldStatus(String v)                { this.oldStatus = v; }

    public String getNewStatus()                      { return newStatus; }
    public void setNewStatus(String v)                { this.newStatus = v; }

    public String getStatus()                         { return status; }
    public void setStatus(String v)                   { this.status = v; }

    public Timestamp getCreatedAt()                   { return createdAt; }
    public void setCreatedAt(Timestamp v)             { this.createdAt = v; }

    public Timestamp getReviewedAt()                  { return reviewedAt; }
    public void setReviewedAt(Timestamp v)            { this.reviewedAt = v; }

    public Integer getReviewedBy()                    { return reviewedBy; }
    public void setReviewedBy(Integer v)              { this.reviewedBy = v; }

    public String getRejectReason()                   { return rejectReason; }
    public void setRejectReason(String v)             { this.rejectReason = v; }

    public String getRequestedByUsername()            { return requestedByUsername; }
    public void setRequestedByUsername(String v)      { this.requestedByUsername = v; }

    public String getReviewedByUsername()             { return reviewedByUsername; }
    public void setReviewedByUsername(String v)       { this.reviewedByUsername = v; }

    public String getStatusCss() {
        if (status == null) return "secondary";
        switch (status) {
            case "approved": return "success";
            case "rejected": return "danger";
            default:         return "warning";
        }
    }

    public String getStatusVi() {
        if (status == null) return "Không rõ";
        switch (status) {
            case "approved": return "Đã duyệt";
            case "rejected": return "Từ chối";
            default:         return "Chờ duyệt";
        }
    }
}