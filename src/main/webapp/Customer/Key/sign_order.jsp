<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ký xác nhận đơn hàng #${order.orderId} - EDUMART</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        body {
            background: #f5f6fa;
        }

        .sign-container {
            max-width: 800px;
            margin: 32px auto;
            padding: 0 16px;
        }

        .card {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, .08);
            padding: 24px;
            margin-bottom: 20px;
        }

        .card h3 {
            margin-top: 0;
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 12px;
        }

        .hash-box {
            font-family: monospace;
            background: #1e1e2e;
            color: #a6e3a1;
            padding: 16px;
            border-radius: 8px;
            word-break: break-all;
            font-size: 13px;
            user-select: all;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid #dee2e6;
            padding: 10px 14px;
            font-size: 14px;
            text-align: left;
        }

        th {
            background: #f8f9fa;
            font-weight: 600;
        }

        .btn {
            padding: 11px 24px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
        }

        .btn-primary {
            background: #1a73e8;
            color: #fff;
        }

        .btn-success {
            background: #28a745;
            color: #fff;
        }

        .btn-secondary {
            background: #6c757d;
            color: #fff;
        }

        .status-badge {
            display: inline-block;
            padding: 4px 14px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 600;
        }

        .badge-unsigned {
            background: #fff3cd;
            color: #856404;
        }

        .badge-signed {
            background: #cce5ff;
            color: #004085;
        }

        .badge-verified {
            background: #d4edda;
            color: #155724;
        }

        .badge-tampered {
            background: #f8d7da;
            color: #721c24;
        }

        #result-box {
            display: none;
            padding: 16px;
            border-radius: 8px;
            margin-top: 16px;
        }

        .result-verified {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .result-tampered {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .step {
            display: flex;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 20px;
        }

        .step-num {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: #1a73e8;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            flex-shrink: 0;
        }

        .step-content {
            flex: 1;
        }

        #fileLabel {
            display: inline-block;
            padding: 10px 20px;
            border: 2px dashed #1a73e8;
            border-radius: 6px;
            cursor: pointer;
            color: #1a73e8;
            font-weight: 600;
        }

        #fileLabel:hover {
            background: #e8f0fe;
        }

        #fileName {
            margin-left: 12px;
            color: #555;
            font-size: 13px;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 14px;
        }

        .info-label {
            color: #666;
        }

        .info-value {
            font-weight: 600;
        }
    </style>
</head>
<body>
<jsp:include page="/style/header/header.jsp"/>

