<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Khóa học của tôi - EDUMART</title>
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
                        <h1 style="margin-bottom:20px;">Khóa học của tôi</h1>

                        <c:if test="${empty enrollments}">
                            <div style="text-align:center;padding:60px 0;">
                                <i class="fa-solid fa-graduation-cap" style="font-size:48px;color:#ccc;"></i>
                                <p style="margin-top:12px;color:#888;">Bạn chưa đăng ký khóa học nào.</p>
                                <a href="${pageContext.request.contextPath}/courses"
                                    style="display:inline-block;margin-top:12px;padding:10px 20px;background:#1a73e8;color:#fff;border-radius:8px;text-decoration:none;">Khám
                                    phá khóa học</a>
                            </div>
                        </c:if>

                        <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:20px;">
                            <c:forEach var="e" items="${enrollments}">
                                <div style="border:1px solid #eee;border-radius:12px;overflow:hidden;">
                                    <img src="${e.courseThumbnail}" alt="${e.courseTitle}"
                                        style="width:100%;height:140px;object-fit:cover;">
                                    <div style="padding:14px;">
                                        <h3 style="font-size:15px;margin-bottom:4px;">${e.courseTitle}</h3>
                                        <p style="font-size:13px;color:#888;margin-bottom:10px;">GV: ${e.instructorName}</p>
                                        <div style="background:#eee;border-radius:999px;height:8px;overflow:hidden;margin-bottom:6px;">
                                            <div style="background:#1a73e8;height:100%;width:${e.progressPercent}%;"></div>
                                        </div>
                                        <p style="font-size:12px;color:#888;margin-bottom:10px;">${e.progressPercent}% hoàn thành
                                            <c:if test="${e.completed}"> · <span style="color:#3b6d11;">Đã hoàn thành</span></c:if>
                                        </p>
                                        <a href="${pageContext.request.contextPath}/course-detail?id=${e.courseId}"
                                            style="display:block;text-align:center;padding:8px;background:#1a73e8;color:#fff;border-radius:6px;text-decoration:none;font-size:14px;">Vào
                                            học</a>
                                    </div>
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
