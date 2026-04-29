package vn.edu.hcmuaf.fit.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.DocumentAccessDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;

@WebServlet(name = "DocumentDetailServlet", value = "/document-detail")
public class DocumentDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect("documents"); return; }
        try {
            int id = Integer.parseInt(idStr);
            Document doc = DocumentService.getInstance().getById(id);
            if (doc == null) { resp.sendRedirect("documents"); return; }

            req.setAttribute("document", doc);
            req.setAttribute("relatedDocs", DocumentService.getInstance().getRelated(id, doc.getCategoryId(), 4));

            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("auth");
            if (user != null) {
                int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
                req.setAttribute("hasAccess", DocumentAccessDAO.getInstance().hasAccess(cid, id));
                Cart cart = CartService.getInstance().getCart(cid);
                req.setAttribute("inCart", cart.contains("document", id));
            }
            req.getRequestDispatcher("/Documents/document-detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendRedirect("documents"); }
    }
}
