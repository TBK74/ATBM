<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Tài liệu - EDUMART</title>
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
                                        Tất cả tài liệu
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

                    <form id="filterForm" method="get" action="${pageContext.request.contextPath}/documents">
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
                                    <h3 class="filter-title">LOẠI FILE</h3>
                                    <div class="filter-list">
                                        <label class="chk"><input type="radio" name="fileType" value="PDF"
                                                onchange="this.form.submit()"
                                                ${selectedFileType=="PDF" ? "checked" : "" }> <span>PDF</span></label>
                                        <label class="chk"><input type="radio" name="fileType" value="DOCX"
                                                onchange="this.form.submit()"
                                                ${selectedFileType=="DOCX" ? "checked" : "" }>
                                            <span>DOCX</span></label>
                                        <label class="chk"><input type="radio" name="fileType" value="PPTX"
                                                onchange="this.form.submit()"
                                                ${selectedFileType=="PPTX" ? "checked" : "" }>
                                            <span>PPTX</span></label>
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
                                                }> <span>Dưới 50.000đ</span></label>
                                        <label class="chk"><input type="radio" name="price" value="p2"
                                                onchange="this.form.submit()" ${selectedPrice=="p2" ? "checked" : ""
                                                }> <span>50.000đ - 150.000đ</span></label>
                                        <label class="chk"><input type="radio" name="price" value="p3"
                                                onchange="this.form.submit()" ${selectedPrice=="p3" ? "checked" : ""
                                                }> <span>Trên 150.000đ</span></label>
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
                                    <div class="result-counter" id="resultCounter">${totalItems} tài liệu</div>
                                </div>

                                <div class="product-grid" id="productGrid">
                                    <c:if test="${empty documents}">
                                        <div class="empty-state" style="padding:60px 0;text-align:center;width:100%;">
                                            <i class="fa-solid fa-file-circle-xmark" style="font-size:48px;color:#ccc;"></i>
                                            <p style="margin-top:12px;color:#888;">Không tìm thấy tài liệu phù hợp.</p>
                                        </div>
                                    </c:if>
                                    <c:forEach var="d" items="${documents}">
                                        <article class="p-card" data-id="${d.id}">
                                            <c:if test="${not empty d.badge}">
                                                <div class="p-badge">${d.badge}</div>
                                            </c:if>
                                            <a class="p-thumb"
                                                href="${pageContext.request.contextPath}/document-detail?id=${d.id}">
                                                <img class="p-img" src="${d.thumbnailUrl}" alt="${d.title}">
                                            </a>
                                            <h3 class="p-title"><a
                                                    href="${pageContext.request.contextPath}/document-detail?id=${d.id}"
                                                    class="p-link">${d.title}</a></h3>
                                            <div class="p-rating">
                                                <span class="stars">
                                                    <c:forEach begin="1" end="${d.rating.intValue()}">★</c:forEach>
                                                </span>
                                                <span class="p-reviews">(${d.reviewCount})</span>
                                                <span class="p-reviews">· ${d.fileType} · ${d.pageCount} trang</span>
                                            </div>
                                            <div class="p-price">
                                                <span class="price-new">
                                                    <fmt:formatNumber value="${d.price}" type="currency"
                                                        currencySymbol="đ" />
                                                </span>
                                                <c:if test="${d.oldPrice > d.price}">
                                                    <s class="price-old">
                                                        <fmt:formatNumber value="${d.oldPrice}" type="currency"
                                                            currencySymbol="đ" />
                                                    </s>
                                                </c:if>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/document-detail?id=${d.id}"
                                                class="p-buy">XEM CHI TIẾT</a>
                                        </article>
                                    </c:forEach>
                                </div>

                                <c:if test="${totalPages > 1}">
                                    <div class="pagination" style="display:flex;justify-content:center;gap:8px;margin:24px 0;">
                                        <c:forEach begin="1" end="${totalPages}" var="p">
                                            <a href="?cid=${currentCategory.categoryID}&fileType=${selectedFileType}&price=${selectedPrice}&sort=${selectedSort}&page=${p}"
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
