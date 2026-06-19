<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <title>EDUMART Admin — Yêu cầu chỉnh sửa đơn</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/admin.css?v=2"/>
    <style>
        .badge { display:inline-block; padding:3px 8px; border-radius:4px; font-size:.75rem; font-weight:600; }
        .badge-success   { background:#d1e7dd; color:#0f5132; }
        .badge-warning   { background:#fff3cd; color:#856404; }
        .badge-danger    { background:#f8d7da; color:#721c24; }
        .badge-secondary { background:#e2e3e5; color:#41464b; }
        .btn-sm { padding:4px 10px; font-size:.8rem; }
        .flash-success { background:#d1e7dd; border:1px solid #badbcc; color:#0f5132;
            padding:10px 14px; border-radius:6px; margin-bottom:12px; }
        .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,.4);
            z-index:1000; align-items:center; justify-content:center; }
        .modal-overlay.open { display:flex; }
        .modal-box { background:#fff; border-radius:8px; padding:24px; width:420px;
            box-shadow:0 4px 20px rgba(0,0,0,.2); }
        .modal-box h4 { margin:0 0 12px; }
        .modal-box textarea { width:100%; box-sizing:border-box; padding:8px;
            border:1px solid #ced4da; border-radius:4px; resize:vertical; }
        .modal-actions { margin-top:14px; display:flex; gap:8px; justify-content:flex-end; }
        .filter-tabs { display:flex; gap:8px; margin-bottom:14px; }
        .filter-tabs a { padding:6px 14px; border-radius:20px; text-decoration:none;
            background:#e9ecef; color:#495057; font-size:.88rem; }
        .filter-tabs a.active { background:#0d6efd; color:#fff; }
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
            <a class="menu-item active" href="edit-requests">Yêu cầu chỉnh sửa</a>
            <a class="menu-item" href="alerts">Cảnh báo</a>
        </nav>
        <div class="sidebar-logout">
            <a class="logout-btn" href="${pageContext.request.contextPath}/logout"
               onclick="return confirm('Bạn có chắc muốn đăng xuất?')">
                <i class="fa-solid fa-right-from-bracket"></i> Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content">
        <h2><i class="fa-solid fa-pen-to-square"></i> Yêu cầu chỉnh sửa đơn hàng</h2>

        <c:if test="${not empty flash}">
            <div class="flash-success"><i class="fa-solid fa-circle-check"></i> ${flash}</div>
            <c:remove var="flash" scope="session"/>
        </c:if>

        <%-- Tabs lọc --%>
        <div class="filter-tabs">
            <a href="edit-requests" class="${empty filter ? 'active' : ''}">Tất cả</a>
            <a href="edit-requests?filter=pending" class="${filter == 'pending' ? 'active' : ''}">
                Chờ duyệt
            </a>
        </div>

        <section class="card">
            <div class="table-wrap">
                <table class="table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Đơn hàng</th>
                        <th>Người yêu cầu</th>
                        <th>Thay đổi</th>
                        <th>Trạng thái cũ → mới</th>
                        <th>Thời gian</th>
                        <th>Trạng thái</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty editRequests}">
                            <tr><td colspan="8" style="text-align:center; color:#888; padding:24px;">
                                Không có yêu cầu nào.
                            </td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${editRequests}" var="r">
                                <tr>
                                    <td>${r.requestId}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/orders">
                                            DH${r.orderId}
                                        </a>
                                    </td>
                                    <td>${r.requestedByUsername}</td>
                                    <td style="max-width:220px; word-break:break-word;">
                                            ${r.changeSummary}
                                    </td>
                                    <td>
                                        <span class="badge badge-secondary">${r.oldStatus}</span>
                                        <i class="fa-solid fa-arrow-right" style="font-size:.7rem;"></i>
                                        <span class="badge badge-warning">${r.newStatus}</span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <span class="badge badge-${r.statusCss}">${r.statusVi}</span>
                                        <c:if test="${not empty r.rejectReason}">
                                            <br/><small style="color:#888;">${r.rejectReason}</small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${r.status == 'pending'}">
                                            <%-- Duyệt --%>
                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/admin/edit-requests"
                                                  style="display:inline;"
                                                  onsubmit="return confirm('Duyệt và áp dụng thay đổi cho đơn DH${r.orderId}?')">
                                                <input type="hidden" name="action" value="approve"/>
                                                <input type="hidden" name="id"     value="${r.requestId}"/>
                                                <button class="btn btn-ghost btn-sm" type="submit"
                                                        style="color:#198754; border-color:#198754;">
                                                    <i class="fa-solid fa-check"></i> Duyệt
                                                </button>
                                            </form>
                                            <%-- Từ chối → mở modal --%>
                                            <button class="btn btn-ghost btn-sm"
                                                    style="color:#dc3545; border-color:#dc3545;"
                                                    onclick="openRejectModal(${r.requestId}, 'DH${r.orderId}')">
                                                <i class="fa-solid fa-xmark"></i> Từ chối
                                            </button>
                                        </c:if>
                                        <c:if test="${r.status != 'pending'}">
                                            <small style="color:#888;">
                                                <fmt:formatDate value="${r.reviewedAt}" pattern="dd/MM HH:mm"/>
                                                — ${r.reviewedByUsername}
                                            </small>
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

<%-- Modal từ chối --%>
<div class="modal-overlay" id="rejectModal">
    <div class="modal-box">
        <h4><i class="fa-solid fa-xmark" style="color:#dc3545;"></i> Từ chối yêu cầu</h4>
        <p id="rejectDesc" style="color:#555; font-size:.9rem; margin-bottom:10px;"></p>
        <form method="post" action="${pageContext.request.contextPath}/admin/edit-requests">
            <input type="hidden" name="action" value="reject"/>
            <input type="hidden" name="id" id="rejectRequestId"/>
            <label style="font-size:.88rem; font-weight:600;">Lý do từ chối (bắt buộc):</label>
            <textarea name="reason" id="rejectReason" rows="3" required
                      placeholder="Nhập lý do từ chối..."></textarea>
            <div class="modal-actions">
                <button type="button" class="btn btn-ghost" onclick="closeRejectModal()">Hủy</button>
                <button type="submit" class="btn btn-ghost" style="color:#dc3545; border-color:#dc3545;">
                    Xác nhận từ chối
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function openRejectModal(requestId, orderLabel) {
        document.getElementById('rejectRequestId').value = requestId;
        document.getElementById('rejectDesc').textContent =
            'Yêu cầu #' + requestId + ' cho đơn ' + orderLabel;
        document.getElementById('rejectReason').value = '';
        document.getElementById('rejectModal').classList.add('open');
    }
    function closeRejectModal() {
        document.getElementById('rejectModal').classList.remove('open');
    }
    // Đóng modal khi click ngoài
    document.getElementById('rejectModal').addEventListener('click', function(e) {
        if (e.target === this) closeRejectModal();
    });
</script>
</body>
</html>