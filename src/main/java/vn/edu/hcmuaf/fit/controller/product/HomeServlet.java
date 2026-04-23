package vn.edu.hcmuaf.fit.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", value = {"/home", "/index"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("auth");
        if (user != null) {
            int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
            if (cid != -1) session.setAttribute("cart", CartService.getInstance().getCart(cid));
        }

        List<Category> categories = CategoryService.getInstance().getAll();
        List<Course> featuredCourses = CourseService.getInstance().getFeatured(8);
        List<Document> newestDocs = DocumentService.getInstance().getNewest(8);

        req.setAttribute("categories", categories);
        req.setAttribute("featuredCourses", featuredCourses);
        req.setAttribute("newestDocs", newestDocs);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
