package vn.edu.hcmuaf.fit.controller.custome;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.dao.EnrollmentDAO;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;

import java.io.IOException;

@WebServlet(name = "MyCoursesServlet", value = "/my-courses")
public class MyCoursesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("auth");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        int cid = new UserDAO().getCustomerIdByAccountId(user.getAccountID());
        req.setAttribute("enrollments", EnrollmentDAO.getInstance().getByCustomer(cid));
        req.getRequestDispatcher("/Customer/MyCourses/my-courses.jsp").forward(req, resp);
    }
}
