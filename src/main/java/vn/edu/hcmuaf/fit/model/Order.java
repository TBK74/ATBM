package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int customerId;
    private Timestamp orderDate;
    private double totalAmount;
    private String status; // Pending, Processing, Completed, Cancelled
    private String paymentMethod;
    private String recipientName;
    private String promoCode;
    private double discountAmount;

    public Order() {}

    public Order(int orderId, int customerId, Timestamp orderDate, double totalAmount,
                 String status, String paymentMethod, String recipientName) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.recipientName = recipientName;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getStatusVietnamese() {
        if (status == null) return "Không xác định";
        switch (status) {
            case "Pending":    return "Chờ xác nhận";
            case "Processing": return "Đã xác nhận";
            case "Completed":  return "Hoàn thành";
            case "Cancelled":  return "Đã hủy";
            default:           return status;
        }
    }

    public String getStatusCssClass() {
        if (status == null) return "secondary";
        switch (status) {
            case "Pending":    return "warning";
            case "Processing": return "primary";
            case "Completed":  return "success";
            case "Cancelled":  return "danger";
            default:           return "secondary";
        }
    }
}
