package vn.edu.hcmuaf.fit.controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.CartService;

import java.io.IOException;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        Cart cart = CartService.getInstance().getCart(cid);
        session.setAttribute("cart", cart);
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/Cart/cart.jsp").forward(req, resp);
    }
}
