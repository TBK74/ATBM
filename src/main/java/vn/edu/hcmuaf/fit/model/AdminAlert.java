package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class AdminAlert {
    private int alertId;
    private String alertType;
    private String severity;
    private Integer orderId;
    private String message;
    private boolean read;
    private Timestamp createdAt;
    private Timestamp resolvedAt;
    private Integer resolvedBy;

    public AdminAlert() {}

    // Getters & Setters
    public int getAlertId()                    { return alertId; }
    public void setAlertId(int v)              { this.alertId = v; }

    public String getAlertType()               { return alertType; }
    public void setAlertType(String v)         { this.alertType = v; }

    public String getSeverity()                { return severity; }
    public void setSeverity(String v)          { this.severity = v; }

    public Integer getOrderId()                { return orderId; }
    public void setOrderId(Integer v)          { this.orderId = v; }

    public String getMessage()                 { return message; }
    public void setMessage(String v)           { this.message = v; }

    public boolean isRead()                    { return read; }
    public void setRead(boolean v)             { this.read = v; }

    public Timestamp getCreatedAt()            { return createdAt; }
    public void setCreatedAt(Timestamp v)      { this.createdAt = v; }

    public Timestamp getResolvedAt()           { return resolvedAt; }
    public void setResolvedAt(Timestamp v)     { this.resolvedAt = v; }

    public Integer getResolvedBy()             { return resolvedBy; }
    public void setResolvedBy(Integer v)       { this.resolvedBy = v; }

    /** Badge CSS class cho JSP */
    public String getSeverityCss() {
        if (severity == null) return "secondary";
        switch (severity) {
            case "critical": return "danger";
            case "warning":  return "warning";
            default:         return "info";
        }
    }

    /** Icon FontAwesome cho từng loại cảnh báo */
    public String getTypeIcon() {
        if (alertType == null) return "fa-bell";
        switch (alertType) {
            case "signature_mismatch":  return "fa-file-signature";
            case "key_revoked_orders":  return "fa-key";
            case "edit_blocked":        return "fa-lock";
            default:                    return "fa-triangle-exclamation";
        }
    }
}