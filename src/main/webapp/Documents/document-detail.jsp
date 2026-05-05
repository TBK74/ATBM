<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="utf-8" />
                <title>${document.title} | EDUMART</title>
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/ProductDetail.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Home/Product/HomeProduct.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/view.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/inforProduct.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
            </head>

            <body>
                <jsp:include page="/style/header/header.jsp" />

                <div class="container">
                    <div class="product">
                        <div class="product-gallery" id="gallery">
                            <div class="main-image" aria-live="polite">
                                <img id="mainImg" src="${document.thumbnailUrl}" alt="${document.title}">
                            </div>
                        </div>

                        <div class="detail">
                            <section id="product-panel" class="product-panel">
                                <h1 class="prod-title">${document.title}</h1>
                                <p style="color:#666;margin-top:6px;">Tác giả: ${document.author}</p>
                                <div class="divider"></div>

                                <div class="price-wrap">
                                    <div class="price-new" id="price-new">
                                        <c:choose>
                                            <c:when test="${document.price == 0}">Miễn phí</c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${document.price}" type="currency"
                                                    currencySymbol="đ" />
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <c:if test="${document.oldPrice > document.price}">
                                        <div class="price-old" id="price-old">
                                            <fmt:formatNumber value="${document.oldPrice}" type="currency"
                                                currencySymbol="đ" />
                                        </div>
                                        <div class="price-off" id="price-off">
                                            -
                                            <fmt:formatNumber value="${(1 - document.price / document.oldPrice) * 100}"
                                                maxFractionDigits="0" />%
                                        </div>
                                    </c:if>
                                </div>

                                <div class="grid">
                                    <div class="label">Loại file:</div>
                                    <div class="status">${document.fileType}</div>

                                    <div class="label">Số trang:</div>
                                    <div class="status">${document.pageCount} trang</div>

                                    <div class="label">Kích thước:</div>
                                    <div class="status">${document.fileSizeDisplay}</div>
                                </div>

                                <div class="qty-row">
                                    <c:choose>
                                        <c:when test="${hasAccess}">
                                            <a href="${pageContext.request.contextPath}/my-documents" class="btn btn-buy"
                                                style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;">
                                                <i class="fa-solid fa-download"></i> Tải tài liệu
                                            </a>
                                        </c:when>
                                        <c:when test="${inCart}">
                                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-call"
                                                style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;">
                                                <i class="fa-solid fa-cart-shopping"></i> Đã có trong giỏ — Xem giỏ hàng
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                                <input type="hidden" name="type" value="document">
                                                <input type="hidden" name="id" value="${document.id}">
                                                <button type="submit" id="btnAddToCart" class="add-cart">
                                                    <i class="fa-solid fa-cart-plus"></i> Thêm vào giỏ
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="benefits">
                                    <div class="benefit"><i class="fa-solid fa-rotate"></i>Tải lại tối đa 5 lần</div>
                                    <div class="benefit"><i class="fa-solid fa-shield-halved"></i>Tài liệu chính chủ,
                                        bản quyền</div>
                                </div>
                            </section>
                        </div>

                        <div class="product-info">
                            <article class="product-card">
                                <div id="descWrap" class="collapse-wrap">
                                    ${document.description}
                                </div>
                            </article>
                        </div>

                        <div id="viewed-root">
                            <aside class="viewed-panel viewed--compact" aria-labelledby="viewed-title">
                                <div class="viewed-header">
                                    <h3 id="viewed-title">Tài liệu liên quan</h3>
                                    <a class="btn-view-all" href="${pageContext.request.contextPath}/documents">Xem
                                        tất cả</a>
                                </div>
                                <ul class="viewed-list" role="list">
                                    <c:forEach var="rd" items="${relatedDocs}">
                                        <li class="viewed-item">
                                            <div class="viewed-thumb">
                                                <c:if test="${not empty rd.badge}">
                                                    <span class="badge-sale">${rd.badge}</span>
                                                </c:if>
                                                <img src="${rd.thumbnailUrl}" alt="${rd.title}" loading="lazy">
                                            </div>
                                            <div class="viewed-info">
                                                <a href="${pageContext.request.contextPath}/document-detail?id=${rd.id}"
                                                    class="viewed-name">${rd.title}</a>
                                                <div class="viewed-price">
                                                    <span class="price-now">
                                                        <fmt:formatNumber value="${rd.price}" type="currency"
                                                            currencySymbol="đ" />
                                                    </span>
                                                </div>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </aside>
                        </div>
                    </div>

                    <jsp:include page="/style/footer/footer.jsp" />
                </div>
                <script src="style/header/header.js"></script>
                <script src="style/footer/footer.js"></script>
            </body>

            </html>
