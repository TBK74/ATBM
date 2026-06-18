package vn.edu.hcmuaf.fit.controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;
import vn.edu.hcmuaf.fit.service.CartService;

import java.io.IOException;

@WebServlet(name = "AddToCartServlet", value = "/cart/add")
public class AddToCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        int customerId = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        String itemType = req.getParameter("type");  // "course" hoặc "document"
        String idStr    = req.getParameter("id");

        if (itemType == null || idStr == null) { resp.sendRedirect(req.getHeader("Referer")); return; }

        try {
            int itemId = Integer.parseInt(idStr);
            boolean added;
            if ("course".equals(itemType)) {
                added = CartService.getInstance().addCourse(customerId, itemId);
            } else {
                added = CartService.getInstance().addDocument(customerId, itemId);
            }
            // Cập nhật cart trong session
            session.setAttribute("cart", CartService.getInstance().getCart(customerId));
            String redirect = req.getParameter("redirect");
            resp.sendRedirect(redirect != null ? redirect : req.getContextPath() + "/cart");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getHeader("Referer"));
        }
    }
}
