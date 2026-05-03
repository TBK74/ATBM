package vn.edu.hcmuaf.fit.model;

import java.sql.Timestamp;

public class Enrollment {
    private int enrollmentId;
    private int customerId;
    private int courseId;
    private int orderId;
    private Timestamp enrolledAt;
    private int progressPercent;
    private Timestamp completedAt;

    // Thông tin hiển thị (JOIN)
    private String courseTitle;
    private String courseThumbnail;
    private String instructorName;

    public Enrollment() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Timestamp getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Timestamp enrolledAt) { this.enrolledAt = enrolledAt; }

    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCourseThumbnail() { return courseThumbnail; }
    public void setCourseThumbnail(String courseThumbnail) { this.courseThumbnail = courseThumbnail; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public boolean isCompleted() { return completedAt != null; }
}
