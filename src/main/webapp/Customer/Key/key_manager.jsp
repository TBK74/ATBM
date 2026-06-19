<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý khóa điện tử - EDUMART</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/header/header.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Customer/User_sidebar/user_sidebar.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer/footer.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        .key-card { border:1px solid #ddd; border-radius:8px; padding:20px; margin-bottom:16px; background:#fff; }
        .key-status-badge { display:inline-block; padding:3px 12px; border-radius:999px; font-size:13px; font-weight:600; }
        .badge-active  { background:#d4edda; color:#155724; }
        .badge-revoked { background:#f8d7da; color:#721c24; }
        .badge-none    { background:#e2e3e5; color:#383d41; }
        .btn-primary   { background:#1a73e8; color:#fff; border:none; padding:10px 22px; border-radius:6px; cursor:pointer; font-size:14px; }
        .btn-danger    { background:#dc3545; color:#fff; border:none; padding:8px 18px; border-radius:6px; cursor:pointer; font-size:14px; }
        .btn-secondary { background:#6c757d; color:#fff; border:none; padding:8px 18px; border-radius:6px; cursor:pointer; font-size:14px; }
        .history-table { width:100%; border-collapse:collapse; margin-top:12px; }
        .history-table th, .history-table td { border:1px solid #dee2e6; padding:8px 12px; font-size:13px; text-align:left; }
        .history-table th { background:#f8f9fa; }
        .key-pem { font-family:monospace; font-size:11px; background:#f4f4f4; padding:10px; border-radius:4px;
            word-break:break-all; max-height:80px; overflow:auto; }
        .modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,.5); z-index:1000; align-items:center; justify-content:center; }
        .modal-overlay.open { display:flex; }
        .modal-box { background:#fff; border-radius:10px; padding:28px; width:420px; max-width:95vw; }
        #progressBar { height:6px; background:#e9ecef; border-radius:3px; margin:12px 0; }
        #progressFill { height:100%; width:0%; background:#1a73e8; border-radius:3px; transition:width .3s; }
    </style>
</head>
<body>
<jsp:include page="/style/header/header.jsp"/>

<div class="user-container" style="display:flex;gap:24px;padding:32px 24px;max-width:1200px;margin:0 auto;">
    <jsp:include page="/Customer/User_sidebar/user_sidebar.jsp"/>

    <main class="content" style="flex:1;">
        <h2 style="margin-bottom:20px;">Quản lý khóa điện tử</h2>

        <%-- Thông báo toast --%>
        <c:if test="${not empty sessionScope.toastSuccess}">
            <div class="alert alert-success" style="background:#d4edda;color:#155724;padding:12px 18px;border-radius:6px;margin-bottom:16px;">
                <i class="fa-solid fa-circle-check"></i> ${sessionScope.toastSuccess}
            </div>
            <c:remove var="toastSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.toastError}">
            <div class="alert alert-danger" style="background:#f8d7da;color:#721c24;padding:12px 18px;border-radius:6px;margin-bottom:16px;">
                <i class="fa-solid fa-circle-exclamation"></i> ${sessionScope.toastError}
            </div>
            <c:remove var="toastError" scope="session"/>
        </c:if>

        <%-- Trạng thái khóa hiện tại --%>
        <div class="key-card">
            <h3 style="margin-top:0;">Khóa hiện tại</h3>
            <c:choose>
                <c:when test="${keyStatus == 'active'}">
                    <p>Trạng thái: <span class="key-status-badge badge-active">✓ Đang hoạt động</span></p>
                    <p style="font-size:13px;color:#555;">Tạo lúc: <fmt:formatDate value="${activeKey.createdAt}" pattern="dd/MM/yyyy HH:mm"/></p>
                    <p style="font-size:12px;color:#888;">Public key (${activeKey.algorithm}):</p>
                    <div class="key-pem">${activeKey.publicKey}</div>
                    <div style="margin-top:16px;display:flex;gap:10px;">
                        <button class="btn-primary" onclick="generateKey()"><i class="fa-solid fa-rotate"></i> Tạo khóa mới</button>
                        <button class="btn-danger" onclick="document.getElementById('revokeModal').classList.add('open')">
                            <i class="fa-solid fa-triangle-exclamation"></i> Báo mất khóa
                        </button>
                    </div>
                </c:when>
                <c:when test="${keyStatus == 'revoked'}">
                    <p>Trạng thái: <span class="key-status-badge badge-revoked">✗ Đã thu hồi</span></p>
                    <p style="color:#856404;background:#fff3cd;padding:10px;border-radius:6px;font-size:13px;">
                        <i class="fa-solid fa-triangle-exclamation"></i>
                        Bạn chưa có khóa hoạt động. Cần tạo khóa mới để ký đơn hàng.
                    </p>
                    <button class="btn-primary" onclick="generateKey()"><i class="fa-solid fa-key"></i> Tạo khóa mới</button>
                </c:when>
                <c:otherwise>
                    <p>Trạng thái: <span class="key-status-badge badge-none">Chưa có khóa</span></p>
                    <p style="color:#555;font-size:14px;">Bạn cần tạo cặp khóa điện tử để ký xác nhận đơn hàng.</p>
                    <button class="btn-primary" onclick="generateKey()"><i class="fa-solid fa-key"></i> Tạo khóa ngay</button>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Thanh tiến trình sinh khóa --%>
        <div id="genProgress" style="display:none;" class="key-card">
            <p id="genStatus" style="font-size:14px;color:#555;">Đang sinh khóa RSA-2048...</p>
            <div id="progressBar"><div id="progressFill"></div></div>
        </div>

        <%-- Lịch sử khóa --%>
        <div class="key-card">
            <h3 style="margin-top:0;">Lịch sử khóa</h3>
            <c:choose>
                <c:when test="${empty keyHistory}">
                    <p style="color:#888;">Chưa có lịch sử.</p>
                </c:when>
                <c:otherwise>
                    <table class="history-table">
                        <thead>
                        <tr><th>#</th><th>Thuật toán</th><th>Tạo lúc</th><th>Trạng thái</th><th>Thu hồi lúc</th><th>Nghi ngờ lộ từ</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="k" items="${keyHistory}" varStatus="st">
                            <tr>
                                <td>${st.count}</td>
                                <td>${k.algorithm}</td>
                                <td><fmt:formatDate value="${k.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td><span class="key-status-badge badge-${k.statusCss}">${k.statusLabel}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${k.revokedAt != null}"><fmt:formatDate value="${k.revokedAt}" pattern="dd/MM/yyyy HH:mm"/></c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${k.suspectedAt != null}"><fmt:formatDate value="${k.suspectedAt}" pattern="dd/MM/yyyy HH:mm"/></c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>

<%-- Modal báo mất khóa --%>
<div id="revokeModal" class="modal-overlay">
    <div class="modal-box">
        <h3 style="margin-top:0;color:#dc3545;"><i class="fa-solid fa-triangle-exclamation"></i> Báo mất / lộ khóa</h3>
        <p style="font-size:14px;color:#555;">
            Nếu bạn nghi ngờ private key bị lộ từ một thời điểm cụ thể, hãy nhập vào đây.
            Các đơn hàng được ký sau thời điểm đó sẽ được đánh dấu cần xem lại.
        </p>
        <form method="post" action="${pageContext.request.contextPath}/key-manager">
            <input type="hidden" name="action" value="revoke"/>
            <input type="hidden" name="keyId" value="${activeKey.keyId}"/>
            <label style="font-size:14px;font-weight:600;">Thời điểm nghi ngờ bị lộ (để trống nếu không nhớ):</label><br>
            <input type="datetime-local" name="suspectedAt" style="width:100%;margin:8px 0 16px;padding:8px;border:1px solid #ccc;border-radius:6px;"/>
            <div style="display:flex;gap:10px;">
                <button type="submit" class="btn-danger">Xác nhận báo mất</button>
                <button type="button" class="btn-secondary" onclick="document.getElementById('revokeModal').classList.remove('open')">Hủy</button>
            </div>
        </form>
    </div>
</div>

<%-- Form ẩn để POST public key lên server --%>
<form id="saveKeyForm" method="post" action="${pageContext.request.contextPath}/key-manager" style="display:none;">
    <input type="hidden" name="action" value="save"/>
    <input type="hidden" name="publicKey" id="publicKeyInput"/>
</form>

<jsp:include page="/style/footer/footer.jsp"/>

<script>
    /**
     * Sinh cặp khóa RSA-2048 bằng Web Crypto API (chạy hoàn toàn trên trình duyệt).
     * Private key chỉ tồn tại trong RAM, được export ra file PEM và download xuống máy.
     * Public key (PEM) được gửi lên server lưu vào bảng user_keys.
     */
    async function generateKey() {
        const progress = document.getElementById('genProgress');
        const statusEl = document.getElementById('genStatus');
        const fill = document.getElementById('progressFill');
        progress.style.display = 'block';

        statusEl.textContent = 'Đang sinh khóa RSA-2048, vui lòng đợi...';
        fill.style.width = '20%';

        let keyPair;
        try {
            keyPair = await crypto.subtle.generateKey(
                { name: 'RSASSA-PKCS1-v1_5', modulusLength: 2048,
                    publicExponent: new Uint8Array([1, 0, 1]), hash: 'SHA-256' },
                true, ['sign', 'verify']
            );
        } catch(e) {
            statusEl.textContent = 'Lỗi sinh khóa: ' + e.message;
            return;
        }
        fill.style.width = '60%';
        statusEl.textContent = 'Đang xuất private key...';

        // Export private key → PKCS#8 → PEM → download
        const privRaw  = await crypto.subtle.exportKey('pkcs8', keyPair.privateKey);
        const privB64  = btoa(String.fromCharCode(...new Uint8Array(privRaw)));
        const privPem  = '-----BEGIN PRIVATE KEY-----\n' + privB64.match(/.{1,64}/g).join('\n') + '\n-----END PRIVATE KEY-----';
        downloadText('private_key.pem', privPem);

        fill.style.width = '80%';
        statusEl.textContent = 'Đang gửi public key lên server...';

        const pubRaw = await crypto.subtle.exportKey('spki', keyPair.publicKey);
        const pubB64 = btoa(String.fromCharCode(...new Uint8Array(pubRaw)));
        const pubPem = '-----BEGIN PUBLIC KEY-----\n' + pubB64.match(/.{1,64}/g).join('\n') + '\n-----END PUBLIC KEY-----';

        document.getElementById('publicKeyInput').value = pubPem;
        fill.style.width = '100%';
        statusEl.textContent = 'Hoàn tất! Đang lưu...';

        document.getElementById('saveKeyForm').submit();
    }

    function downloadText(filename, text) {
        const a = document.createElement('a');
        a.href = 'data:application/octet-stream;charset=utf-8,' + encodeURIComponent(text);
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
</script>
</body>
</html>