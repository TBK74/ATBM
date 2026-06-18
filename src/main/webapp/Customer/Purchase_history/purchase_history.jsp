<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="user" value="${sessionScope.auth}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Lịch sử mua hàng - EDUMART</title>

    <link rel="stylesheet" href="${contextPath}/Customer/Purchase_history/purchase_history.css"/>
    <link rel="stylesheet" href="${contextPath}/style/header/header.css"/>
    <link rel="stylesheet" href="${contextPath}/Customer/User_sidebar/user_sidebar.css"/>
    <link rel="stylesheet" href="${contextPath}/style/footer/footer.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        .order-card { border: 1px solid #ddd; margin-bottom: 20px; padding: 15px; border-radius: 8px; background: white; }
        .order-header { display: flex; justify-content: space-between; background: #f9f9f9; padding: 10px; border-bottom: 1px solid #eee; margin-bottom: 10px; }
        .order-item { display: flex; gap: 15px; margin-bottom: 10px; align-items: center; border-bottom: 1px dashed #eee; padding-bottom: 10px; }
        .item-img { width: 70px; height: 70px; object-fit: cover; border: 1px solid #eee; border-radius: 6px; }
        .status-Pending { color: #f39c12; font-weight: bold; }
        .status-Processing { color: #3498db; font-weight: bold; }
        .status-Completed { color: #2ecc71; font-weight: bold; }
        .status-Cancelled { color: #e74c3c; font-weight: bold; }
        .item-info { flex-grow: 1; }
        .item-type-badge { font-size: 11px; padding: 2px 8px; border-radius: 999px; margin-left: 8px; }
        .badge-course { background: #e6f1fb; color: #185fa5; }
        .badge-document { background: #eaf3de; color: #3b6d11; }
        .hidden { display: none !important; }
    </style>
</head>

<body>
<jsp:include page="/style/header/header.jsp"/>

<div class="user-container" style="display:flex;gap:24px;padding:32px 24px;max-width:1200px;margin:0 auto;">

    <jsp:include page="/Customer/User_sidebar/user_sidebar.jsp"/>

    <main class="content" style="flex:1;">
        <section class="order-history">
            <h2 style="padding-bottom: 10px;">Lịch sử đơn hàng</h2>

            <div class="tabs">
                <button class="tab active" onclick="filterOrders('all', this)">Tất cả</button>
                <button class="tab" onclick="filterOrders('Pending', this)">Chờ xác nhận</button>
                <button class="tab" onclick="filterOrders('Processing', this)">Đã xác nhận</button>
                <button class="tab" onclick="filterOrders('Completed', this)">Hoàn thành</button>
                <button class="tab" onclick="filterOrders('Cancelled', this)">Đã hủy</button>
            </div>

            <div class="search-box">
                <input type="text" id="searchInput" onkeyup="searchOrders()"
                       placeholder="Tìm theo mã đơn hàng, tên sản phẩm..."/>
                <button class="search-btn" onclick="searchOrders()"><i class="fa-solid fa-magnifying-glass"></i>
                </button>
            </div>

            <c:choose>
                <c:when test="${empty orders}">
                    <div class="empty-state" style="text-align:center;padding:60px 0;">
                        <i class="fa-solid fa-receipt" style="font-size:48px;color:#ccc;"></i>
                        <p style="margin-top:12px;color:#888;">Chưa có đơn hàng nào</p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div id="orderList">
                        <c:forEach var="order" items="${orders}">

                            <div class="order-card" data-status="${order.status}">

                                <div class="order-header">
                                    <div>
                                        <b class="order-id-text">Đơn hàng #${order.orderId}</b>
                                        <span style="font-size: 0.9em; color: #666; margin-left: 10px;">
                                        <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                    </div>

                                    <div class="status-${order.status}">
                                        ${order.statusVietnamese}
                                    </div>
                                </div>

                                <c:forEach var="item" items="${orderItemsMap[order.orderId]}">
                                    <div class="order-item">
                                        <img src="${item.itemThumbnail}" alt="${item.itemTitle}" class="item-img"
                                             onerror="this.src='https://placehold.co/70x70?text=No+Image'">

                                        <div class="item-info">
                                            <div class="search-target">
                                                <b>${item.itemTitle}</b>
                                                <c:if test="${item.itemType == 'course'}">
                                                    <span class="item-type-badge badge-course">Khóa học</span>
                                                </c:if>
                                                <c:if test="${item.itemType == 'document'}">
                                                    <span class="item-type-badge badge-document">Tài liệu</span>
                                                </c:if>
                                            </div>
                                            <div>
                                                <span style="color: #d70018;">
                                                <fmt:formatNumber value="${item.priceAtOrder}" type="currency"
                                                                  currencySymbol="₫"/>
                                            </span>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>

                                <div style="display: flex; justify-content: flex-end; align-items: flex-end; margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
                                    <div style="text-align: right;">
                                        Tổng tiền:
                                        <span style="font-size: 1.2em; color: #d70018; font-weight: bold;">
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency"
                                                          currencySymbol="₫"/>
                                    </span>

                                        <c:if test="${order.status == 'Pending'}">
                                            <div style="margin-top: 10px;">
                                                <button onclick="confirmCancelOrder(${order.orderId})"
                                                        style="background: #fff; border: 1px solid #d32f2f; color: #d32f2f; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
                                                    Hủy đơn
                                                </button>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

        </section>
    </main>
</div>

<jsp:include page="/style/footer/footer.jsp"/>

<c:if test="${not empty sessionScope.toastSuccess}">
    <div id="toast-msg"
         style="position: fixed; top: 20px; right: 20px; z-index: 9999; background: #d4edda; color: #155724; padding: 15px 20px; border-radius: 5px; border-left: 5px solid #28a745; box-shadow: 0 4px 6px rgba(0,0,0,0.1); animation: slideIn 0.5s ease-out;">
        <i class="fa-solid fa-circle-check"></i> <strong>Thành công!</strong> ${sessionScope.toastSuccess}
    </div>
    <c:remove var="toastSuccess" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.toastError}">
    <div id="toast-msg"
         style="position: fixed; top: 20px; right: 20px; z-index: 9999; background: #f8d7da; color: #721c24; padding: 15px 20px; border-radius: 5px; border-left: 5px solid #dc3545; box-shadow: 0 4px 6px rgba(0,0,0,0.1); animation: slideIn 0.5s ease-out;">
        <i class="fa-solid fa-circle-exclamation"></i> <strong>Lỗi!</strong> ${sessionScope.toastError}
    </div>
    <c:remove var="toastError" scope="session"/>
</c:if>

<style>
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
</style>

<script>
    setTimeout(function () {
        let toast = document.getElementById('toast-msg');
        if (toast) {
            toast.style.transition = "opacity 0.5s ease";
            toast.style.opacity = "0";
            setTimeout(() => toast.remove(), 500);
        }
    }, 3000);
</script>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const urlParams = new URLSearchParams(window.location.search);
        const activeTab = urlParams.get('tab');

        if (activeTab) {
            let buttons = document.querySelectorAll('.tab');
            buttons.forEach(btn => {
                let onClickText = btn.getAttribute('onclick').toLowerCase();
                if (onClickText.includes(activeTab.toLowerCase())) {
                    filterOrders(activeTab, btn);
                }
            });
        }
    });

    function filterOrders(statusType, btnElement) {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        if (btnElement) btnElement.classList.add('active');

        let cards = document.querySelectorAll('.order-card');
        cards.forEach(card => {
            let cardStatus = card.getAttribute('data-status');
            if (statusType === 'all' ||
                (cardStatus && cardStatus.toUpperCase() === statusType.toUpperCase())) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    function searchOrders() {
        let input = document.getElementById('searchInput').value.toLowerCase();
        let cards = document.querySelectorAll('.order-card');

        cards.forEach(card => {
            let idEl = card.querySelector('.order-id-text');
            let idText = idEl ? idEl.innerText.toLowerCase() : "";

            let hasProduct = false;
            let productNames = card.querySelectorAll('.search-target');
            productNames.forEach(name => {
                if (name.innerText.toLowerCase().includes(input)) hasProduct = true;
            });

            if (idText.includes(input) || hasProduct) {
                card.classList.remove('hidden');
                if (card.style.display !== 'none') card.style.display = 'block';
            } else {
                card.classList.add('hidden');
            }
        });
    }

    function confirmCancelOrder(orderId) {
        if (confirm("Bạn có chắc chắn muốn hủy đơn hàng #" + orderId + " không?")) {
            window.location.href = "${contextPath}/cancel-order?id=" + orderId;
        }
    }
</script>
</body>
</html>
