<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="user" value="${sessionScope.auth}"/>
<c:set var="customer" value="${sessionScope.customer}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="currentURI" value="${pageContext.request.requestURI}"/>

<c:if test="${not empty user}">
    <aside class="sidebar">
        <div class="sidebar-profile">
            <div class="avatar">
                <c:choose>
                    <c:when test="${not empty customer.fullName}">
                        ${fn:substring(customer.fullName, 0, 1)}
                    </c:when>
                    <c:otherwise>
                        ${fn:substring(user.username, 0, 1)}
                    </c:otherwise>
                </c:choose>
            </div>
            <p class="username">${user.username}</p>
        </div>

        <ul class="menu">
            <li class="${fn:contains(currentURI, '/update-profile') ? 'active' : ''}">
                <a href="${contextPath}/update-profile">
                    <i class="fa-regular fa-user"></i> Thông tin tài khoản
                </a>
            </li>

            <li class="${fn:contains(currentURI, 'my-courses') ? 'active' : ''}">
                <a href="${contextPath}/my-courses">
                    <i class="fa-solid fa-graduation-cap"></i> Khóa học của tôi
                </a>
            </li>

            <li class="${fn:contains(currentURI, 'my-documents') ? 'active' : ''}">
                <a href="${contextPath}/my-documents">
                    <i class="fa-solid fa-file-lines"></i> Tài liệu của tôi
                </a>
            </li>

            <li class="${fn:contains(currentURI, 'purchase-history') ? 'active' : ''}">
                <a href="${contextPath}/purchase-history">
                    <i class="fa-regular fa-clock"></i> Lịch sử mua hàng
                </a>
            </li>

            <li class="${fn:contains(currentURI, 'key-manager') ? 'active' : ''}">
                <a href="${contextPath}/key-manager">
                    <i class="fa-solid fa-key"></i> Quản lý khóa
                </a>
            </li>

            <li>
                <a href="${contextPath}/logout">
                    <i class="fa-solid fa-right-from-bracket"></i> Đăng xuất
                </a>
            </li>

        </ul>
    </aside>
</c:if>
