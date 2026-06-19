-- ============================================
-- DATABASE SCHEMA: Website Bán Khóa Học & Tài Liệu
-- ============================================

CREATE
DATABASE IF NOT EXISTS edu_web CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE atbm;

-- Tài khoản (giữ nguyên + thêm role instructor)
CREATE TABLE accounts
(
    AccountID    INT AUTO_INCREMENT PRIMARY KEY,
    Username     VARCHAR(50)  NOT NULL UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    Email        VARCHAR(100) NOT NULL UNIQUE,
    Role         ENUM('student','instructor','admin') DEFAULT 'student',
    Status       ENUM('active','banned') DEFAULT 'active',
    CreatedAt    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thông tin khách hàng (giữ nguyên)
CREATE TABLE customers
(
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    AccountID  INT NOT NULL UNIQUE,
    FullName   VARCHAR(100),
    Phone      VARCHAR(20),
    AvatarUrl  VARCHAR(255),
    FOREIGN KEY (AccountID) REFERENCES accounts (AccountID)
);

-- Danh mục (giữ nguyên, dùng chung cho course & document)
CREATE TABLE categories
(
    CategoryID   INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL,
    Description  TEXT,
    DisplayOrder INT DEFAULT 0,
    Image        VARCHAR(255)
);

-- Khóa học (thay thế bảng products + productdetails)
CREATE TABLE courses
(
    CourseID      INT AUTO_INCREMENT PRIMARY KEY,
    Title         VARCHAR(200)   NOT NULL,
    InstructorID  INT,
    CategoryID    INT,
    Price         DECIMAL(12, 0) NOT NULL DEFAULT 0,
    OldPrice      DECIMAL(12, 0)          DEFAULT 0,
    ThumbnailUrl  VARCHAR(255),
    ShortDesc     VARCHAR(500),
    Description   TEXT,
    Level         ENUM('beginner','intermediate','advanced') DEFAULT 'beginner',
    Language      VARCHAR(30)             DEFAULT 'Tiếng Việt',
    DurationHours DECIMAL(5, 1)           DEFAULT 0,
    Rating        DECIMAL(2, 1)           DEFAULT 0,
    ReviewCount   INT                     DEFAULT 0,
    SoldCount     INT                     DEFAULT 0,
    Badge         VARCHAR(50),
    IsActive      TINYINT(1) DEFAULT 1,
    CreatedAt     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (InstructorID) REFERENCES accounts (AccountID),
    FOREIGN KEY (CategoryID) REFERENCES categories (CategoryID)
);

-- Tài liệu (bảng mới)
CREATE TABLE documents
(
    DocumentID   INT AUTO_INCREMENT PRIMARY KEY,
    Title        VARCHAR(200)   NOT NULL,
    Author       VARCHAR(100),
    CategoryID   INT,
    Price        DECIMAL(12, 0) NOT NULL DEFAULT 0,
    OldPrice     DECIMAL(12, 0)          DEFAULT 0,
    ThumbnailUrl VARCHAR(255),
    Description  TEXT,
    FilePath     VARCHAR(500),
    FileType     VARCHAR(10)             DEFAULT 'PDF',
    PageCount    INT                     DEFAULT 0,
    FileSizeKb   INT                     DEFAULT 0,
    Rating       DECIMAL(2, 1)           DEFAULT 0,
    ReviewCount  INT                     DEFAULT 0,
    SoldCount    INT                     DEFAULT 0,
    Badge        VARCHAR(50),
    IsActive     TINYINT(1) DEFAULT 1,
    CreatedAt    TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID) REFERENCES categories (CategoryID)
);

-- Giỏ hàng (giữ cấu trúc, đổi nội dung)
CREATE TABLE carts
(
    CartID     INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL UNIQUE,
    CreatedAt  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES customers (CustomerID)
);

CREATE TABLE cart_items
(
    CartItemID INT AUTO_INCREMENT PRIMARY KEY,
    CartID     INT            NOT NULL,
    ItemType   ENUM('course','document') NOT NULL,
    ItemID     INT            NOT NULL,
    PriceAtAdd DECIMAL(12, 0) NOT NULL,
    AddedAt    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_cart_item (CartID, ItemType, ItemID),
    FOREIGN KEY (CartID) REFERENCES carts (CartID) ON DELETE CASCADE
);

