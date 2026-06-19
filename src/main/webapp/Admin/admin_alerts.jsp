<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <title>EDUMART Admin — Cảnh báo bảo mật</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/admin.css?v=2"/>
    <style>
        .alert-row.unread { background: #fffbea; }
        .alert-row.critical td { border-left: 4px solid #dc3545; }
        .alert-row.warning  td:first-child { border-left: 4px solid #ffc107; }
        .alert-row.info     td:first-child { border-left: 4px solid #0dcaf0; }
        .badge { display:inline-block; padding:3px 8px; border-radius:4px; font-size:.75rem; font-weight:600; }
        .badge-danger  { background:#f8d7da; color:#721c24; }
        .badge-warning { background:#fff3cd; color:#856404; }
        .badge-info    { background:#cff4fc; color:#055160; }
        .badge-secondary { background:#e2e3e5; color:#41464b; }
        .btn-sm { padding:4px 10px; font-size:.8rem; }
        .unread-dot { display:inline-block; width:8px; height:8px; border-radius:50%; background:#dc3545; margin-right:6px; }
        .flash-success { background:#d1e7dd; border:1px solid #badbcc; color:#0f5132; padding:10px 14px;
            border-radius:6px; margin-bottom:12px; }
    </style>
</head>
<body>
<header class="site-header">
    <a href="overview" class="logo">EDUMART</a>
    <nav class="header-right">
        <span class="topbtn" style="cursor:default;">
            <i class="fa-solid fa-user"></i> ${sessionScope.auth.username}
        </span>
    </nav>
</header>

<div class="layout">
    <aside id="sidebar" class="sidebar">
        <div class="sidebar-title">Quản trị</div>
        <nav class="menu">
            <a class="menu-item" href="overview">Tổng quan</a>
            <a class="menu-item" href="accounts">Tài khoản</a>
            <a class="menu-item" href="courses">Khóa học</a>
            <a class="menu-item" href="documents">Tài liệu</a>
            <a class="menu-item" href="categories">Danh mục</a>
            <a class="menu-item" href="promocodes">Khuyến mãi</a>
            <a class="menu-item" href="orders">Đơn hàng</a>
            <a class="menu-item" href="edit-requests">Yêu cầu chỉnh sửa
                <c:if test="${not empty pendingEditCount and pendingEditCount > 0}">
                    <span class="badge badge-warning" style="margin-left:4px;">${pendingEditCount}</span>
                </c:if>
            </a>
            <a class="menu-item active" href="alerts">
                Cảnh báo
                <c:if test="${unreadCount > 0}">
                    <span class="badge badge-danger" style="margin-left:4px;">${unreadCount}</span>
                </c:if>
            </a>
        </nav>
        <div class="sidebar-logout">
            <a class="logout-btn" href="${pageContext.request.contextPath}/logout"
               onclick="return confirm('Bạn có chắc muốn đăng xuất?')">
                <i class="fa-solid fa-right-from-bracket"></i> Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content">
        <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:14px;">
            <h2><i class="fa-solid fa-shield-halved"></i> Cảnh báo bảo mật
                <c:if test="${unreadCount > 0}">
                    <span class="badge badge-danger">${unreadCount} chưa đọc</span>
                </c:if>
            </h2>
            <c:if test="${unreadCount > 0}">
                <form method="post" action="${pageContext.request.contextPath}/admin/alerts">
                    <input type="hidden" name="action" value="readAll"/>
                    <button class="btn btn-ghost btn-sm" type="submit">
                        <i class="fa-solid fa-check-double"></i> Đánh dấu tất cả đã đọc
                    </button>
                </form>
            </c:if>
        </div>

        <c:if test="${not empty flash}">
            <div class="flash-success"><i class="fa-solid fa-circle-check"></i> ${flash}</div>
            <c:remove var="flash" scope="session"/>
        </c:if>

        <section class="card">
            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Mức độ</th>
                        <th>Loại</th>
                        <th>Đơn hàng</th>
                        <th>Nội dung</th>
                        <th>Thời gian</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty alerts}">
                            <tr><td colspan="7" style="text-align:center; color:#888; padding:24px;">
                                <i class="fa-solid fa-circle-check" style="color:#198754;"></i>
                                Không có cảnh báo nào.
                            </td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${alerts}" var="a">
                                <tr class="alert-row ${a.read ? '' : 'unread'} ${a.severity}">
                                    <td>
                                            <span class="badge badge-${a.severityCss}">
                                                <c:choose>
                                                    <c:when test="${a.severity == 'critical'}">Nguy hiểm</c:when>
                                                    <c:when test="${a.severity == 'warning'}">Cảnh báo</c:when>
                                                    <c:otherwise>Thông tin</c:otherwise>
                                                </c:choose>
                                            </span>
                                    </td>
                                    <td>
                                        <i class="fa-solid ${a.typeIcon}"></i>
                                        <c:choose>
                                            <c:when test="${a.alertType == 'signature_mismatch'}">Chữ ký lệch</c:when>
                                            <c:when test="${a.alertType == 'key_revoked_orders'}">Key bị thu hồi</c:when>
                                            <c:when test="${a.alertType == 'edit_blocked'}">Chặn sửa đơn</c:when>
                                            <c:otherwise>${a.alertType}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${not empty a.orderId}">
                                            <a href="${pageContext.request.contextPath}/admin/orders">DH${a.orderId}</a>
                                        </c:if>
                                        <c:if test="${empty a.orderId}">—</c:if>
                                    </td>
                                    <td style="max-width:300px;">${a.message}</td>
                                    <td>
                                        <fmt:formatDate value="${a.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.read}">
                                                    <span style="color:#198754;">
                                                        <i class="fa-solid fa-check"></i> Đã xử lý
                                                    </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span><span class="unread-dot"></span>Chưa đọc</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${not a.read}">
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/admin/alerts"
                                                  style="display:inline;">
                                                <input type="hidden" name="action" value="read"/>
                                                <input type="hidden" name="id"     value="${a.alertId}"/>
                                                <button class="btn btn-ghost btn-sm" type="submit">
                                                    <i class="fa-solid fa-check"></i> Đã xử lý
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>