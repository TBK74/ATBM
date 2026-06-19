<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Tài liệu của tôi - EDUMART</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/Customer/Profile/profile.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
            </head>

            <body>
                <jsp:include page="/style/header/header.jsp" />

                <main class="container" style="display:flex;gap:24px;padding:32px 0;align-items:flex-start;">
                    <jsp:include page="/Customer/User_sidebar/user_sidebar.jsp" />

                    <section style="flex:1;">
                        <h1 style="margin-bottom:20px;">Tài liệu của tôi</h1>

                        <c:if test="${empty accesses}">
                            <div style="text-align:center;padding:60px 0;">
                                <i class="fa-solid fa-file-lines" style="font-size:48px;color:#ccc;"></i>
                                <p style="margin-top:12px;color:#888;">Bạn chưa mua tài liệu nào.</p>
                                <a href="${pageContext.request.contextPath}/documents"
                                    style="display:inline-block;margin-top:12px;padding:10px 20px;background:#3b6d11;color:#fff;border-radius:8px;text-decoration:none;">Khám
                                    phá tài liệu</a>
                            </div>
                        </c:if>

                        <div style="display:flex;flex-direction:column;gap:12px;">
                            <c:forEach var="a" items="${accesses}">
                                <div style="display:flex;align-items:center;gap:16px;border:1px solid #eee;border-radius:12px;padding:14px;">
                                    <img src="${a.documentThumbnail}" alt="${a.documentTitle}"
                                        style="width:60px;height:60px;object-fit:cover;border-radius:8px;">
                                    <div style="flex:1;">
                                        <h3 style="font-size:15px;margin-bottom:4px;">${a.documentTitle}</h3>
                                        <p style="font-size:13px;color:#888;">${a.fileType} · ${a.pageCount} trang ·
                                            Còn lại ${a.remainingDownloads}/${a.maxDownloads} lần tải</p>
                                    </div>
                                    <c:choose>
                                        <c:when test="${a.canDownload}">
                                            <a href="${pageContext.request.contextPath}/download?id=${a.documentId}"
                                                style="padding:8px 18px;background:#3b6d11;color:#fff;border-radius:6px;text-decoration:none;font-size:14px;white-space:nowrap;">
                                                <i class="fa-solid fa-download"></i> Tải xuống
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="padding:8px 18px;background:#eee;color:#999;border-radius:6px;font-size:14px;white-space:nowrap;">Đã
                                                hết lượt tải</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:forEach>
                        </div>
                    </section>
                </main>

                <jsp:include page="/style/footer/footer.jsp" />
                <script src="${pageContext.request.contextPath}/style/header/header.js"></script>
                <script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
            </body>

            </html>