<div class="sign-container">
    <a href="${pageContext.request.contextPath}/purchase-history" style="color:#1a73e8;font-size:14px;">
        ← Quay lại lịch sử đơn hàng
    </a>

    <h2 style="margin:16px 0 4px;">Ký xác nhận đơn hàng <span style="color:#1a73e8;">#${order.orderId}</span></h2>

    <%-- Trạng thái hiện tại --%>
    <c:set var="currentStatus" value="${sig != null ? sig.signStatus : 'unsigned'}"/>
    <p>Trạng thái:
        <span class="status-badge badge-${currentStatus}">${sig != null ? sig.signStatusLabel : 'Chưa ký'}</span>
    </p>

    <%-- Thông tin đơn hàng (đủ fields để review trước khi ký) --%>
    <div class="card">
        <h3><i class="fa-solid fa-file-invoice"></i> Thông tin đơn hàng</h3>
        <div class="info-row"><span class="info-label">Mã đơn hàng</span><span
                class="info-value">#${order.orderId}</span></div>
        <div class="info-row"><span class="info-label">Người nhận</span><span
                class="info-value">${order.recipientName}</span></div>
        <div class="info-row"><span class="info-label">Phương thức thanh toán</span><span
                class="info-value">${order.paymentMethod}</span></div>
        <div class="info-row"><span class="info-label">Ngày đặt</span>
            <span class="info-value"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/></span>
        </div>
        <c:if test="${not empty order.promoCode}">
            <div class="info-row"><span class="info-label">Mã khuyến mãi</span><span
                    class="info-value">${order.promoCode}</span></div>
            <div class="info-row"><span class="info-label">Giảm giá</span>
                <span class="info-value" style="color:#28a745;">
                    -<fmt:formatNumber value="${order.discountAmount}" type="currency" currencySymbol="₫"/>
                </span></div>
        </c:if>

        <h4 style="margin:16px 0 8px;">Danh sách sản phẩm</h4>
        <table>
            <thead>
            <tr>
                <th>STT</th>
                <th>Tên sản phẩm</th>
                <th>Loại</th>
                <th style="text-align:right;">Đơn giá</th>
                <th style="text-align:center;">SL</th>
                <th style="text-align:right;">Thành tiền</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${items}" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td>${item.itemTitle}</td>
                    <td><c:choose>
                        <c:when test="${item.itemType == 'course'}">Khóa học</c:when>
                        <c:otherwise>Tài liệu</c:otherwise>
                    </c:choose></td>
                    <td style="text-align:right;"><fmt:formatNumber value="${item.priceAtOrder}" type="currency"
                                                                    currencySymbol="₫"/></td>
                    <td style="text-align:center;">1</td>
                    <td style="text-align:right;color:#d70018;font-weight:600;">
                        <fmt:formatNumber value="${item.priceAtOrder}" type="currency" currencySymbol="₫"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
                <td colspan="5" style="text-align:right;font-weight:700;">Tổng cộng:</td>
                <td style="text-align:right;color:#d70018;font-weight:700;font-size:16px;">
                    <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/>
                </td>
            </tr>
            </tfoot>
        </table>
    </div>

    <%-- Hash cần ký --%>
    <div class="card">
        <h3><i class="fa-solid fa-hashtag"></i> Mã băm (Hash) cần ký</h3>
        <p style="font-size:13px;color:#555;">Đây là dấu vân tay của đơn hàng. Khi bạn ký hash này, bạn xác nhận toàn bộ
            thông tin bên trên là chính xác.</p>
        <div class="hash-box" id="hashDisplay">
            <c:choose>
                <c:when test="${sig != null}">${sig.dataHash}</c:when>
                <c:otherwise>Đang tải...</c:otherwise>
            </c:choose>
        </div>
        <button class="btn btn-secondary" style="margin-top:12px;" onclick="copyHash()">
            <i class="fa-solid fa-copy"></i> Sao chép hash
        </button>
    </div>

    <%-- Kiểm tra có key chưa --%>
    <c:choose>
        <c:when test="${activeKey == null}">
            <div class="card" style="border:2px solid #ffc107;">
                <p style="color:#856404;"><i class="fa-solid fa-triangle-exclamation"></i>
                    Bạn chưa có khóa điện tử. Vui lòng
                    <a href="${pageContext.request.contextPath}/key-manager">tạo khóa tại đây</a> trước khi ký đơn hàng.
                </p>
            </div>
        </c:when>
        <c:otherwise>
            <%-- Đã có key → hiển thị tool ký --%>
            <div class="card">
                <h3><i class="fa-solid fa-pen-to-square"></i> Ký đơn hàng</h3>

                <div class="step">
                    <div class="step-num">1</div>
                    <div class="step-content">
                        <b>Chọn file private key</b>
                        <p style="font-size:13px;color:#555;">File <code>private_key.pem</code> bạn đã tải về khi tạo
                            khóa. File này <b>không bao giờ rời khỏi máy bạn</b>.</p>
                        <label id="fileLabel" for="privateKeyFile">
                            <i class="fa-solid fa-upload"></i> Chọn file private key
                        </label>
                        <input type="file" id="privateKeyFile" accept=".pem,.key,text/plain" style="display:none;"
                               onchange="onFileSelected(this)"/>
                        <span id="fileName">Chưa chọn file</span>
                    </div>
                </div>

                <div class="step">
                    <div class="step-num">2</div>
                    <div class="step-content">
                        <b>Ký hash</b>
                        <p style="font-size:13px;color:#555;">Nhấn nút dưới để dùng private key ký lên mã băm của đơn
                            hàng.</p>
                        <button class="btn btn-primary" id="signBtn" onclick="signHash()" disabled>
                            <i class="fa-solid fa-signature"></i> Ký đơn hàng
                        </button>
                    </div>
                </div>

                <div class="step" id="step3" style="display:none;">
                    <div class="step-num">3</div>
                    <div class="step-content">
                        <b>Xác nhận & Gửi chữ ký</b>
                        <p style="font-size:13px;color:#555;">Chữ ký đã được tạo. Nhấn nút dưới để gửi lên server xác
                            thực.</p>
                        <div style="background:#f4f4f4;padding:10px;border-radius:6px;font-family:monospace;font-size:11px;word-break:break-all;max-height:60px;overflow:auto;"
                             id="sigPreview"></div>
                        <button class="btn btn-success" style="margin-top:12px;" id="submitBtn"
                                onclick="submitSignature()">
                            <i class="fa-solid fa-paper-plane"></i> Gửi và xác thực
                        </button>
                    </div>
                </div>

                <div id="result-box"></div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/style/footer/footer.jsp"/>

