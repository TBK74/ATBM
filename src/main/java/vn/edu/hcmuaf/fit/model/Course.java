package vn.edu.hcmuaf.fit.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Course implements Serializable {
    private int id;
    private String title;
    private String instructor;      // tên giảng viên (JOIN từ customers)
    private int instructorId;
    private String thumbnailUrl;
    private String shortDesc;
    private String description;
    private double price;
    private double oldPrice;
    private String level;           // beginner / intermediate / advanced
    private String language;
    private double durationHours;
    private double rating;
    private int reviewCount;
    private int soldCount;
    private int categoryId;
    private String categoryName;
    private String badge;
    private boolean isActive;
    private Timestamp createdAt;

    public Course() {}

    // ---- getters / setters ----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getShortDesc() { return shortDesc; }
    public void setShortDesc(String shortDesc) { this.shortDesc = shortDesc; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getLevelVietnamese() {
        if (level == null) return "";
        switch (level) {
            case "beginner": return "Cơ bản";
            case "intermediate": return "Trung cấp";
            case "advanced": return "Nâng cao";
            default: return level;
        }
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public double getDurationHours() { return durationHours; }
    public void setDurationHours(double durationHours) { this.durationHours = durationHours; }

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
}
