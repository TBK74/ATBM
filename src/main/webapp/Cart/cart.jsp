<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Giỏ hàng - EDUMART</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Cart/cart.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
            </head>

            <body>
                <jsp:include page="/style/header/header.jsp" />

                <main class="cart-container">
                    <h1>Giỏ hàng</h1>

                    <c:if test="${empty cart.data}">
                        <div class="empty-cart" style="text-align:center;padding:60px 0;">
                            <i class="fa-solid fa-cart-shopping" style="font-size:56px;color:#ccc;"></i>
                            <p style="margin:16px 0;color:#888;font-size:16px;">Giỏ hàng của bạn đang trống.</p>
                            <a href="${pageContext.request.contextPath}/courses" class="btn-checkout"
                                style="display:inline-block;text-decoration:none;">Tiếp tục mua sắm</a>
                        </div>
                    </c:if>

                    <c:if test="${not empty cart.data}">
                        <div class="cart-grid">
                            <section class="cart-list">
                                <table class="cart-table">
                                    <thead>
                                        <tr>
                                            <th>Sản phẩm</th>
                                            <th>Loại</th>
                                            <th>Giá</th>
                                            <th></th>
                                        </tr>
                                    </thead>

                                    <tbody id="cart-rows">
                                        <c:forEach var="entry" items="${cart.data}">
                                            <c:set var="item" value="${entry.value}" />
                                            <tr>
                                                <td>
                                                    <div class="cart-item">
                                                        <img class="cart-thumb" src="${item.thumbnailUrl}"
                                                            alt="${item.title}">
                                                        <div class="cart-name">
                                                            <a href="${pageContext.request.contextPath}/${item.detailUrl}"
                                                                style="color:inherit;text-decoration:none;">${item.title}</a>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <c:if test="${item.itemType == 'course'}">
                                                        <span class="tag" style="background:#e6f1fb;color:#185fa5;padding:3px 10px;border-radius:999px;font-size:12px;">Khóa
                                                            học</span>
                                                    </c:if>
                                                    <c:if test="${item.itemType == 'document'}">
                                                        <span class="tag" style="background:#eaf3de;color:#3b6d11;padding:3px 10px;border-radius:999px;font-size:12px;">Tài
                                                            liệu</span>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${item.price}" type="currency"
                                                        currencySymbol="đ" />
                                                </td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/cart/remove"
                                                        method="post">
                                                        <input type="hidden" name="type" value="${item.itemType}">
                                                        <input type="hidden" name="id" value="${item.itemId}">
                                                        <button type="submit" class="remove-btn" title="Xóa">
                                                            <i class="fa-solid fa-trash"></i>
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </section>

                            <aside class="cart-summary">
                                <h2>Thông tin đơn hàng</h2>
                                <div class="sum-row"><span>Tổng sản phẩm</span><strong
                                        id="sum-qty">${cart.totalQuantity}</strong></div>
                                <div class="sum-row"><span>Tạm tính</span><strong id="sum-price">
                                        <fmt:formatNumber value="${cart.totalPrice}" type="currency"
                                            currencySymbol="đ" />
                                    </strong></div>
                                <a href="${pageContext.request.contextPath}/checkout" class="btn-checkout"
                                    id="btn-checkout">Tiến hành đặt
                                    hàng</a>
                            </aside>
                        </div>
                    </c:if>
                </main>

                <jsp:include page="/style/footer/footer.jsp" />

                <script src="${pageContext.request.contextPath}/style/header/header.js"></script>
                <script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
            </body>

            </html>
