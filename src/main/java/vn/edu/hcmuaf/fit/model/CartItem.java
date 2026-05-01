package vn.edu.hcmuaf.fit.model;

public class CartItem {
    private String itemType;   // "course" hoặc "document"
    private int itemId;
    private String title;
    private String thumbnailUrl;
    private double price;
    private double oldPrice;

    public CartItem() {}

    public CartItem(Course course) {
        this.itemType = "course";
        this.itemId = course.getId();
        this.title = course.getTitle();
        this.thumbnailUrl = course.getThumbnailUrl();
        this.price = course.getPrice();
        this.oldPrice = course.getOldPrice();
    }

    public CartItem(Document doc) {
        this.itemType = "document";
        this.itemId = doc.getId();
        this.title = doc.getTitle();
        this.thumbnailUrl = doc.getThumbnailUrl();
        this.price = doc.getPrice();
        this.oldPrice = doc.getOldPrice();
    }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    // Compat: quantity luôn = 1 vì khóa học/tài liệu số không có số lượng
    public int getQuantity() { return 1; }
    public double getTotalPrice() { return price; }

    public String getDetailUrl() {
        if ("course".equals(itemType)) return "course-detail?id=" + itemId;
        return "document-detail?id=" + itemId;
    }
}
