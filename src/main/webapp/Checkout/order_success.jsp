<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Đặt hàng thành công - EDUMART</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
            </head>

            <body>
                <jsp:include page="/style/header/header.jsp" />

                <main class="container" style="max-width:700px;margin:60px auto;text-align:center;">
                    <i class="fa-solid fa-circle-check" style="font-size:64px;color:#3b6d11;"></i>
                    <h1 style="margin:16px 0 8px;">Đặt hàng thành công!</h1>
                    <p style="color:#666;">Mã đơn hàng: <strong>#${order.orderId}</strong></p>
                    <p style="color:#666;margin-bottom:24px;">Tổng tiền: <strong>
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="đ" />
                        </strong></p>

                    <div style="text-align:left;border:1px solid #eee;border-radius:12px;padding:20px;margin-bottom:24px;">
                        <h3 style="margin-bottom:12px;">Sản phẩm đã mua</h3>
                        <c:forEach var="item" items="${items}">
                            <div style="display:flex;align-items:center;gap:12px;padding:8px 0;border-bottom:1px solid #f3f3f3;">
                                <img src="${item.itemThumbnail}" style="width:48px;height:48px;object-fit:cover;border-radius:6px;">
                                <span style="flex:1;">${item.itemTitle}</span>
                                <fmt:formatNumber value="${item.priceAtOrder}" type="currency" currencySymbol="đ" />
                            </div>
                        </c:forEach>
                    </div>

                    <div style="display:flex;gap:12px;justify-content:center;">
                        <a href="${pageContext.request.contextPath}/my-courses"
                            style="padding:12px 24px;background:#1a73e8;color:#fff;border-radius:8px;text-decoration:none;">Khóa
                            học của tôi</a>
                        <a href="${pageContext.request.contextPath}/my-documents"
                            style="padding:12px 24px;background:#3b6d11;color:#fff;border-radius:8px;text-decoration:none;">Tài
                            liệu của tôi</a>
                    </div>
                </main>

                <jsp:include page="/style/footer/footer.jsp" />
                <script src="${pageContext.request.contextPath}/style/header/header.js"></script>
                <script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
            </body>

            </html>
