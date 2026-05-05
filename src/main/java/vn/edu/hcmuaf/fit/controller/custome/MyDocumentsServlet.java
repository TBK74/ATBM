package vn.edu.hcmuaf.fit.controller.custome;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.DocumentAccessDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;

import java.io.IOException;

@WebServlet(name = "MyDocumentsServlet", value = "/my-documents")
public class MyDocumentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        req.setAttribute("accesses", DocumentAccessDAO.getInstance().getByCustomer(cid));
        req.getRequestDispatcher("/Customer/MyDocuments/my-documents.jsp").forward(req, resp);
    }
}
