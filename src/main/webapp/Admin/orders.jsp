<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <!doctype html>
        <html lang="vi">

        <head>
            <meta charset="utf-8" />
            <title>EDUMART Admin — Đơn hàng</title>
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/admin.css?v=2" />
        </head>

        <body>
            <header class="site-header">
                <a href="overview" class="logo">EDUMART</a>
                <nav class="header-right">
                    <span class="topbtn" style="cursor: default;">
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
                        <a class="menu-item active" href="orders">Đơn hàng</a>
                    </nav>
                    <div class="sidebar-logout">
                        <a class="logout-btn" href="${pageContext.request.contextPath}/logout"
                            onclick="return confirm('Bạn có chắc muốn đăng xuất?')">
                            <i class="fa-solid fa-right-from-bracket"></i> Đăng xuất
                        </a>
                    </div>
                </aside>

                <main class="content">
                    <h2>Quản lý đơn hàng</h2>

                    <!-- BỘ LỌC -->
                    <section class="card" style="padding:12px; margin:10px 0 14px;">
                        <form class="form" action="orders" method="get"
                            style="display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:10px; align-items:end;">
                            <label>
                                Tên / Mã đơn
                                <input class="input" type="text" name="q" value="${msgName}" placeholder="Tên người nhận hoặc mã đơn..." />
                            </label>
                            <label>
                                Trạng thái
                                <select class="input" name="status">
                                    <option value="">Tất cả</option>
                                    <option value="Pending"    ${msgStatus=='Pending'    ? 'selected' : ''}>Chờ xác nhận</option>
                                    <option value="Processing" ${msgStatus=='Processing' ? 'selected' : ''}>Đã xác nhận</option>
                                    <option value="Completed"  ${msgStatus=='Completed'  ? 'selected' : ''}>Hoàn thành</option>
                                    <option value="Cancelled"  ${msgStatus=='Cancelled'  ? 'selected' : ''}>Đã hủy</option>
                                </select>
                            </label>
                            <label>
                                Từ ngày
                                <input class="input" type="date" name="dateFrom" value="${msgDateFrom}" />
                            </label>
                            <label>
                                Đến ngày
                                <input class="input" type="date" name="dateTo" value="${msgDateTo}" />
                            </label>
                            <div class="actions" style="margin:0;">
                                <button class="btn btn-ghost" type="submit">Lọc</button>
                                <a class="btn btn-ghost" href="orders">Reset</a>
                            </div>
                        </form>
                    </section>

                    <!-- BẢNG ĐƠN HÀNG -->
                    <section class="card">
                        <div class="table-wrap">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Mã đơn</th>
                                        <th>Người nhận</th>
                                        <th>Ngày đặt</th>
                                        <th>Tổng tiền (₫)</th>
                                        <th>Thanh toán</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${listO}" var="o">
                                        <tr>
                                            <td>DH${o.orderId}</td>
                                            <td>${o.recipientName}</td>
                                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></td>
                                            <td><fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="" /></td>
                                            <td>${o.paymentMethod}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${o.status == 'Pending'}"><span class="badge" style="background:#fff3cd;color:#854f0b;">Chờ xác nhận</span></c:when>
                                                    <c:when test="${o.status == 'Processing'}"><span class="badge" style="background:#e6f1fb;color:#185fa5;">Đã xác nhận</span></c:when>
                                                    <c:when test="${o.status == 'Completed'}"><span class="badge ok">Hoàn thành</span></c:when>
                                                    <c:when test="${o.status == 'Cancelled'}"><span class="badge danger">Đã hủy</span></c:when>
                                                </c:choose>
                                            </td>
                                            <td style="display:flex;gap:6px;flex-wrap:wrap;">
                                                <c:if test="${o.status == 'Pending'}">
                                                    <form action="orders" method="post" style="display:inline;">
                                                        <input type="hidden" name="csrf_token" value="${csrfToken}">
                                                        <input type="hidden" name="action" value="updateStatus">
                                                        <input type="hidden" name="id" value="${o.orderId}">
                                                        <input type="hidden" name="status" value="Processing">
                                                        <button class="btn" type="submit" style="padding:4px 10px;">Xác nhận</button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${o.status == 'Processing'}">
                                                    <form action="orders" method="post" style="display:inline;">
                                                        <input type="hidden" name="csrf_token" value="${csrfToken}">
                                                        <input type="hidden" name="action" value="updateStatus">
                                                        <input type="hidden" name="id" value="${o.orderId}">
                                                        <input type="hidden" name="status" value="Completed">
                                                        <button class="btn" type="submit" style="padding:4px 10px;">Hoàn thành</button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${o.status == 'Pending' || o.status == 'Processing'}">
                                                    <form action="orders" method="post" style="display:inline;" onsubmit="return confirm('Hủy đơn hàng này?')">
                                                        <input type="hidden" name="csrf_token" value="${csrfToken}">
                                                        <input type="hidden" name="action" value="updateStatus">
                                                        <input type="hidden" name="id" value="${o.orderId}">
                                                        <input type="hidden" name="status" value="Cancelled">
                                                        <button class="btn btn-danger" type="submit" style="padding:4px 10px;">Hủy</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </main>
            </div>
        </body>

        </html>