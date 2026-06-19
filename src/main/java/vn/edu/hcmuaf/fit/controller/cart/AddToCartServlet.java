package vn.edu.hcmuaf.fit.controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.Cart;
import vn.edu.hcmuaf.fit.model.User;
import vn.edu.hcmuaf.fit.service.CartService;
import vn.edu.hcmuaf.fit.service.CustomerService;

import java.io.IOException;

@WebServlet(name = "AddToCartServlet", value = "/cart/add")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        HttpSession session   = req.getSession();
        User user             = (User) session.getAttribute("auth");
        boolean isAjax        = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));

        if (user == null) {
            sendJson(resp, isAjax,
                    "{\"status\":\"error\",\"message\":\"Vui lòng đăng nhập để thêm vào giỏ hàng.\","
                            + "\"redirect\":\"" + req.getContextPath() + "/login\"}",
                    req.getContextPath() + "/login");
            return;
        }

        UserDAO userDAO  = new UserDAO();
        int customerId   = userDAO.getCustomerIdByAccountId(user.getAccountID());
        if (customerId == -1) {
            CustomerService.getInstance().ensureCustomerRecord(user.getAccountID());
            customerId = userDAO.getCustomerIdByAccountId(user.getAccountID());
        }

        if (customerId == -1) {
            sendJson(resp, isAjax,
                    "{\"status\":\"error\",\"message\":\"Không thể xử lý tài khoản. Vui lòng liên hệ hỗ trợ.\"}",
                    req.getHeader("Referer"));
            return;
        }

        String itemType = req.getParameter("type");
        String idStr    = req.getParameter("id");
        if (itemType == null || idStr == null) {
            sendJson(resp, isAjax,
                    "{\"status\":\"error\",\"message\":\"Thiếu thông tin sản phẩm.\"}",
                    req.getHeader("Referer"));
            return;
        }

        try {
            int itemId = Integer.parseInt(idStr);
            boolean added;
            if ("course".equals(itemType)) {
                added = CartService.getInstance().addCourse(customerId, itemId);
            } else {
                added = CartService.getInstance().addDocument(customerId, itemId);
            }

            Cart cart   = CartService.getInstance().getCart(customerId);
            session.setAttribute("cart", cart);
            int totalQty = cart.getTotalQuantity();

            if (isAjax) {
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write("{\"status\":\"success\",\"added\":" + added
                        + ",\"totalQuantity\":" + totalQty + "}");
            } else {
                String redirect = req.getParameter("redirect");
                String fallback = req.getHeader("Referer");
                resp.sendRedirect(redirect != null ? redirect
                        : fallback != null ? fallback
                          : req.getContextPath() + "/cart");
            }

        } catch (NumberFormatException e) {
            sendJson(resp, isAjax,
                    "{\"status\":\"error\",\"message\":\"ID sản phẩm không hợp lệ.\"}",
                    req.getHeader("Referer"));
        }
    }

    private void sendJson(HttpServletResponse resp, boolean isAjax,
                          String json, String redirectUrl) throws IOException {
        if (isAjax) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(json);
        } else if (redirectUrl != null) {
            resp.sendRedirect(redirectUrl);
        }
    }
}