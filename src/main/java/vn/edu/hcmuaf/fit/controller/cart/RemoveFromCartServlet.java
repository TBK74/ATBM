package vn.edu.hcmuaf.fit.controller.cart;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;
import vn.edu.hcmuaf.fit.service.CartService;

import java.io.IOException;

@WebServlet(name = "RemoveFromCartServlet", value = "/cart/remove")
public class RemoveFromCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        CartService.getInstance().removeItem(cid, req.getParameter("type"), Integer.parseInt(req.getParameter("id")));
        req.getSession().setAttribute("cart", CartService.getInstance().getCart(cid));
        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}
