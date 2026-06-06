package vn.edu.hcmuaf.fit.controller.custome;

import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.OrderSignatureService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "PurchaseHistoryController", value = "/purchase-history")
public class PurchaseHistoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
        List<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderItemsMap.put(order.getOrderId(), orderDAO.getItems(order.getOrderId()));
            orderIds.add(order.getOrderId());
        }

        OrderSignatureService sigService = OrderSignatureService.getInstance();
        sigService.verifyAll(orderIds);

        Map<Integer, OrderSignature> signatureMap = sigService.getSignatureMap(orderIds);

        request.setAttribute("orders", orders);
        request.setAttribute("orderItemsMap", orderItemsMap);
        request.setAttribute("signatureMap", signatureMap);
        request.setAttribute("accountId", user.getAccountID());

        request.getRequestDispatcher("/Customer/Purchase_history/purchase_history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}