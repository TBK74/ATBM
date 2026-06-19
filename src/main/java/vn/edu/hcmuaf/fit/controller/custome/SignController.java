package vn.edu.hcmuaf.fit.controller.custome;

import com.google.gson.Gson;
import vn.edu.hcmuaf.fit.dao.KeyDAO;
import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.OrderSignatureService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Xử lý tool ký đơn hàng:
 *   GET  /sign-order?orderId=X  → Hiển thị trang ký, trả về thông tin đơn + hash
 *   POST /sign-order             → Nhận chữ ký từ client, verify và lưu
 *
 * Endpoint JSON (POST action=getHash): trả hash để tool ký JS dùng
 */
@WebServlet(name = "SignController", value = "/sign-order")
@MultipartConfig
public class SignController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null) { resp.sendRedirect(req.getContextPath() + "/purchase-history"); return; }

        int orderId;
        try { orderId = Integer.parseInt(orderIdStr); }
        catch (NumberFormatException e) { resp.sendRedirect(req.getContextPath() + "/purchase-history"); return; }

        // Kiểm tra đơn hàng thuộc về user này
        OrderDAO orderDAO = OrderDAO.getInstance();
        // Lấy customerId từ accountId (cần JOIN)
        int customerId = new vn.edu.hcmuaf.fit.dao.UserDAO().getCustomerIdByAccountId(user.getAccountID());
        if (!orderDAO.isOwned(orderId, customerId)) {
            resp.sendRedirect(req.getContextPath() + "/purchase-history");
            return;
        }

        Order order = orderDAO.getById(orderId);
        List<OrderItem> items = orderDAO.getItems(orderId);
        OrderSignature sig = OrderSignatureService.getInstance().getSignatureInfo(orderId);

        // Lấy key active của user
        UserKey activeKey = KeyDAO.getInstance().getActiveKey(user.getAccountID());

        req.setAttribute("order", order);
        req.setAttribute("items", items);
        req.setAttribute("sig", sig);
        req.setAttribute("activeKey", activeKey);
        req.getRequestDispatcher("/Customer/Key/sign_order.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) {
            sendJson(resp, 401, "{\"error\":\"Chưa đăng nhập\"}");
            return;
        }

        String action = req.getParameter("action");

        if ("submit".equals(action)) {
            handleSubmitSignature(req, resp, user);
        } else {
            sendJson(resp, 400, "{\"error\":\"Action không hợp lệ\"}");
        }
    }

    /** Nhận chữ ký từ browser, verify và lưu */
    private void handleSubmitSignature(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        String orderIdStr   = req.getParameter("orderId");
        String signatureB64 = req.getParameter("signature");
        String keyIdStr     = req.getParameter("keyId");

        if (orderIdStr == null || signatureB64 == null || keyIdStr == null) {
            sendJson(resp, 400, "{\"error\":\"Thiếu tham số\"}"); return;
        }

        int orderId, keyId;
        try {
            orderId = Integer.parseInt(orderIdStr);
            keyId   = Integer.parseInt(keyIdStr);
        } catch (NumberFormatException e) {
            sendJson(resp, 400, "{\"error\":\"Tham số không hợp lệ\"}"); return;
        }

        // Kiểm tra key thuộc về user này
        vn.edu.hcmuaf.fit.model.UserKey key = KeyDAO.getInstance().getById(keyId);
        if (key == null || key.getAccountId() != user.getAccountID()) {
            sendJson(resp, 403, "{\"error\":\"Key không hợp lệ\"}"); return;
        }

        // Kiểm tra đơn hàng thuộc về user
        int customerId = new vn.edu.hcmuaf.fit.dao.UserDAO().getCustomerIdByAccountId(user.getAccountID());
        if (!OrderDAO.getInstance().isOwned(orderId, customerId)) {
            sendJson(resp, 403, "{\"error\":\"Đơn hàng không thuộc về bạn\"}"); return;
        }

        String result = OrderSignatureService.getInstance().submitSignature(orderId, signatureB64, keyId);

        // Sau khi verify xong, scan lại toàn bộ danh sách đơn (theo yêu cầu)
        // (Thực tế scan này sẽ xảy ra khi user load lại purchase-history)
        String message;
        switch (result) {
            case "verified": message = "Đơn hàng đã được ký và xác thực thành công!"; break;
            case "tampered": message = "Cảnh báo: chữ ký không khớp với dữ liệu đơn hàng."; break;
            default: message = "Đã lưu chữ ký nhưng xác thực thất bại.";
        }

        sendJson(resp, 200,
                "{\"status\":\"" + result + "\",\"message\":\"" + escapeJson(message) + "\"}");
    }

    private void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter w = resp.getWriter()) { w.print(json); }
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}