package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class OrderSignature {
    private int sigId;
    private int orderId;
    private String dataHash;       // SHA-256 hex
    private String canonicalJson;  // Snapshot bất biến
    private String signature;      // Base64 RSA signature từ client
    private Integer signingKeyId;  // FK → user_keys.KeyID
    private Timestamp signedAt;
    private String signStatus;     // "unsigned" | "signed" | "verified" | "tampered"
    private Timestamp lastCheckedAt;

    public OrderSignature() {
    }

    public int getSigId() {
        return sigId;
    }

    public void setSigId(int v) {
        this.sigId = v;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int v) {
        this.orderId = v;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String v) {
        this.dataHash = v;
    }

    public String getCanonicalJson() {
        return canonicalJson;
    }

    public void setCanonicalJson(String v) {
        this.canonicalJson = v;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String v) {
        this.signature = v;
    }

    public Integer getSigningKeyId() {
        return signingKeyId;
    }

    public void setSigningKeyId(Integer v) {
        this.signingKeyId = v;
    }

    public Timestamp getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(Timestamp v) {
        this.signedAt = v;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String v) {
        this.signStatus = v;
    }

    public Timestamp getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Timestamp v) {
        this.lastCheckedAt = v;
    }

    /**
     * Nhãn hiển thị tiếng Việt
     */
    public String getSignStatusLabel() {
        if (signStatus == null) return "Chưa xác nhận";
        switch (signStatus) {
            case "unsigned":
                return "Chưa ký";
            case "signed":
                return "Đã ký – chờ xác thực";
            case "verified":
                return "Đã xác thực ✓";
            case "tampered":
                return "Cảnh báo: dữ liệu thay đổi ⚠";
            default:
                return signStatus;
        }
    }

    public String getSignStatusCss() {
        if (signStatus == null) return "secondary";
        switch (signStatus) {
            case "unsigned":
                return "warning";
            case "signed":
                return "info";
            case "verified":
                return "success";
            case "tampered":
                return "danger";
            default:
                return "secondary";
        }
    }
}