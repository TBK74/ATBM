package vn.edu.hcmuaf.fit.controller.custome;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.model.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderSuccessServlet", value = "/order-success")
public class OrderSuccessServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect(req.getContextPath() + "/home"); return; }
        try {
            int orderId = Integer.parseInt(idStr);
            Order order = OrderDAO.getInstance().getById(orderId);
            List<OrderItem> items = OrderDAO.getInstance().getItems(orderId);
            req.setAttribute("order", order);
            req.setAttribute("items", items);
            req.getRequestDispatcher("/Checkout/order_success.jsp").forward(req, resp);
        } catch (Exception e) { resp.sendRedirect(req.getContextPath() + "/home"); }
    }
}