<script>
    const ORDER_ID = ${order.orderId};
    const KEY_ID = ${activeKey != null ? activeKey.keyId : 0};
    const HASH_HEX = document.getElementById('hashDisplay').textContent.trim();
    const CTX = '${pageContext.request.contextPath}';

    let privateKey = null;
    let signatureB64 = null;

    function copyHash() {
        navigator.clipboard.writeText(HASH_HEX).then(() => alert('Đã sao chép hash!'));
    }

    async function onFileSelected(input) {
        const file = input.files[0];
        if (!file) return;
        document.getElementById('fileName').textContent = file.name;

        const pem = await file.text();
        try {
            privateKey = await importPrivateKey(pem);
            document.getElementById('signBtn').disabled = false;
        } catch (e) {
            alert('File private key không hợp lệ: ' + e.message);
            privateKey = null;
            document.getElementById('signBtn').disabled = true;
        }
    }

    async function importPrivateKey(pem) {
        const stripped = pem
            .replace('-----BEGIN PRIVATE KEY-----', '')
            .replace('-----END PRIVATE KEY-----', '')
            .replace(/\s+/g, '');
        const raw = Uint8Array.from(atob(stripped), c => c.charCodeAt(0));
        return crypto.subtle.importKey('pkcs8', raw.buffer,
            {name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256'},
            false, ['sign']);
    }

    async function signHash() {
        if (!privateKey) {
            alert('Vui lòng chọn file private key.');
            return;
        }
        if (!HASH_HEX) {
            alert('Không tìm thấy hash đơn hàng.');
            return;
        }

        try {
            const encoder = new TextEncoder();
            const data = encoder.encode(HASH_HEX);
            const sigBuffer = await crypto.subtle.sign(
                {name: 'RSASSA-PKCS1-v1_5'},
                privateKey, data
            );
            signatureB64 = btoa(String.fromCharCode(...new Uint8Array(sigBuffer)));
            document.getElementById('sigPreview').textContent = signatureB64;
            document.getElementById('step3').style.display = 'flex';
        } catch (e) {
            alert('Ký thất bại: ' + e.message);
        }
    }

    async function submitSignature() {
        if (!signatureB64) {
            alert('Chưa có chữ ký.');
            return;
        }

        if (!KEY_ID || KEY_ID === 0) {
            alert('Không tìm thấy khóa điện tử đang hoạt động. ' +
                'Nếu bạn vừa tạo khóa ở tab/trang khác, vui lòng tải lại trang này rồi thử lại.');
            return;
        }

        const btn = document.getElementById('submitBtn');
        btn.disabled = true;
        btn.textContent = 'Đang xác thực...';

        const formData = new FormData();
        formData.append('action', 'submit');
        formData.append('orderId', ORDER_ID);
        formData.append('signature', signatureB64);
        formData.append('keyId', KEY_ID);

        try {
            const resp = await fetch(CTX + '/sign-order', {method: 'POST', body: formData});
            const json = await resp.json();

            const box = document.getElementById('result-box');
            box.style.display = 'block';

            const displayMessage = json.message || json.error || 'Không rõ nguyên nhân, vui lòng thử lại.';

            if (json.status === 'verified') {
                box.className = 'result-verified';
                box.innerHTML = '<i class="fa-solid fa-circle-check"></i> <b>Xác thực thành công!</b> ' + displayMessage;
                setTimeout(() => window.location.href = CTX + '/purchase-history', 2000);
            } else {
                box.className = 'result-tampered';
                box.innerHTML = '<i class="fa-solid fa-circle-exclamation"></i> <b>Xác thực thất bại.</b> ' + displayMessage;
                btn.disabled = false;
                btn.textContent = 'Thử lại';

                if (json.error && json.error.indexOf('Key không hợp lệ') !== -1) {
                    box.innerHTML += '<br/><small>Gợi ý: hãy <a href="javascript:location.reload()">tải lại trang</a> để cập nhật khóa mới nhất.</small>';
                }
            }
        } catch (e) {
            alert('Lỗi kết nối: ' + e.message);
            btn.disabled = false;
            btn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Gửi và xác thực';
        }
    }
</script>
</body>
</html>