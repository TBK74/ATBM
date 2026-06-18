<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <!doctype html>
            <html lang="vi">

            <head>
                <meta charset="utf-8" />
                <title>EDUMART Admin — Khóa học</title>
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
                            <a class="menu-item active" href="courses">Khóa học</a>
                            <a class="menu-item" href="documents">Tài liệu</a>
                            <a class="menu-item" href="categories">Danh mục</a>
                            <a class="menu-item" href="promocodes">Khuyến mãi</a>
                            <a class="menu-item" href="orders">Đơn hàng</a>
                        </nav>
                        <div class="sidebar-logout">
                            <a class="logout-btn" href="${pageContext.request.contextPath}/logout"
                                onclick="return confirm('Bạn có chắc muốn đăng xuất?')">
                                <i class="fa-solid fa-right-from-bracket"></i> Đăng xuất
                            </a>
                        </div>
                    </aside>

                    <main class="content">
                        <h2>Quản lý khóa học</h2>

                        <!-- BỘ LỌC -->
                        <section class="card" style="padding:12px; margin:10px 0 14px;">
                            <form class="form" action="courses" method="get"
                                style="display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:10px; align-items:end;">
                                <label>
                                    Tên khóa học
                                    <input class="input" type="text" name="search" value="${param.search}"
                                        placeholder="Tìm theo tên..." />
                                </label>
                                <label>
                                    Danh mục
                                    <select class="input" name="category">
                                        <option value="">Tất cả</option>
                                        <c:forEach items="${categories}" var="cat">
                                            <option value="${cat.categoryID}" ${param.category == cat.categoryID ? 'selected' : ''}>${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>
                                    Cấp độ
                                    <select class="input" name="level">
                                        <option value="">Tất cả</option>
                                        <option value="beginner" ${param.level == 'beginner' ? 'selected' : ''}>Cơ bản</option>
                                        <option value="intermediate" ${param.level == 'intermediate' ? 'selected' : ''}>Trung cấp</option>
                                        <option value="advanced" ${param.level == 'advanced' ? 'selected' : ''}>Nâng cao</option>
                                    </select>
                                </label>
                                <div class="actions" style="margin:0;">
                                    <button class="btn btn-ghost" type="submit">Lọc</button>
                                    <a class="btn btn-ghost" href="courses">Reset</a>
                                </div>
                            </form>
                        </section>

                        <div class="actions" style="gap:8px; flex-wrap:wrap; align-items:center;">
                            <a class="btn" href="#form-add" onclick="document.getElementById('addCourseForm').style.display='block'">Thêm khóa học</a>
                        </div>

                        <!-- FORM THÊM KHÓA HỌC (ẩn mặc định) -->
                        <section class="card" id="addCourseForm" style="display:none;padding:16px;margin:14px 0;">
                            <h3>Thêm khóa học mới</h3>
                            <form action="courses" method="post" style="display:grid;grid-template-columns:repeat(2,1fr);gap:12px;">
                                <input type="hidden" name="action" value="add">
                                <label>Tên khóa học <input class="input" name="title" required></label>
                                <label>Giảng viên (AccountID) <input class="input" name="instructorId" type="number" required></label>
                                <label>Danh mục
                                    <select class="input" name="categoryId" required>
                                        <c:forEach items="${categories}" var="cat">
                                            <option value="${cat.categoryID}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>Cấp độ
                                    <select class="input" name="level">
                                        <option value="beginner">Cơ bản</option>
                                        <option value="intermediate">Trung cấp</option>
                                        <option value="advanced">Nâng cao</option>
                                    </select>
                                </label>
                                <label>Giá <input class="input" name="price" type="number" required></label>
                                <label>Giá gốc <input class="input" name="oldPrice" type="number"></label>
                                <label>Ngôn ngữ <input class="input" name="language" value="Tiếng Việt"></label>
                                <label>Thời lượng (giờ) <input class="input" name="durationHours" type="number" step="0.5"></label>
                                <label>Ảnh thumbnail (URL) <input class="input" name="thumbnailUrl"></label>
                                <label>Badge <input class="input" name="badge" placeholder="VD: Bán chạy"></label>
                                <label style="grid-column:1/-1;">Mô tả ngắn <input class="input" name="shortDesc"></label>
                                <label style="grid-column:1/-1;">Mô tả đầy đủ <textarea class="input" name="description" rows="3"></textarea></label>
                                <div style="grid-column:1/-1;display:flex;gap:8px;">
                                    <button class="btn" type="submit">Lưu</button>
                                    <button class="btn btn-ghost" type="button" onclick="document.getElementById('addCourseForm').style.display='none'">Hủy</button>
                                </div>
                            </form>
                        </section>

                        <!-- BẢNG KHÓA HỌC -->
                        <section class="card">
                            <div class="table-wrap">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Mã</th>
                                            <th>Ảnh</th>
                                            <th>Tên</th>
                                            <th>Giảng viên</th>
                                            <th>Danh mục</th>
                                            <th>Cấp độ</th>
                                            <th>Giá (₫)</th>
                                            <th>Đã bán</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${courses}" var="c">
                                            <tr>
                                                <td>KH${c.id}</td>
                                                <td><img src="${c.thumbnailUrl}" alt="" style="width:40px;height:40px;object-fit:cover;border-radius:4px;"></td>
                                                <td>${c.title}</td>
                                                <td>${c.instructor}</td>
                                                <td>${c.categoryName}</td>
                                                <td>${c.levelVietnamese}</td>
                                                <td><fmt:formatNumber value="${c.price}" type="currency" currencySymbol="" /></td>
                                                <td>${c.soldCount}</td>
                                                <td>
                                                    <form action="courses" method="post" style="display:inline;" onsubmit="return confirm('Xóa khóa học này?')">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${c.id}">
                                                        <button class="btn btn-danger" type="submit" style="padding:4px 10px;">Xóa</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </section>

                        <section class="card" style="margin-top:16px; padding:14px; display:flex; justify-content:space-between; align-items:center;">
                            <div style="color:#444;">Trang <strong>${currentPage}</strong> / ${totalPages}</div>
                            <div class="pagination" style="display:flex; gap:6px; flex-wrap:wrap;">
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <a class="btn ${i == currentPage ? 'btn-primary' : 'btn btn-ghost'}" href="courses?page=${i}">${i}</a>
                                </c:forEach>
                            </div>
                        </section>
                    </main>
                </div>
            </body>

            </html>
