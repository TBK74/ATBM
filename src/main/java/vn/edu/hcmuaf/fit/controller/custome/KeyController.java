package vn.edu.hcmuaf.fit.controller.custome;

import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;
import vn.edu.hcmuaf.fit.model.UserKey;
import vn.edu.hcmuaf.fit.service.KeyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(name = "KeyController", value = "/key-manager")
public class KeyController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        int accountId = user.getAccountID();
        KeyService ks = KeyService.getInstance();

        UserKey activeKey = ks.getActiveKey(accountId);
        List<UserKey> history = ks.getKeyHistory(accountId);
        String keyStatus = ks.getKeyStatus(accountId); // "active" | "revoked" | "none"

        req.setAttribute("activeKey", activeKey);
        req.setAttribute("keyHistory", history);
        req.setAttribute("keyStatus", keyStatus);
        req.getRequestDispatcher("/Customer/Key/key_manager.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = req.getParameter("action");
        int accountId = user.getAccountID();
        KeyService ks = KeyService.getInstance();

        switch (action == null ? "" : action) {

            case "save": {
                String pem = req.getParameter("publicKey");
                if (pem == null || pem.isBlank()) {
                    req.getSession().setAttribute("toastError", "Public key không hợp lệ.");
                    break;
                }
                int keyId = ks.registerKey(accountId, pem);
                if (keyId > 0) {
                    req.getSession().setAttribute("toastSuccess", "Đã lưu public key thành công!");
                } else {
                    req.getSession().setAttribute("toastError", "Lưu khóa thất bại, vui lòng thử lại.");
                }
                break;
            }

            case "revoke": {
                String keyIdStr = req.getParameter("keyId");
                String suspectedStr = req.getParameter("suspectedAt"); // ISO datetime string
                if (keyIdStr == null) { resp.sendRedirect("key-manager"); return; }
                try {
                    int keyId = Integer.parseInt(keyIdStr);
                    Timestamp suspected = null;
                    if (suspectedStr != null && !suspectedStr.isBlank()) {
                        // Parse "2024-06-01T07:00" từ datetime-local input
                        suspected = Timestamp.valueOf(suspectedStr.replace("T", " ") + ":00");
                    }
                    boolean ok = ks.revokeKey(keyId, suspected);
                    if (ok) {
                        req.getSession().setAttribute("toastSuccess",
                                "Đã báo mất khóa. Các đơn ký sau " +
                                        (suspected != null ? suspected : "thời điểm báo") + " sẽ cần xem lại.");
                    } else {
                        req.getSession().setAttribute("toastError", "Thu hồi khóa thất bại.");
                    }
                } catch (Exception e) {
                    req.getSession().setAttribute("toastError", "Dữ liệu không hợp lệ.");
                }
                break;
            }

            default:
                break;
        }

        resp.sendRedirect(req.getContextPath() + "/key-manager");
    }
}