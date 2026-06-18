package vn.edu.hcmuaf.fit.model;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private String itemType;   // "course" hoặc "document"
    private int itemId;
    private double priceAtOrder;

    // Thông tin hiển thị (JOIN)
    private String itemTitle;
    private String itemThumbnail;

    public OrderItem() {}

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public String getItemThumbnail() { return itemThumbnail; }
    public void setItemThumbnail(String itemThumbnail) { this.itemThumbnail = itemThumbnail; }

    public String getDetailUrl() {
        if ("course".equals(itemType)) return "course-detail?id=" + itemId;
        return "document-detail?id=" + itemId;
    }
}
