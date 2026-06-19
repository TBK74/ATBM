<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8" />
    <title>${document.title} | EDUMART</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/detail-page.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Home/Product/HomeProduct.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
<div id="dp-toast-container"></div>
<jsp:include page="/style/header/header.jsp" />

<div class="dp-page">
    <div class="dp-grid">
        <div class="dp-left">
            <div class="dp-thumb">
                <img src="${document.thumbnailUrl}" alt="${document.title}">
            </div>

            <div class="dp-desc-card">
                <h3><i class="fa-solid fa-align-left"></i> Mô tả tài liệu</h3>
                <div id="descWrap" class="collapse-wrap">
                    <c:choose>
                        <c:when test="${not empty document.description}">${document.description}</c:when>
                        <c:otherwise><p style="color:#999;">Chưa có mô tả.</p></c:otherwise>
                    </c:choose>
                </div>
                <div class="toggle-btn-wrap">
                    <button id="toggleDesc" class="toggle-btn">Xem tất cả ▼</button>
                </div>
            </div>

            <!-- Reviews -->
            <div class="dp-reviews">
                <jsp:include page="/Product_Detail/reviews-section.jsp" />
            </div>
        </div>

        <div class="dp-right">

            <div class="dp-panel">
                <h1>${document.title}</h1>
                <p class="dp-subtitle">Tác giả: <strong>${document.author}</strong></p>
                <hr class="dp-divider">

                <!-- Giá -->
                <div class="dp-price-wrap">
                    <c:choose>
                        <c:when test="${document.price == 0}">
                            <span class="dp-price-free"><i class="fa-solid fa-gift"></i> Miễn phí</span>
                        </c:when>
                        <c:otherwise>
                            <span class="dp-price-new">
                                <fmt:formatNumber value="${document.price}" type="currency" currencySymbol="₫"/>
                            </span>
                            <c:if test="${document.oldPrice > document.price}">
                                <span class="dp-price-old">
                                    <fmt:formatNumber value="${document.oldPrice}" type="currency" currencySymbol="₫"/>
                                </span>
                                <span class="dp-price-off">
                                    -<fmt:formatNumber value="${(1 - document.price / document.oldPrice) * 100}" maxFractionDigits="0"/>%
                                </span>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Thông số -->
                <div class="dp-specs">
                    <span class="dp-spec-label"><i class="fa-solid fa-file"></i> Loại file</span>
                    <span class="dp-spec-val">${document.fileType}</span>

                    <span class="dp-spec-label"><i class="fa-solid fa-book-open"></i> Số trang</span>
                    <span class="dp-spec-val">${document.pageCount} trang</span>

                    <span class="dp-spec-label"><i class="fa-solid fa-weight-hanging"></i> Kích thước</span>
                    <span class="dp-spec-val">${document.fileSizeDisplay}</span>
                </div>

                <hr class="dp-divider">

                <!-- Nút hành động -->
                <div class="dp-cta">
                    <c:choose>
                        <c:when test="${hasAccess}">
                            <a href="${pageContext.request.contextPath}/my-documents" class="btn-dp-enrolled">
                                <i class="fa-solid fa-download"></i> Tải tài liệu
                            </a>
                        </c:when>
                        <c:when test="${inCart}">
                            <a href="${pageContext.request.contextPath}/cart" class="btn-dp-incart">
                                <i class="fa-solid fa-cart-shopping"></i> Xem giỏ hàng
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button type="button" id="btnAddToCart" class="btn-dp-add">
                                <i class="fa-solid fa-cart-plus"></i> Thêm vào giỏ
                            </button>
                            <button type="button" id="btnBuyNow" class="btn-dp-buy">
                                <i class="fa-solid fa-bolt"></i> Mua ngay
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Lợi ích -->
                <div class="dp-benefits">
                    <div class="dp-benefit"><i class="fa-solid fa-rotate"></i>Tải lại tối đa 5 lần</div>
                    <div class="dp-benefit"><i class="fa-solid fa-shield-halved"></i>Tài liệu chính chủ, bản quyền</div>
                    <div class="dp-benefit"><i class="fa-solid fa-mobile-screen"></i>Xem trên mọi thiết bị</div>
                    <div class="dp-benefit"><i class="fa-solid fa-rotate-left"></i>Cam kết hoàn tiền trong 7 ngày</div>
                </div>
            </div>

            <!-- Tài liệu liên quan -->
            <c:if test="${not empty relatedDocs}">
                <div class="dp-related">
                    <div class="dp-related-header">
                        <h3><i class="fa-solid fa-layer-group"></i> Tài liệu liên quan</h3>
                        <a href="${pageContext.request.contextPath}/documents">Xem tất cả</a>
                    </div>
                    <ul class="dp-related-list">
                        <c:forEach var="rd" items="${relatedDocs}">
                            <li class="dp-related-item">
                                <img class="dp-related-thumb" src="${rd.thumbnailUrl}" alt="${rd.title}" loading="lazy">
                                <div class="dp-related-info">
                                    <a href="${pageContext.request.contextPath}/document-detail?id=${rd.id}" class="dp-related-name">${rd.title}</a>
                                    <div class="dp-related-price">
                                        <c:choose>
                                            <c:when test="${rd.price == 0}"><span class="dp-related-free">Miễn phí</span></c:when>
                                            <c:otherwise><fmt:formatNumber value="${rd.price}" type="currency" currencySymbol="₫"/></c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

        </div>
        <!-- end dp-right -->

    </div>
    <!-- end dp-grid -->
