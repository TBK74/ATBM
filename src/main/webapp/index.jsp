<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Trang chủ - EDUMART</title>
                <link rel="stylesheet" href="style/style.css">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" />
                <link rel="stylesheet" href="Home/Category/Category-sibar.css">
                <link rel="stylesheet" href="Home/Product/HomeProduct.css">
                <link rel="stylesheet" href="Home/Slide/slide.css">
                <link rel="stylesheet" href="Home/Product/productTypes.css">
                <link rel="stylesheet" href="style/footer/footer.css">
                <link rel="stylesheet" href="Home/Category/Categories.css">
                <link rel="stylesheet" href="style/header/header.css">
            </head>

            <body>
                    <c:if test="${categories == null}">
                        <c:redirect url="/home" />
                    </c:if>

                    <jsp:include page="/style/header/header.jsp" />
                    <div class="container">
                        <div class="category-sidebar" role="navigation" aria-label="Danh mục">
                            <div class="category">
                                <div class="category-header">
                                    <div class="burger" aria-hidden="true"><i class="fa-solid fa-bars menu-icon"></i>
                                    </div>
                                    Danh mục
                                </div>
                                <div id="category-list" class="category-list" role="list">
                                    <c:if test="${not empty categories}">
                                        <c:forEach var="category" items="${categories}">
                                            <a class="category-link" href="courses?cid=${category.categoryID}">
                                                <div class="category-item">
                                                    <img src="${category.image}" alt="${category.categoryName}">
                                                    <span>${category.categoryName}</span>
                                                </div>
                                            </a>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${categories != null && empty categories}">
                                        <div class="category-item"><span>Không có danh mục nào</span></div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        <section id="dm-section">

                            <section class="home-intro" aria-label="Banner nổi bật">
                                <div class="home-intro-carousel" data-autoplay="true">
                                    <div class="home-intro-track">
                                        <div class="home-intro-slide is-active">
                                            <img src="https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=1600&q=80"
                                                alt="Banner học trực tuyến" loading="eager" fetchpriority="high">
                                        </div>
                                        <div class="home-intro-slide">
                                            <img src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=1600&q=80"
                                                alt="Banner khóa học online" loading="lazy">
                                        </div>
                                    </div>

                                    <button class="home-intro-arrow prev" type="button" aria-label="Banner trước">
                                        &#10094;
                                    </button>
                                    <button class="home-intro-arrow next" type="button" aria-label="Banner sau">
                                        &#10095;
                                    </button>

                                    <div class="home-intro-dots" role="tablist" aria-label="Chọn banner">
                                        <button class="home-intro-dot is-active" type="button" data-slide="0"
                                            aria-label="Banner 1" aria-selected="true"></button>
                                        <button class="home-intro-dot" type="button" data-slide="1"
                                            aria-label="Banner 2" aria-selected="false"></button>
                                    </div>
                                </div>
                            </section>

                            <%-- KHÓA HỌC NỔI BẬT --%>
                            <c:if test="${not empty featuredCourses}">
                                <div class="dm-container">
                                    <div class="dm-head">
                                        <h2>Khóa học nổi bật</h2>
                                        <a class="dm-viewall" href="courses">Xem tất cả &gt;</a>
                                    </div>
                                    <div class="dm-slider">
                                        <button class="dm-nav dm-prev" aria-label="Trước">&#10094;</button>
                                        <div class="dm-track" style="--ppv:4">
                                            <c:forEach var="c" items="${featuredCourses}">
                                                <a class="dm-card" href="course-detail?id=${c.id}">
                                                    <c:if test="${not empty c.badge}">
                                                        <span class="badge badge--sale">${c.badge}</span>
                                                    </c:if>
                                                    <div class="thumb"><img src="${c.thumbnailUrl}" alt="${c.title}">
                                                    </div>
                                                    <div class="brand">${c.instructor}</div>
                                                    <h4 class="name">${c.title}</h4>
                                                    <div class="price">
                                                        <span class="new">
                                                            <fmt:formatNumber value="${c.price}" type="currency"
                                                                currencySymbol="đ" />
                                                        </span>
                                                        <c:if test="${c.oldPrice > c.price}">
                                                            <span class="old">
                                                                <fmt:formatNumber value="${c.oldPrice}"
                                                                    type="currency" currencySymbol="đ" />
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                    <div class="rating">
                                                        <span class="stars">
                                                            <c:forEach begin="1" end="${c.rating.intValue()}">★
                                                            </c:forEach>
                                                        </span>
                                                        <span class="count">(${c.reviewCount})</span>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                        <button class="dm-nav dm-next" aria-label="Sau">&#10095;</button>
                                    </div>
                                </div>
                            </c:if>

                            <%-- TÀI LIỆU MỚI NHẤT --%>
                            <c:if test="${not empty newestDocs}">
                                <div class="dm-container">
                                    <div class="dm-head">
                                        <h2>Tài liệu mới nhất</h2>
                                        <a class="dm-viewall" href="documents">Xem tất cả &gt;</a>
                                    </div>
                                    <div class="dm-slider">
                                        <button class="dm-nav dm-prev" aria-label="Trước">&#10094;</button>
                                        <div class="dm-track" style="--ppv:5">
                                            <c:forEach var="d" items="${newestDocs}">
                                                <a class="dm-card" href="document-detail?id=${d.id}">
                                                    <c:if test="${not empty d.badge}">
                                                        <span class="badge badge--gift">${d.badge}</span>
                                                    </c:if>
                                                    <div class="thumb"><img src="${d.thumbnailUrl}" alt="${d.title}">
                                                    </div>
                                                    <div class="brand">${d.fileType} · ${d.pageCount} trang</div>
                                                    <h4 class="name">${d.title}</h4>
                                                    <div class="price">
                                                        <span class="new">
                                                            <fmt:formatNumber value="${d.price}" type="currency"
                                                                currencySymbol="đ" />
                                                        </span>
                                                        <c:if test="${d.oldPrice > d.price}">
                                                            <span class="old">
                                                                <fmt:formatNumber value="${d.oldPrice}"
                                                                    type="currency" currencySymbol="đ" />
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                    <div class="rating">
                                                        <span class="stars">
                                                            <c:forEach begin="1" end="${d.rating.intValue()}">★
                                                            </c:forEach>
                                                        </span>
                                                        <span class="count">(${d.reviewCount})</span>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                        <button class="dm-nav dm-next" aria-label="Sau">&#10095;</button>
                                    </div>
                                </div>
                            </c:if>

                        </section>

                        <jsp:include page="/style/footer/footer.jsp" />

                    </div>
                    <script src="Home/Category/categories.js"></script>
                    <script src="style/header/header.js"></script>
                    <script src="style/footer/footer.js"></script>

            </body>

            </html>
