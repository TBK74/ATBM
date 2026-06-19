<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Thanh toán - EDUMART</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css">
    <style>
        .co-page {
            max-width: 1100px;
            margin: 24px auto 56px;
            padding: 0 12px;
        }
        .co-page > h1 {
            margin: 0 0 18px;
            font-size: 22px;
            font-weight: 800;
            color: #124a91;
        }
        .co-grid {
            display: grid;
            grid-template-columns: minmax(0, 1fr) 320px;
            gap: 16px;
            align-items: start;
        }
        .co-card {
            background: #fff;
            border: 1px solid #dbe7f5;
            border-radius: 12px;
            box-shadow: 0 16px 26px -28px rgba(10,70,139,.82);
            padding: 20px 22px;
            margin-bottom: 14px;
        }
        .co-card:last-child { margin-bottom: 0; }
        .co-card-title {
            font-size: 14px;
            font-weight: 700;
            color: #124a91;
            margin: 0 0 16px;
            padding-bottom: 10px;
            border-bottom: 1px solid #ebf2fa;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .co-card-title i { color: #2563b8; font-size: 13px; }
        .co-field { margin-bottom: 12px; }
        .co-field:last-child { margin-bottom: 0; }
        .co-field label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #2f537b;
            text-transform: uppercase;
            letter-spacing: .3px;
            margin-bottom: 5px;
        }
        .co-field label i { margin-right: 4px; color: #2563b8; }
        .co-input {
            width: 100%;
            padding: 9px 12px;
            border: 1.5px solid #d3e4f6;
            border-radius: 8px;
            font-size: 14px;
            color: #1a3a5c;
            background: #fff;
            box-sizing: border-box;
            transition: border-color .18s, box-shadow .18s;
            outline: none;
        }
        .co-input:focus {
            border-color: #2563b8;
            box-shadow: 0 0 0 3px rgba(37,99,184,.1);
        }
        .co-promo-wrap { display: flex; gap: 8px; }
        .co-promo-wrap .co-input { flex: 1; }
        .btn-promo {
            padding: 9px 18px;
            background: linear-gradient(145deg, #0f6fda, #0c57af);
            color: #fff;
            border: none;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 700;
            cursor: pointer;
            white-space: nowrap;
            transition: filter .2s;
        }
        .btn-promo:hover { filter: brightness(1.08); }
        .btn-promo:disabled { background: #95a5a6; cursor: not-allowed; filter: none; }
        .co-promo-msg { margin-top: 6px; font-size: 13px; min-height: 18px; }
        .pm-option { cursor: pointer; margin-bottom: 8px; }
        .pm-option:last-child { margin-bottom: 0; }
        .pm-inner {
            border: 2px solid #d3e4f6;
            border-radius: 9px;
            padding: 11px 14px;
            display: flex;
            align-items: center;
            gap: 12px;
            background: #fff;
            transition: border-color .18s, background .18s;
        }
        .pm-inner:hover { border-color: #aac8ee; }
        .pm-icon { font-size: 20px; flex-shrink: 0; }
        .pm-label { font-weight: 700; font-size: 14px; }
        .pm-desc { font-size: 12px; color: #7a99bb; margin-top: 1px; }
        .co-sidebar { position: sticky; top: 80px; }
        .order-item-row {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 8px 0;
            border-bottom: 1px dashed #ebf2fa;
        }
        .order-item-row:last-of-type { border-bottom: none; }
        .order-item-thumb {
            width: 46px;
            height: 46px;
            object-fit: cover;
            border-radius: 6px;
            flex-shrink: 0;
        }
        .oitem-name {
            flex: 1;
            font-size: 13px;
            color: #2f537b;
            line-height: 1.4;
        }
        .oitem-type {
            font-size: 11px;
            color: #8eaac9;
            display: block;
        }
        .oitem-price {
            font-weight: 700;
            color: #0e4a90;
            white-space: nowrap;
            font-size: 13px;
        }
        .co-divider { border: none; border-top: 1px solid #ebf2fa; margin: 12px 0; }
        .co-total-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 13px;
            color: #2f537b;
            padding: 4px 0;
        }
        .co-total-row.main {
            font-size: 15px;
            font-weight: 800;
            color: #124a91;
            padding-top: 8px;
        }
        .co-total-row.discount { color: #27ae60; font-weight: 700; }
        .co-final-price { font-size: 18px; font-weight: 900; color: #e74c3c; }
        .btn-submit {
            margin-top: 14px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(145deg, #2ecc71, #27ae60);
            color: #fff;
            border: none;
            border-radius: 9px;
            font-size: 15px;
            font-weight: 800;
            letter-spacing: .3px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            transition: filter .2s;
        }
        .btn-submit:hover { filter: brightness(1.06); }
        .co-security-note {
            margin-top: 10px;
            text-align: center;
            font-size: 11px;
            color: #8eaac9;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 5px;
        }
        .co-error {
            background: #fff5f5;
            border: 1px solid #f5c6cb;
            color: #c0392b;
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 13px;
            margin-bottom: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        @media (max-width: 860px) {
            .co-grid { grid-template-columns: 1fr; }
            .co-sidebar { position: relative; top: 0; }
        }
    </style>
</head>
<body>
<jsp:include page="/style/header/header.jsp" />

<main class="co-page">
    <h1><i class="fas fa-shopping-bag" style="font-size:18px;"></i> Thanh toán</h1>

    <c:if test="${not empty error}">
        <div class="co-error"><i class="fas fa-exclamation-circle"></i> ${error}</div>
    </c:if>

    <div class="co-grid">

        <form action="${pageContext.request.contextPath}/checkout" method="post" id="checkoutForm">

            <!-- Người nhận -->
            <div class="co-card">
                <div class="co-card-title"><i class="fas fa-user"></i> Thông tin người nhận</div>
                <div class="co-field">
                    <label for="recipientName"><i class="fas fa-user"></i> Họ và tên</label>
                    <input class="co-input" type="text" id="recipientName" name="recipientName"
                           required placeholder="Nhập tên người nhận"
                           value="${sessionScope.auth.username}">
                </div>
            </div>

            <!-- Mã khuyến mãi -->
            <div class="co-card">
                <div class="co-card-title"><i class="fas fa-tag"></i> Mã khuyến mãi</div>
                <div class="co-field">
                    <div class="co-promo-wrap">
                        <input class="co-input" type="text" id="promoCodeInput"
                               placeholder="Nhập mã khuyến mãi (tùy chọn)">
                        <button type="button" id="btnApplyPromo" class="btn-promo">Áp dụng</button>
                    </div>
                    <div id="promoMessage" class="co-promo-msg"></div>
                    <!-- hidden field thực sự submit -->
                    <input type="hidden" name="promoCode" id="promoCodeHidden" value="">
                </div>
            </div>

            <!-- Phương thức thanh toán -->
            <div class="co-card">
                <div class="co-card-title"><i class="fas fa-credit-card"></i> Phương thức thanh toán</div>

                <div class="pm-option" onclick="selectPayment('COD')">
                    <input type="radio" name="paymentMethod" id="pmCOD" value="COD" checked style="display:none">
                    <div class="pm-inner" id="lbl-COD" style="border-color:#2ecc71;background:#f0fdf4;">
                        <i class="fas fa-money-bill-wave pm-icon" style="color:#2ecc71;"></i>
                        <div>
                            <div class="pm-label" style="color:#155724;">Thanh toán khi xác nhận (COD)</div>
                            <div class="pm-desc">Xác nhận đơn hàng qua email sau khi đặt</div>
                        </div>
                    </div>
                </div>

                <div class="pm-option" onclick="selectPayment('SEPAY')">
                    <input type="radio" name="paymentMethod" id="pmSEPAY" value="SEPAY" style="display:none">
                    <div class="pm-inner" id="lbl-SEPAY">
                        <i class="fas fa-qrcode pm-icon" style="color:#1abc9c;"></i>
                        <div>
                            <div class="pm-label" style="color:#1abc9c;">Thanh toán QR Banking (SePay)</div>
                            <div class="pm-desc">Quét mã QR qua app ngân hàng – xác nhận tức thì</div>
                        </div>
                    </div>
                </div>

                <div class="pm-option" onclick="selectPayment('BankTransfer')">
                    <input type="radio" name="paymentMethod" id="pmBank" value="BankTransfer" style="display:none">
                    <div class="pm-inner" id="lbl-BankTransfer">
                        <i class="fas fa-exchange-alt pm-icon" style="color:#2980b9;"></i>
                        <div>
                            <div class="pm-label" style="color:#2980b9;">Chuyển khoản ngân hàng</div>
                            <div class="pm-desc">Chuyển khoản theo thông tin được cung cấp sau đặt hàng</div>
                        </div>
                    </div>
                </div>
            </div>

        </form>

        <!-- Sidebar tóm tắt đơn hàng -->
        <aside class="co-sidebar">
            <div class="co-card">
                <div class="co-card-title"><i class="fas fa-receipt"></i> Đơn hàng của bạn</div>

                <c:forEach items="${cart.data}" var="entry">
                    <c:set var="item" value="${entry.value}" />
                    <div class="order-item-row">
                        <img class="order-item-thumb" src="${item.thumbnailUrl}" alt="${item.title}">
                        <div class="oitem-name">
                                ${item.title}
                            <span class="oitem-type">
                                <c:choose>
                                    <c:when test="${item.itemType == 'course'}">Khóa học</c:when>
                                    <c:otherwise>Tài liệu</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <span class="oitem-price">
                            <c:choose>
                                <c:when test="${item.price == 0}">Miễn phí</c:when>
                                <c:otherwise><fmt:formatNumber value="${item.price}" type="currency" currencySymbol="₫"/></c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </c:forEach>

                <hr class="co-divider">

                <div class="co-total-row">
                    <span>Tạm tính</span>
                    <span id="originalTotal" data-original="${cart.totalPrice}">
                        <fmt:formatNumber value="${cart.totalPrice}" type="currency" currencySymbol="₫"/>
                    </span>
                </div>

                <div class="co-total-row discount" id="discountRow" style="display:none;">
                    <span><i class="fas fa-tag"></i> Giảm giá</span>
                    <span id="discountAmount"></span>
                </div>

                <hr class="co-divider">

                <div class="co-total-row main">
                    <span>Tổng cộng</span>
                    <span class="co-final-price" id="finalTotal">
                        <fmt:formatNumber value="${cart.totalPrice}" type="currency" currencySymbol="₫"/>
                    </span>
                </div>

                <button type="submit" form="checkoutForm" class="btn-submit" id="btnSubmit">
                    <i class="fas fa-check-circle"></i> Đặt hàng ngay
                </button>

                <div class="co-security-note">
                    <i class="fas fa-lock"></i> Thanh toán được bảo mật
                </div>
            </div>
        </aside>

    </div>
</main>

<jsp:include page="/style/footer/footer.jsp" />

<script>
    // ---- Mã khuyến mãi ----
    let appliedDiscount = 0;
    let appliedPromoCode = "";

    document.getElementById("btnApplyPromo").addEventListener("click", async function () {
        const promoCode = document.getElementById("promoCodeInput").value.trim();
        const promoMessage = document.getElementById("promoMessage");
        const originalTotal = parseFloat(document.getElementById("originalTotal").dataset.original);

        if (!promoCode) {
            promoMessage.style.color = "#e74c3c";
            promoMessage.textContent = "Vui lòng nhập mã khuyến mãi.";
            return;
        }

        promoMessage.style.color = "#2563b8";
        promoMessage.textContent = "Đang kiểm tra...";

        try {
            const response = await fetch("${pageContext.request.contextPath}/api/validate-promo?code=" + encodeURIComponent(promoCode) + "&total=" + originalTotal);
            const result = await response.json();

            if (result.success) {
                appliedDiscount = result.discount;
                appliedPromoCode = promoCode;

                const finalAmt = originalTotal - appliedDiscount;
                document.getElementById("discountAmount").textContent = formatCurrency(appliedDiscount);
                document.getElementById("discountRow").style.display = "flex";
                document.getElementById("finalTotal").textContent = formatCurrency(finalAmt);
                document.getElementById("promoCodeHidden").value = appliedPromoCode;

                promoMessage.style.color = "#27ae60";
                promoMessage.textContent = "✓ Áp dụng thành công! " + result.message;

                document.getElementById("promoCodeInput").disabled = true;
                this.disabled = true;
                this.style.background = "#95a5a6";
            } else {
                promoMessage.style.color = "#e74c3c";
                promoMessage.textContent = "✗ " + result.message;
                appliedDiscount = 0;
                appliedPromoCode = "";
                document.getElementById("discountRow").style.display = "none";
                document.getElementById("promoCodeHidden").value = "";
            }
        } catch (err) {
            promoMessage.style.color = "#e74c3c";
            promoMessage.textContent = "Lỗi kết nối máy chủ.";
            console.error(err);
        }
    });

    function formatCurrency(amount) {
        return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);
    }

    // ---- Phương thức thanh toán ----
    function selectPayment(value) {
        const colors = { COD: "#2ecc71", SEPAY: "#1abc9c", BankTransfer: "#2980b9" };
        const bgs   = { COD: "#f0fdf4", SEPAY: "#f0fdfb", BankTransfer: "#eff7ff" };

        document.querySelectorAll(".pm-inner").forEach(function (el) {
            el.style.borderColor = "#d3e4f6";
            el.style.background  = "#fff";
        });

        const inner = document.getElementById("lbl-" + value);
        if (inner) {
            inner.style.borderColor = colors[value] || "#999";
            inner.style.background  = bgs[value] || "#fff";
        }

        const radio = document.getElementById("pm" + value);
        if (radio) radio.checked = true;

        const form = document.getElementById("checkoutForm");
        form.action = (value === "SEPAY")
            ? "${pageContext.request.contextPath}/payment/sepay"
            : "${pageContext.request.contextPath}/checkout";
    }

    selectPayment("COD");
</script>

<script src="${pageContext.request.contextPath}/style/header/header.js"></script>
<script src="${pageContext.request.contextPath}/style/footer/footer.js"></script>
</body>
</html>