package vn.edu.hcmuaf.fit.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Document implements Serializable {
    private int id;
    private String title;
    private String author;
    private String thumbnailUrl;
    private String description;
    private double price;
    private double oldPrice;
    private String fileType;        // PDF / DOCX / PPTX
    private int pageCount;
    private int fileSizeKb;
    private double rating;
    private int reviewCount;
    private int soldCount;
    private int categoryId;
    private String categoryName;
    private String badge;
    private boolean isActive;
    private Timestamp createdAt;

    public Document() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public int getFileSizeKb() { return fileSizeKb; }
    public void setFileSizeKb(int fileSizeKb) { this.fileSizeKb = fileSizeKb; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFileSizeDisplay() {
        if (fileSizeKb < 1024) return fileSizeKb + " KB";
        return String.format("%.1f MB", fileSizeKb / 1024.0);
    }
}
