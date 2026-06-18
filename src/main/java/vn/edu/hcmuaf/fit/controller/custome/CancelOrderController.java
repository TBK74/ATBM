package vn.edu.hcmuaf.fit.controller.custome;

import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "CancelOrderController", value = "/cancel-order")
public class CancelOrderController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("auth");
        String contextPath = request.getContextPath();
        String targetUrl = contextPath + "/purchase-history";

        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(targetUrl);
            return;
        }

        try {
            int orderId = Integer.parseInt(idParam);
            int customerId = new UserDAO().getCustomerIdByAccountId(user.getAccountID());

            OrderDAO orderDAO = OrderDAO.getInstance();

            if (!orderDAO.isOwned(orderId, customerId)) {
                session.setAttribute("toastError", "Không tìm thấy đơn hàng hoặc bạn không có quyền hủy.");
            } else {
                boolean success = orderDAO.cancelOrder(orderId);

                if (success) {
                    session.setAttribute("toastSuccess", "Đơn hàng #" + orderId + " đã được hủy thành công.");
                    targetUrl += "?tab=Cancelled";
                } else {
                    session.setAttribute("toastError", "Không thể hủy đơn hàng #" + orderId + " (Có thể đơn đã được xử lý).");
                }
            }

        } catch (NumberFormatException e) {
            session.setAttribute("toastError", "Mã đơn hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("toastError", "Đã xảy ra lỗi hệ thống.");
        }

        response.sendRedirect(targetUrl);
    }
}
