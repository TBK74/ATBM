package vn.edu.hcmuaf.fit.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.model.Category;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CourseServlet", value = "/courses")
public class CourseServlet extends HttpServlet {
    private static final int PAGE_SIZE = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cidStr = req.getParameter("cid");
        String level  = req.getParameter("level");
        String price  = req.getParameter("price");
        String sort   = req.getParameter("sort");
        String search = req.getParameter("q");
        int page = 1;
        try { page = Math.max(1, Integer.parseInt(req.getParameter("page"))); } catch (Exception ignored) {}

        Integer categoryId = null;
        Category currentCategory = null;
        if (cidStr != null && !cidStr.isBlank()) {
            try { categoryId = Integer.parseInt(cidStr); currentCategory = CategoryService.getInstance().getById(categoryId); } catch (Exception ignored) {}
        }

        int total = CourseService.getInstance().countCourses(categoryId, level, price, search);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        req.setAttribute("courses", CourseService.getInstance().getCourses(categoryId, level, price, sort, search, page, PAGE_SIZE));
        req.setAttribute("categories", CategoryService.getInstance().getAll());
        req.setAttribute("currentCategory", currentCategory);
        req.setAttribute("selectedLevel", level);
        req.setAttribute("selectedPrice", price);
        req.setAttribute("selectedSort", sort);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", total);
        req.getRequestDispatcher("/Courses/courses.jsp").forward(req, resp);
    }
}
