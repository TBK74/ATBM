package vn.edu.hcmuaf.fit.controller.custome;

import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PurchaseHistoryController", value = "/purchase-history")
public class PurchaseHistoryController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("auth");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int customerId = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        OrderDAO orderDAO = OrderDAO.getInstance();
        List<Order> orders = orderDAO.getByCustomerId(customerId);

        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order order : orders) {
            orderItemsMap.put(order.getOrderId(), orderDAO.getItems(order.getOrderId()));
        }

        request.setAttribute("orders", orders);
        request.setAttribute("orderItemsMap", orderItemsMap);

        request.getRequestDispatcher("/Customer/Purchase_history/purchase_history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
