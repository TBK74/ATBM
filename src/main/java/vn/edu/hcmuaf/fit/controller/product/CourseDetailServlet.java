package vn.edu.hcmuaf.fit.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.EnrollmentDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.*;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;

@WebServlet(name = "CourseDetailServlet", value = "/course-detail")
public class CourseDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) { resp.sendRedirect("courses"); return; }
        try {
            int id = Integer.parseInt(idStr);
            Course course = CourseService.getInstance().getById(id);
            if (course == null) { resp.sendRedirect("courses"); return; }

            req.setAttribute("course", course);
            req.setAttribute("relatedCourses", CourseService.getInstance().getRelated(id, course.getCategoryId(), 4));

            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("auth");
            if (user != null) {
                int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
                req.setAttribute("isEnrolled", EnrollmentDAO.getInstance().isEnrolled(cid, id));
                Cart cart = CartService.getInstance().getCart(cid);
                req.setAttribute("inCart", cart.contains("course", id));
            }
            req.getRequestDispatcher("/Courses/course-detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) { resp.sendRedirect("courses"); }
    }
}
