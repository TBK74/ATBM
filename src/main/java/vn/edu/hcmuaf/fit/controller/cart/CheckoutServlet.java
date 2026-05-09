package vn.edu.hcmuaf.fit.controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;

@WebServlet(name = "CheckoutServlet", value = "/checkout")
public class CheckoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect("login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        Cart cart = CartService.getInstance().getCart(cid);
        if (cart == null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath() + "/cart"); return; }
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/Checkout/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("auth");
        if (user == null) { resp.sendRedirect("login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());

        String recipientName  = req.getParameter("recipientName");
        String paymentMethod  = req.getParameter("paymentMethod");
        String promoCode      = req.getParameter("promoCode");

        int orderId = OrderService.getInstance().placeOrder(cid, recipientName, paymentMethod, promoCode);
        if (orderId != -1) {
            session.removeAttribute("cart");
            resp.sendRedirect(req.getContextPath() + "/order-success?id=" + orderId);
        } else {
            req.setAttribute("error", "Đặt hàng thất bại. Vui lòng thử lại.");
            doGet(req, resp);
        }
    }
}
