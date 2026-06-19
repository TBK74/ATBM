package vn.edu.hcmuaf.fit.controller.admin;

import vn.edu.hcmuaf.fit.dao.AuditDAO;
import vn.edu.hcmuaf.fit.dao.OrderEditRequestDAO;
import vn.edu.hcmuaf.fit.model.AdminAlert;
import vn.edu.hcmuaf.fit.model.OrderEditRequest;
import vn.edu.hcmuaf.fit.service.AuditService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * AdminAuditController — Person C
 *
 * Xử lý:
 *   GET  /admin/alerts              → danh sách cảnh báo
 *   POST /admin/alerts?action=read  → đánh dấu đã đọc
 *   GET  /admin/edit-requests       → danh sách yêu cầu chỉnh sửa đơn
 *   POST /admin/edit-requests       → duyệt / từ chối yêu cầu
 */
@WebServlet(name = "AdminAuditController",
        urlPatterns = {"/admin/alerts", "/admin/edit-requests"})
public class AdminAuditController extends HttpServlet {

    private final AuditDAO              auditDAO   = AuditDAO.getInstance();
    private final OrderEditRequestDAO   editDAO    = OrderEditRequestDAO.getInstance();
    private final AuditService          auditSvc   = AuditService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();

        if (uri.endsWith("/alerts")) {
            handleAlertList(req, resp);
        } else {
            handleEditRequestList(req, resp);
        }
    }

    private void handleAlertList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<AdminAlert> alerts  = auditDAO.getAll(100);
        int              unread  = auditDAO.countUnread();

        req.setAttribute("alerts",       alerts);
        req.setAttribute("unreadCount",  unread);
        req.getRequestDispatcher("/Admin/admin_alerts.jsp").forward(req, resp);
    }

    private void handleEditRequestList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String filter = req.getParameter("filter"); // "pending" | "" | null

        List<OrderEditRequest> list = "pending".equals(filter)
                ? editDAO.getAllPending()
                : editDAO.getAll();

        req.setAttribute("editRequests", list);
        req.setAttribute("filter",       filter);
        req.getRequestDispatcher("/Admin/admin_edit_requests.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri    = req.getRequestURI();
        String action = req.getParameter("action");

        if (uri.endsWith("/alerts")) {
            handleAlertAction(req, resp, action);
        } else {
            handleEditRequestAction(req, resp, action);
        }
    }

    private void handleAlertAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {

        int actorId = getActorId(req);

        switch (action == null ? "" : action) {
            case "read":
                int alertId = Integer.parseInt(req.getParameter("id"));
                auditDAO.markRead(alertId, actorId);
                break;
            case "readAll":
                auditDAO.markAllRead(actorId);
                break;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/alerts");
    }

    private void handleEditRequestAction(HttpServletRequest req, HttpServletResponse resp, String action)
            throws IOException {

        int requestId = Integer.parseInt(req.getParameter("id"));
        int actorId   = getActorId(req);

        switch (action == null ? "" : action) {
            case "approve":
                auditSvc.applyApprovedEdit(requestId, actorId);
                req.getSession().setAttribute("flash",
                        "Đã duyệt yêu cầu #" + requestId + " và áp dụng thay đổi.");
                break;
            case "reject":
                String reason = req.getParameter("reason");
                auditSvc.rejectEdit(requestId, actorId, reason);
                req.getSession().setAttribute("flash",
                        "Đã từ chối yêu cầu #" + requestId + ".");
                break;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/edit-requests");
    }

    private int getActorId(HttpServletRequest req) {
        try {
            Object auth = req.getSession().getAttribute("auth");
            if (auth != null) {
                return (int) auth.getClass().getMethod("getAccountId").invoke(auth);
            }
        } catch (Exception ignored) {}
        return 0;
    }
}