-- Đơn hàng (giữ nguyên, bỏ ShippingAddress)
CREATE TABLE orders
(
    OrderID        INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID     INT            NOT NULL,
    TotalAmount    DECIMAL(12, 0) NOT NULL,
    Status         ENUM('Pending','Processing','Completed','Cancelled','PaymentMismatch') DEFAULT 'Pending',
    PaymentMethod  VARCHAR(50),
    RecipientName  VARCHAR(100),
    PromoCode      VARCHAR(50),
    DiscountAmount DECIMAL(12, 0) DEFAULT 0,
    OrderDate      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES customers (CustomerID)
);

CREATE TABLE orderitems
(
    OrderItemID  INT AUTO_INCREMENT PRIMARY KEY,
    OrderID      INT            NOT NULL,
    ItemType     ENUM('course','document') NOT NULL,
    ItemID       INT            NOT NULL,
    PriceAtOrder DECIMAL(12, 0) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES orders (OrderID)
);

-- Ghi danh khóa học (sau khi mua)
CREATE TABLE enrollments
(
    EnrollmentID    INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID      INT NOT NULL,
    CourseID        INT NOT NULL,
    OrderID         INT,
    EnrolledAt      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ProgressPercent INT       DEFAULT 0,
    CompletedAt     TIMESTAMP NULL,
    UNIQUE KEY uq_enrollment (CustomerID, CourseID),
    FOREIGN KEY (CustomerID) REFERENCES customers (CustomerID),
    FOREIGN KEY (CourseID) REFERENCES courses (CourseID),
    FOREIGN KEY (OrderID) REFERENCES orders (OrderID)
);

-- Quyền truy cập tài liệu (sau khi mua)
CREATE TABLE document_access
(
    AccessID      INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID    INT NOT NULL,
    DocumentID    INT NOT NULL,
    OrderID       INT,
    GrantedAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    DownloadCount INT       DEFAULT 0,
    MaxDownloads  INT       DEFAULT 5,
    UNIQUE KEY uq_access (CustomerID, DocumentID),
    FOREIGN KEY (CustomerID) REFERENCES customers (CustomerID),
    FOREIGN KEY (DocumentID) REFERENCES documents (DocumentID),
    FOREIGN KEY (OrderID) REFERENCES orders (OrderID)
);

-- Đánh giá (mở rộng cho cả course & document)
CREATE TABLE reviews
(
    ReviewID   INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT     NOT NULL,
    ItemType   ENUM('course','document') NOT NULL,
    ItemID     INT     NOT NULL,
    Rating     TINYINT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Content    TEXT,
    CreatedAt  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_review (CustomerID, ItemType, ItemID),
    FOREIGN KEY (CustomerID) REFERENCES customers (CustomerID)
);

-- Mã khuyến mãi (giữ nguyên + mở rộng AppliesTo để khớp với AdminPromoController/promocodes.jsp đã build)
CREATE TABLE promo_codes
(
    PromoID       INT AUTO_INCREMENT PRIMARY KEY,
    Code          VARCHAR(50)    NOT NULL UNIQUE,
    Type          ENUM('percent','fixed') NOT NULL,
    Amount        DECIMAL(10, 2) NOT NULL,
    MinOrderValue DECIMAL(12, 0) DEFAULT 0,
    UsageLimit    INT            DEFAULT 0,
    UsedCount     INT            DEFAULT 0,
    StartAt       TIMESTAMP NULL,
    EndAt         TIMESTAMP NULL,
    IsActive      TINYINT(1) DEFAULT 1,
    AppliesTo     ENUM('all','category','product') DEFAULT 'all',
    AppliesToId   INT NULL,
    CreatedBy     INT NULL,
    FOREIGN KEY (CreatedBy) REFERENCES accounts (AccountID)
);

