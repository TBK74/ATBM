<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Khóa học - EDUMART</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Catalog/catalog.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
            </head>

            <body>
                <jsp:include page="/style/header/header.jsp" />

                <div class="container">
                    <div class="page-header">
                        <div class="breadcrumb">
                            <a href="${pageContext.request.contextPath}/home" class="home-link"><i
                                    class="fa-solid fa-house"></i></a>
                            <span class="chevron">›</span>
                            <span id="crumbCurrent">
                                <c:choose>
                                    <c:when test="${not empty currentCategory}">
                                        ${currentCategory.categoryName}
                                    </c:when>
                                    <c:otherwise>
                                        Tất cả khóa học
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

                    <form id="filterForm" method="get" action="${pageContext.request.contextPath}/courses">
                        <c:if test="${not empty currentCategory}">
                            <input type="hidden" name="cid" value="${currentCategory.categoryID}">
                        </c:if>
                        <c:if test="${not empty param.q}">
                            <input type="hidden" name="q" value="${param.q}">
                        </c:if>
                        <input type="hidden" name="sort" id="sortInput" value="${selectedSort}">

                        <div class="catalog-page">
                            <aside class="filter-sidebar" id="filterSidebar">
                                <section class="filter-block">
                                    <h3 class="filter-title">DANH MỤC</h3>
                                    <div class="filter-list">
                                        <c:forEach var="cat" items="${categories}">
                                            <label class="chk">
                                                <input type="radio" name="cid" value="${cat.categoryID}"
                                                    onchange="this.form.submit()"
                                                    ${currentCategory.categoryID == cat.categoryID ? 'checked' : ''}>
                                                <span>${cat.categoryName}</span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                </section>

                                <section class="filter-block">
                                    <h3 class="filter-title">CẤP ĐỘ</h3>
                                    <div class="filter-list">
                                        <label class="chk"><input type="radio" name="level" value="beginner"
                                                onchange="this.form.submit()"
                                                ${selectedLevel=="beginner" ? "checked" : "" }> <span>Cơ
                                                bản</span></label>
                                        <label class="chk"><input type="radio" name="level" value="intermediate"
                                                onchange="this.form.submit()"
                                                ${selectedLevel=="intermediate" ? "checked" : "" }>
                                            <span>Trung cấp</span></label>
                                        <label class="chk"><input type="radio" name="level" value="advanced"
                                                onchange="this.form.submit()"
                                                ${selectedLevel=="advanced" ? "checked" : "" }> <span>Nâng
                                                cao</span></label>
                                    </div>
                                </section>

                                <section class="filter-block price">
                                    <h3 class="filter-title">KHOẢNG GIÁ</h3>
                                    <div class="filter-list" id="priceList">
                                        <label class="chk"><input type="radio" name="price" value="free"
                                                onchange="this.form.submit()" ${selectedPrice=="free" ? "checked" : ""
                                                }> <span>Miễn phí</span></label>
                                        <label class="chk"><input type="radio" name="price" value="p1"
                                                onchange="this.form.submit()" ${selectedPrice=="p1" ? "checked" : ""
                                                }> <span>Dưới 200.000đ</span></label>
                                        <label class="chk"><input type="radio" name="price" value="p2"
                                                onchange="this.form.submit()" ${selectedPrice=="p2" ? "checked" : ""
                                                }> <span>200.000đ - 500.000đ</span></label>
                                        <label class="chk"><input type="radio" name="price" value="p3"
                                                onchange="this.form.submit()" ${selectedPrice=="p3" ? "checked" : ""
                                                }> <span>Trên 500.000đ</span></label>
                                    </div>
                                </section>
                            </aside>

                            <main class="catalog-main">
                                <div class="toolbar">
                                    <div class="sort-bar" id="sortBar">
                                        <button type="button"
                                            class="sort-btn ${selectedSort == 'best' || empty selectedSort ? 'active' : ''}"
                                            onclick="document.getElementById('sortInput').value='best';document.getElementById('filterForm').submit();">Bán
                                            chạy nhất</button>
                                        <button type="button" class="sort-btn ${selectedSort == 'priceAsc' ? 'active' : ''}"
                                            onclick="document.getElementById('sortInput').value='priceAsc';document.getElementById('filterForm').submit();">Giá
                                            tăng dần</button>
                                        <button type="button" class="sort-btn ${selectedSort == 'priceDesc' ? 'active' : ''}"
                                            onclick="document.getElementById('sortInput').value='priceDesc';document.getElementById('filterForm').submit();">Giá
                                            giảm dần</button>
                                        <button type="button" class="sort-btn ${selectedSort == 'newest' ? 'active' : ''}"
                                            onclick="document.getElementById('sortInput').value='newest';document.getElementById('filterForm').submit();">Mới
                                            nhất</button>
                                    </div>
                                    <div class="result-counter" id="resultCounter">${totalItems} khóa học</div>
                                </div>

                                <div class="product-grid" id="productGrid">
                                    <c:if test="${empty courses}">
                                        <div class="empty-state" style="padding:60px 0;text-align:center;width:100%;">
                                            <i class="fa-solid fa-book-open" style="font-size:48px;color:#ccc;"></i>
                                            <p style="margin-top:12px;color:#888;">Không tìm thấy khóa học phù hợp.</p>
                                        </div>
                                    </c:if>
                                    <c:forEach var="c" items="${courses}">
                                        <article class="p-card" data-id="${c.id}">
                                            <c:if test="${not empty c.badge}">
                                                <div class="p-badge">${c.badge}</div>
                                            </c:if>
                                            <a class="p-thumb"
                                                href="${pageContext.request.contextPath}/course-detail?id=${c.id}">
                                                <img class="p-img" src="${c.thumbnailUrl}" alt="${c.title}">
                                            </a>
                                            <h3 class="p-title"><a
                                                    href="${pageContext.request.contextPath}/course-detail?id=${c.id}"
                                                    class="p-link">${c.title}</a></h3>
                                            <div class="p-rating">
                                                <span class="stars">
                                                    <c:forEach begin="1" end="${c.rating.intValue()}">★</c:forEach>
                                                </span>
                                                <span class="p-reviews">(${c.reviewCount})</span>
                                                <span class="p-reviews">· ${c.levelVietnamese}</span>
                                            </div>
                                            <div class="p-price">
                                                <span class="price-new">
                                                    <fmt:formatNumber value="${c.price}" type="currency"
                                                        currencySymbol="đ" />
                                                </span>
                                                <c:if test="${c.oldPrice > c.price}">
                                                    <s class="price-old">
                                                        <fmt:formatNumber value="${c.oldPrice}" type="currency"
                                                            currencySymbol="đ" />
                                                    </s>
                                                </c:if>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/course-detail?id=${c.id}"
                                                class="p-buy">XEM CHI TIẾT</a>
                                        </article>
                                    </c:forEach>
                                </div>

                                <c:if test="${totalPages > 1}">
                                    <div class="pagination" style="display:flex;justify-content:center;gap:8px;margin:24px 0;">
                                        <c:forEach begin="1" end="${totalPages}" var="p">
                                            <a href="?cid=${currentCategory.categoryID}&level=${selectedLevel}&price=${selectedPrice}&sort=${selectedSort}&page=${p}"
                                                class="page-link ${p == currentPage ? 'active' : ''}"
                                                style="padding:8px 14px;border:1px solid #ddd;border-radius:6px;text-decoration:none;color:#333; ${p == currentPage ? 'background:#1a73e8;color:#fff;border-color:#1a73e8;' : ''}">${p}</a>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </main>
                        </div>
                    </form>

                    <jsp:include page="/style/footer/footer.jsp" />
                </div>

                <script src="style/header/header.js"></script>
                <script src="style/footer/footer.js"></script>
            </body>

            </html>
