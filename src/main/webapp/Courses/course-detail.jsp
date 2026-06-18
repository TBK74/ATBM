<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8" />
    <title>${course.title} | EDUMART</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/ProductDetail.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Home/Product/HomeProduct.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/view.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product_Detail/CSS/inforProduct.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        #toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        .toast {
            padding: 12px 20px;
            border-radius: 6px;
            color: #fff;
            font-size: 14px;
            min-width: 250px;
            box-shadow: 0 4px 12px rgba(0,0,0,.2);
            animation: slideIn .3s ease, fadeOut .5s ease 2.5s forwards;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .toast.success { background: #28a745; }
        .toast.error   { background: #dc3545; }
        @keyframes slideIn {
            from { opacity: 0; transform: translateX(100%); }
            to   { opacity: 1; transform: translateX(0); }
        }
        @keyframes fadeOut {
            from { opacity: 1; }
            to   { opacity: 0; pointer-events: none; }
        }
        .toggle-btn-wrap { text-align: center; margin-top: 12px; }
        .toggle-btn {
            background: none;
            border: 1px solid #2563b8;
            color: #2563b8;
            border-radius: 6px;
            padding: 6px 18px;
            font-size: 13px;
            cursor: pointer;
            transition: background .15s;
        }
        .toggle-btn:hover { background: #eff7ff; }
        .collapse-wrap { max-height: 300px; overflow: hidden; transition: max-height .3s ease; }
        .collapse-wrap.expanded { max-height: none; }
    </style>
</head>
<body>
<div id="toast-container"></div>
<jsp:include page="/style/header/header.jsp" />

<div class="container">
    <div class="product">
        <div class="product-gallery" id="gallery">
            <div class="main-image" aria-live="polite">
                <img id="mainImg" src="${course.thumbnailUrl}" alt="${course.title}">
            </div>
        </div>

        <div class="detail">
            <section id="product-panel" class="product-panel">
                <h1 class="prod-title">${course.title}</h1>
                <p style="color:#666;margin-top:6px;">${course.shortDesc}</p>
                <div class="divider"></div>

                <div class="price-wrap">
                    <div class="price-new" id="price-new">
                        <c:choose>
                            <c:when test="${course.price == 0}">Miễn phí</c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${course.price}" type="currency" currencySymbol="đ" />
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${course.oldPrice > course.price}">
                        <div class="price-old" id="price-old">
                            <fmt:formatNumber value="${course.oldPrice}" type="currency" currencySymbol="đ" />
                        </div>
                        <div class="price-off" id="price-off">
                            -<fmt:formatNumber value="${(1 - course.price / course.oldPrice) * 100}" maxFractionDigits="0" />%
                        </div>
                    </c:if>
                </div>

                <div class="grid">
                    <div class="label">Giảng viên:</div>
                    <div class="status">${course.instructor}</div>

                    <div class="label">Cấp độ:</div>
                    <div class="status">${course.levelVietnamese}</div>

                    <div class="label">Thời lượng:</div>
                    <div class="status">${course.durationHours} giờ học</div>

                    <div class="label">Ngôn ngữ:</div>
                    <div class="status">${course.language}</div>
                </div>

                <div class="qty-row">
                    <c:choose>
                        <c:when test="${isEnrolled}">
                            <a href="${pageContext.request.contextPath}/my-courses" class="btn btn-buy"
                               style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;">
                                <i class="fa-solid fa-play"></i> Vào học ngay
                            </a>
                        </c:when>
                        <c:when test="${inCart}">
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-call"
                               style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;">
                                <i class="fa-solid fa-cart-shopping"></i> Đã có trong giỏ — Xem giỏ hàng
                            </a>
                        </c:when>
                        <c:otherwise>
                            <button type="button" id="btnAddToCart" class="add-cart">
                                <i class="fa-solid fa-cart-plus"></i> Thêm vào giỏ
                            </button>
                            <button type="button" id="btnBuyNow" class="btn btn-buy"
                                    style="margin-left:8px;">
                                <i class="fa-solid fa-bolt"></i> Mua ngay
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="benefits">
                    <div class="benefit"><i class="fa-solid fa-infinity"></i>Học không giới hạn thời gian sau khi mua</div>
                    <div class="benefit"><i class="fa-solid fa-certificate"></i>Nhận chứng chỉ hoàn thành khóa học</div>
                    <div class="benefit"><i class="fa-solid fa-mobile-screen"></i>Học trên mọi thiết bị: máy tính, điện thoại, máy tính bảng</div>
                </div>
            </section>
        </div>

        <div class="product-info">
            <article class="product-card">
                <div id="descWrap" class="collapse-wrap">
                    ${course.description}
                </div>
                <div class="toggle-btn-wrap">
                    <button id="toggleDesc" class="toggle-btn">Xem tất cả ▼</button>
                </div>
            </article>
        </div>

        <div id="viewed-root">
            <aside class="viewed-panel viewed--compact" aria-labelledby="viewed-title">
                <div class="viewed-header">
                    <h3 id="viewed-title">Khóa học liên quan</h3>
                    <a class="btn-view-all" href="${pageContext.request.contextPath}/courses">Xem tất cả</a>
                </div>
                <ul class="viewed-list" role="list">
                    <c:forEach var="rc" items="${relatedCourses}">
                        <li class="viewed-item">
                            <div class="viewed-thumb">
                                <c:if test="${not empty rc.badge}">
                                    <span class="badge-sale">${rc.badge}</span>
                                </c:if>
                                <img src="${rc.thumbnailUrl}" alt="${rc.title}" loading="lazy">
                            </div>
                            <div class="viewed-info">
                                <a href="${pageContext.request.contextPath}/course-detail?id=${rc.id}" class="viewed-name">${rc.title}</a>
                                <div class="viewed-price">
                                    <span class="price-now">
                                        <c:choose>
                                            <c:when test="${rc.price == 0}">Miễn phí</c:when>
                                            <c:otherwise><fmt:formatNumber value="${rc.price}" type="currency" currencySymbol="đ" /></c:otherwise>
                                        </c:choose>
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

<script>
    const COURSE_ID = ${course.id};
    const CTX = "${pageContext.request.contextPath}";

    function showToast(message, type) {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = 'toast ' + (type || 'success');
        toast.innerHTML = '<i class="fa-solid ' + (type === 'error' ? 'fa-circle-xmark' : 'fa-circle-check') + '"></i> ' + message;
        container.appendChild(toast);
        setTimeout(() => toast.remove(), 3000);
    }

    function addToCart(callback) {
        fetch(CTX + "/cart/add", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "type=course&id=" + COURSE_ID
        })
            .then(res => res.json())
            .then(data => {
                if (data.status === "success" || data.success) {
                    const badges = document.querySelectorAll(".cart-badge");
                    badges.forEach(b => { if (data.totalQuantity !== undefined) b.textContent = data.totalQuantity; });
                    if (callback) {
                        callback();
                    } else {
                        showToast("Đã thêm khóa học vào giỏ hàng!", "success");
                        // Cập nhật nút sau khi thêm thành công
                        const btnAdd = document.getElementById("btnAddToCart");
                        const btnBuy = document.getElementById("btnBuyNow");
                        if (btnAdd) {
                            btnAdd.outerHTML = '<a href="' + CTX + '/cart" class="btn btn-call" style="text-decoration:none;display:inline-flex;align-items:center;gap:8px;"><i class="fa-solid fa-cart-shopping"></i> Đã có trong giỏ — Xem giỏ hàng</a>';
                        }
                        if (btnBuy) btnBuy.style.display = "none";
                    }
                } else {
                    const msg = data.message || "Vui lòng đăng nhập để thêm vào giỏ.";
                    if (msg.toLowerCase().includes("login") || msg.toLowerCase().includes("đăng nhập")) {
                        window.location.href = CTX + "/login";
                    } else {
                        showToast("Lỗi: " + msg, "error");
                    }
                }
            })
            .catch(err => {
                console.error(err);
                showToast("Đã có lỗi xảy ra, vui lòng thử lại.", "error");
            });
    }

    const btnAdd = document.getElementById("btnAddToCart");
    const btnBuy = document.getElementById("btnBuyNow");

    if (btnAdd) btnAdd.addEventListener("click", () => addToCart(null));
    if (btnBuy) btnBuy.addEventListener("click", () => addToCart(() => { window.location.href = CTX + "/cart"; }));

    // Description collapse
    document.addEventListener("DOMContentLoaded", () => {
        const wrap = document.getElementById("descWrap");
        const btn  = document.getElementById("toggleDesc");
        if (!wrap || !btn) return;

        let expanded = false;
        wrap.style.maxHeight = "300px";

        btn.addEventListener("click", () => {
            expanded = !expanded;
            if (expanded) {
                wrap.style.maxHeight = wrap.scrollHeight + "px";
                wrap.classList.add("expanded");
                btn.textContent = "Thu gọn ▲";
            } else {
                wrap.style.maxHeight = "300px";
                wrap.classList.remove("expanded");
                btn.textContent = "Xem tất cả ▼";
            }
        });
    });
</script>
<script src="${pageContext.request.contextPath}/style/header/header.js"></script>
<script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
</body>
</html>