</div>

<jsp:include page="/style/footer/footer.jsp" />

<script>
    const ITEM_ID = ${document.id};
    const ITEM_TYPE = "document";
    const CTX = "${pageContext.request.contextPath}";

    function showToast(msg, type) {
        const c = document.getElementById('dp-toast-container');
        const t = document.createElement('div');
        t.className = 'dp-toast ' + (type || 'success');
        t.innerHTML = '<i class="fa-solid ' + (type === 'error' ? 'fa-circle-xmark' : 'fa-circle-check') + '"></i> ' + msg;
        c.appendChild(t);
        setTimeout(() => t.remove(), 3100);
    }

    function addToCart(callback) {
        fetch(CTX + "/cart/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "X-Requested-With": "XMLHttpRequest"
            },
            body: "type=" + ITEM_TYPE + "&id=" + ITEM_ID
        })
            .then(r => r.json())
            .then(data => {
                if (data.status === "success") {
                    document.querySelectorAll(".cart-badge").forEach(b => {
                        b.textContent = data.totalQuantity !== undefined ? data.totalQuantity : (parseInt(b.textContent) || 0) + 1;
                    });
                    if (callback) { callback(); return; }
                    showToast("Đã thêm tài liệu vào giỏ hàng!", "success");
                    const btnAdd = document.getElementById("btnAddToCart");
                    const btnBuy = document.getElementById("btnBuyNow");
                    if (btnAdd) btnAdd.outerHTML = '<a href="' + CTX + '/cart" class="btn-dp-incart"><i class="fa-solid fa-cart-shopping"></i> Xem giỏ hàng</a>';
                    if (btnBuy) btnBuy.style.display = "none";
                } else {
                    if (data.redirect) { window.location.href = data.redirect; return; }
                    showToast(data.message || "Vui lòng thử lại.", "error");
                }
            })
            .catch(() => showToast("Lỗi kết nối, vui lòng thử lại.", "error"));
    }

    document.getElementById("btnAddToCart")?.addEventListener("click", () => addToCart(null));
    document.getElementById("btnBuyNow")?.addEventListener("click", () => addToCart(() => { window.location.href = CTX + "/cart"; }));

    // Collapse / expand mô tả
    (function () {
        const wrap = document.getElementById("descWrap");
        const btn  = document.getElementById("toggleDesc");
        if (!wrap || !btn) return;
        let expanded = false;
        btn.addEventListener("click", () => {
            expanded = !expanded;
            wrap.classList.toggle("expanded", expanded);
            btn.textContent = expanded ? "Thu gọn ▲" : "Xem tất cả ▼";
        });
    })();
</script>
<script src="${pageContext.request.contextPath}/style/header/header.js"></script>
<script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
</body>
</html>