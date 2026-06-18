<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <header class="site-header">
                <div class="header-top">
                    <div class="container header-top-inner">
                        <a href="${pageContext.request.contextPath}/home" class="logo-block" aria-label="Trang chủ">
                            <span class="logo-mark"><i class="fa-solid fa-graduation-cap"></i></span>
                            <span class="logo-text-wrap">
                                <span class="logo-main">EDUMART</span>
                                <span class="logo-sub">Khóa học & Tài liệu học tập</span>
                            </span>
                        </a>

                        <form class="searchbar" action="${pageContext.request.contextPath}/courses" method="get">
                            <input type="text" name="q" placeholder="Tìm khóa học, tài liệu..." />
                            <button type="submit">Tìm kiếm</button>
                        </form>

                        <div class="header-contact">
                            <a class="contact-item" href="tel:0888999406">
                                <i class="fa-solid fa-phone-volume"></i>
                                <span class="contact-text">
                                    <span>Hỗ trợ học viên</span>
                                    <strong>0888.999.406</strong>
                                </span>
                            </a>
                        </div>

                        <c:choose>
                            <c:when test="${sessionScope.auth != null}">
                                <a class="account-link" href="${pageContext.request.contextPath}/update-profile">
                                    <i class="fa-solid fa-user"></i>
                                    ${sessionScope.auth.username}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="account-link" href="${pageContext.request.contextPath}/login">
                                    <i class="fa-solid fa-user"></i>
                                    Đăng nhập
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="header-nav-wrap">
                    <div class="container header-nav-inner">
                        <a class="menu-trigger" href="${pageContext.request.contextPath}/home">
                            <i class="fa-solid fa-bars"></i>
                            DANH MỤC
                        </a>

                        <nav class="nav-links" aria-label="Điều hướng chính">
                            <a href="${pageContext.request.contextPath}/home">TRANG CHỦ</a>
                            <a href="${pageContext.request.contextPath}/courses">KHÓA HỌC</a>
                            <a href="${pageContext.request.contextPath}/documents">TÀI LIỆU</a>
                            <a href="${pageContext.request.contextPath}/about">GIỚI THIỆU</a>
                            <a href="${pageContext.request.contextPath}/contact">LIÊN HỆ</a>
                        </nav>

                        <div class="cart-wrap">
                            <a class="cart-btn nav-cart-btn" href="${pageContext.request.contextPath}/cart">
                                <i class="fa-solid fa-cart-shopping"></i>
                                GIỎ HÀNG
                                <span class="cart-badge" aria-label="Số lượng">${sessionScope.cart.totalQuantity !=
                                    null ? sessionScope.cart.totalQuantity : 0}</span>
                            </a>

                            <div class="mini-cart" role="dialog" aria-label="Sản phẩm mới thêm">
                                <ul class="mini-cart-list" id="mini-cart-list">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.cart.data}">
                                            <c:forEach var="entry" items="${sessionScope.cart.data}">
                                                <c:set var="item" value="${entry.value}" />
                                                <li class="mini-cart-item">
                                                    <img src="${item.thumbnailUrl}" alt="${item.title}"
                                                        class="mini-cart-thumb">
                                                    <div class="mini-cart-info">
                                                        <div class="mini-cart-title">${item.title}</div>
                                                        <div class="mini-cart-meta">
                                                            <span class="qty">
                                                                <c:if test="${item.itemType == 'course'}">Khóa học</c:if>
                                                                <c:if test="${item.itemType == 'document'}">Tài liệu</c:if>
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div class="mini-cart-price">
                                                        <fmt:formatNumber value="${item.price}" type="currency"
                                                            currencySymbol="đ" />
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="mini-cart-item" style="justify-content: center;">
                                                <span>Giỏ hàng trống</span>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                                <div class="mini-cart-footer">
                                    <div class="mini-cart-total">
                                        <span>Tổng:</span>
                                        <strong id="mini-cart-total">
                                            <fmt:formatNumber
                                                value="${sessionScope.cart.totalPrice != null ? sessionScope.cart.totalPrice : 0}"
                                                type="currency" currencySymbol="đ" />
                                        </strong>
                                    </div>
                                    <a class="mini-cart-view" href="${pageContext.request.contextPath}/cart">Xem giỏ
                                        hàng</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </header>