-- Giao dịch thanh toán online qua SePay (bảng này bị thiếu trong schema gốc,
-- khiến chức năng thanh toán online luôn lỗi khi ghi nhận giao dịch)
CREATE TABLE payment_transactions
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    order_id           INT         NOT NULL,
    txn_ref            VARCHAR(50) NOT NULL,
    vnp_transaction_no VARCHAR(50) NULL,
    amount             BIGINT      NOT NULL,
    bank_code          VARCHAR(20) NULL,
    pay_date           VARCHAR(20) NULL,
    response_code      VARCHAR(5) NULL,
    transaction_status VARCHAR(5) NULL,
    order_info         VARCHAR(255) NULL,
    status             VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at         TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    INDEX              idx_order_id (order_id),
    INDEX              idx_txn_ref (txn_ref),
    FOREIGN KEY (order_id) REFERENCES orders (OrderID) ON DELETE CASCADE
);

-- ===== DỮ LIỆU MẪU =====
INSERT INTO categories (CategoryName, Description, DisplayOrder)
VALUES ('Lập trình', 'Các khóa học và tài liệu về lập trình', 1),
       ('Thiết kế', 'UI/UX, Photoshop, Illustrator', 2),
       ('Marketing', 'Digital marketing, SEO, content', 3),
       ('Kinh doanh', 'Khởi nghiệp, quản lý, tài chính', 4),
       ('Ngoại ngữ', 'Tiếng Anh, Nhật, Hàn...', 5);

-- Mật khẩu đã hash sẵn bằng BCrypt (cost 12) để có thể đăng nhập demo ngay:
--   admin       / Admin@123
--   instructor1 / Instructor@123
INSERT INTO accounts (Username, PasswordHash, Email, Role)
VALUES ('admin', '$2a$12$./3AXZYZtw8bfnkQ8SlaJu5EpjL4A/4LL0/Y0TEZQRZtxuxfvMYd.', 'admin@edu.vn', 'admin'),
       ('instructor1', '$2a$12$JjG0oL5I2YVkpawkX5ai7.h0gr.s7SMpv.LxQsjTnw/xxVJl/Tfcy', 'instructor@edu.vn',
        'instructor');

INSERT INTO customers (AccountID, FullName)
VALUES (1, 'Quản trị viên'),
       (2, 'Giảng viên Demo');

INSERT INTO courses (Title, InstructorID, CategoryID, Price, OldPrice, ShortDesc, Level, Language, DurationHours,
                     SoldCount, Badge, IsActive)
VALUES ('Java Web từ cơ bản đến nâng cao', 2, 1, 299000, 499000, 'Học lập trình Java Web với Servlet, JSP, Spring Boot',
        'beginner', 'Tiếng Việt', 20.5, 156, 'Bán chạy', 1),
       ('React & Node.js Full Stack', 2, 1, 399000, 699000, 'Xây dựng ứng dụng full-stack với React và Node.js',
        'intermediate', 'Tiếng Việt', 35.0, 89, 'Mới', 1),
       ('UI/UX Design cơ bản', 2, 2, 199000, 349000, 'Thiết kế giao diện người dùng với Figma', 'beginner',
        'Tiếng Việt', 15.0, 203, NULL, 1),
       ('Digital Marketing A-Z', 2, 3, 349000, 549000, 'Toàn bộ kiến thức về marketing số', 'intermediate',
        'Tiếng Việt', 28.0, 127, 'Hot', 1);

INSERT INTO documents (Title, Author, CategoryID, Price, OldPrice, FileType, PageCount, FileSizeKb, SoldCount, Badge,
                       IsActive)
VALUES ('Giáo trình Java cơ bản', 'Nguyễn Văn A', 1, 49000, 99000, 'PDF', 250, 5120, 312, 'Bán chạy', 1),
       ('Tài liệu Spring Boot', 'Trần Thị B', 1, 79000, 149000, 'PDF', 380, 8192, 178, NULL, 1),
       ('Hướng dẫn Figma cho người mới', 'Lê Văn C', 2, 39000, 69000, 'PDF', 120, 3072, 245, 'Mới', 1),
       ('Sách SEO toàn tập 2024', 'Phạm Thị D', 3, 89000, 179000, 'PDF', 450, 10240, 98, 'Hot', 1);

INSERT INTO promo_codes (Code, Type, Amount, MinOrderValue, UsageLimit, IsActive, AppliesTo)
VALUES ('WELCOME10', 'percent', 10.00, 0, 0, 1, 'all'),
       ('GIAM50K', 'fixed', 50000.00, 200000, 100, 1, 'all');