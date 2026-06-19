<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <!doctype html>
            <html lang="vi">

            <head>
                <meta charset="utf-8" />
                <title>EDUMART Admin — Tài liệu</title>
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
                            <a class="menu-item active" href="documents">Tài liệu</a>
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
                        <h2>Quản lý tài liệu</h2>

                        <section class="card" style="padding:12px; margin:10px 0 14px;">
                            <form class="form" action="documents" method="get"
                                style="display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:10px; align-items:end;">
                                <label>
                                    Tên tài liệu
                                    <input class="input" type="text" name="search" value="${param.search}"
                                        placeholder="Tìm theo tên..." />
                                </label>
                                <div class="actions" style="margin:0;">
                                    <button class="btn btn-ghost" type="submit">Lọc</button>
                                    <a class="btn btn-ghost" href="documents">Reset</a>
                                </div>
                            </form>
                        </section>

                        <div class="actions" style="gap:8px; flex-wrap:wrap; align-items:center;">
                            <a class="btn" href="#form-add" onclick="document.getElementById('addDocForm').style.display='block'">Thêm tài liệu</a>
                        </div>

                        <section class="card" id="addDocForm" style="display:none;padding:16px;margin:14px 0;">
                            <h3>Thêm tài liệu mới</h3>
                            <form action="documents" method="post" style="display:grid;grid-template-columns:repeat(2,1fr);gap:12px;">
                                <input type="hidden" name="csrf_token" value="${csrfToken}">
                                <input type="hidden" name="action" value="add">
                                <label>Tên tài liệu <input class="input" name="title" required></label>
                                <label>Tác giả <input class="input" name="author"></label>
                                <label>Danh mục
                                    <select class="input" name="categoryId" required>
                                        <c:forEach items="${categories}" var="cat">
                                            <option value="${cat.categoryID}">${cat.categoryName}</option>
                                        </c:forEach>
                                    </select>
                                </label>
                                <label>Loại file
                                    <select class="input" name="fileType">
                                        <option value="PDF">PDF</option>
                                        <option value="DOCX">DOCX</option>
                                        <option value="PPTX">PPTX</option>
                                    </select>
                                </label>
                                <label>Giá <input class="input" name="price" type="number" required></label>
                                <label>Giá gốc <input class="input" name="oldPrice" type="number"></label>
                                <label>Số trang <input class="input" name="pageCount" type="number"></label>
                                <label>Kích thước (KB) <input class="input" name="fileSizeKb" type="number"></label>
                                <label>Ảnh thumbnail (URL) <input class="input" name="thumbnailUrl"></label>
                                <label>Badge <input class="input" name="badge" placeholder="VD: Mới"></label>
                                <label style="grid-column:1/-1;">Mô tả <textarea class="input" name="description" rows="3"></textarea></label>
                                <div style="grid-column:1/-1;display:flex;gap:8px;">
                                    <button class="btn" type="submit">Lưu</button>
                                    <button class="btn btn-ghost" type="button" onclick="document.getElementById('addDocForm').style.display='none'">Hủy</button>
                                </div>
                            </form>
                        </section>

                        <section class="card">
                            <div class="table-wrap">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Mã</th>
                                            <th>Ảnh</th>
                                            <th>Tên</th>
                                            <th>Tác giả</th>
                                            <th>Loại file</th>
                                            <th>Giá (₫)</th>
                                            <th>Đã bán</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${documents}" var="d">
                                            <tr>
                                                <td>TL${d.id}</td>
                                                <td><img src="${d.thumbnailUrl}" alt="" style="width:40px;height:40px;object-fit:cover;border-radius:4px;"></td>
                                                <td>${d.title}</td>
                                                <td>${d.author}</td>
                                                <td>${d.fileType}</td>
                                                <td><fmt:formatNumber value="${d.price}" type="currency" currencySymbol="" /></td>
                                                <td>${d.soldCount}</td>
                                                <td>
                                                    <form action="documents" method="post" style="display:inline;" onsubmit="return confirm('Xóa tài liệu này?')">
                                                        <input type="hidden" name="csrf_token" value="${csrfToken}">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${d.id}">
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
                                    <a class="btn ${i == currentPage ? 'btn-primary' : 'btn btn-ghost'}" href="documents?page=${i}">${i}</a>
                                </c:forEach>
                            </div>
                        </section>
                    </main>
                </div>
            </body>

            </html>