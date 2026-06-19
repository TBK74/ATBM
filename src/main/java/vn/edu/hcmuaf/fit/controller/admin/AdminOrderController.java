package vn.edu.hcmuaf.fit.controller.admin;

import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.model.Order;
import vn.edu.hcmuaf.fit.dao.DocumentAccessDAO;
import vn.edu.hcmuaf.fit.dao.EnrollmentDAO;
import vn.edu.hcmuaf.fit.service.AuditService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminOrderController", value = "/admin/orders")
public class AdminOrderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q        = request.getParameter("q");
        String status   = request.getParameter("status");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo   = request.getParameter("dateTo");
        String priceMin = request.getParameter("priceMin");
        String priceMax = request.getParameter("priceMax");

        OrderDAO dao = OrderDAO.getInstance();
        List<Order> list;

        boolean hasFilter = isNotBlank(q) || isNotBlank(status) || isNotBlank(dateFrom)
                || isNotBlank(dateTo) || isNotBlank(priceMin) || isNotBlank(priceMax);

        list = hasFilter
                ? dao.filter(q, status, dateFrom, dateTo, priceMin, priceMax)
                : dao.getAll();

        String orderError = (String) request.getSession().getAttribute("orderError");
        if (orderError != null) {
            request.setAttribute("orderError", orderError);
            request.getSession().removeAttribute("orderError");
        }

        request.setAttribute("listO",        list);
        request.setAttribute("msgName",      q);
        request.setAttribute("msgStatus",    status);
        request.setAttribute("msgDateFrom",  dateFrom);
        request.setAttribute("msgDateTo",    dateTo);
        request.setAttribute("msgPriceMin",  priceMin);
        request.setAttribute("msgPriceMax",  priceMax);

        request.getRequestDispatcher("/Admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            handleUpdateStatus(request, response);
        } else {
            doGet(request, response);
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng. Vì sản phẩm là khóa học/tài liệu số,
     * không cần tích hợp vận chuyển (GHN) hay trừ tồn kho vật lý.
     * Quy trình: Pending -> Processing -> Completed, hoặc Cancelled.
     * Việc grant quyền truy cập (enrollment/document_access) đã được
     * thực hiện ngay khi tạo Order trong OrderService.placeOrder().
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int    orderId   = Integer.parseInt(request.getParameter("id"));
        String newStatus = request.getParameter("status");

        int actorAccountId = 0;
        try {
            Object auth = request.getSession().getAttribute("auth");
            if (auth != null) {
                actorAccountId = (int) auth.getClass().getMethod("getAccountId").invoke(auth);
            }
        } catch (Exception ignored) {}

        AuditService.EditCheckResult check =
                AuditService.getInstance().beforeAdminEdit(orderId, actorAccountId, newStatus);

        if (check.isBlocked()) {
            request.getSession().setAttribute("orderError", check.getMessage());
            response.sendRedirect("orders");
            return;
        }

        Order order = OrderDAO.getInstance().getById(orderId);
        if (order == null) {
            response.sendRedirect("orders");
            return;
        }

        String currentStatus = order.getStatus();

        switch (newStatus) {
            case "Processing":
                if ("Pending".equals(currentStatus)) {
                    OrderDAO.getInstance().updateStatus(orderId, newStatus);
                }
                break;
            case "Completed":
                if ("Processing".equals(currentStatus) || "Pending".equals(currentStatus)) {
                    OrderDAO.getInstance().updateStatus(orderId, newStatus);
                }
                break;
            case "Cancelled":
                if ("Pending".equals(currentStatus) || "Processing".equals(currentStatus)) {
                    OrderDAO.getInstance().updateStatus(orderId, newStatus);
                    DocumentAccessDAO.getInstance().revokeAccessByOrder(orderId);
                    EnrollmentDAO.getInstance().revokeEnrollmentByOrder(orderId);
                }
                break;
        }

        response.sendRedirect("orders");
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}