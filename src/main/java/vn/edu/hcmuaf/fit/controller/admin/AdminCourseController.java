package vn.edu.hcmuaf.fit.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.model.Course;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;

@WebServlet(name = "AdminCourseController", value = "/admin/courses")
public class AdminCourseController extends HttpServlet {
    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        String catStr = req.getParameter("category");
        String level  = req.getParameter("level");
        int page = 1;
        try { page = Math.max(1, Integer.parseInt(req.getParameter("page"))); } catch (Exception ignored) {}

        Integer categoryId = null;
        if (catStr != null && !catStr.isBlank()) { try { categoryId = Integer.parseInt(catStr); } catch (Exception ignored) {} }

        req.setAttribute("courses", CourseService.getInstance().getAdminList(search, categoryId, level, page, PAGE_SIZE));
        req.setAttribute("categories", CategoryService.getInstance().getAll());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) CourseService.getInstance().countTotal() / PAGE_SIZE));
        req.getRequestDispatcher("/Admin/courses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            CourseService.getInstance().delete(Integer.parseInt(req.getParameter("id")));
        } else if ("add".equals(action)) {
            Course c = buildFromRequest(req);
            CourseService.getInstance().add(c);
        } else if ("update".equals(action)) {
            Course c = buildFromRequest(req);
            c.setId(Integer.parseInt(req.getParameter("id")));
            CourseService.getInstance().update(c);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/courses");
    }

    private Course buildFromRequest(HttpServletRequest req) {
        Course c = new Course();
        c.setTitle(req.getParameter("title"));
        c.setShortDesc(req.getParameter("shortDesc"));
        c.setDescription(req.getParameter("description"));
        c.setThumbnailUrl(req.getParameter("thumbnailUrl"));
        c.setLevel(req.getParameter("level"));
        c.setLanguage(req.getParameter("language"));
        try { c.setDurationHours(Double.parseDouble(req.getParameter("durationHours"))); } catch (Exception ignored) {}
        try { c.setPrice(Double.parseDouble(req.getParameter("price"))); } catch (Exception ignored) {}
        try { c.setOldPrice(Double.parseDouble(req.getParameter("oldPrice"))); } catch (Exception ignored) {}
        try { c.setCategoryId(Integer.parseInt(req.getParameter("categoryId"))); } catch (Exception ignored) {}
        try { c.setInstructorId(Integer.parseInt(req.getParameter("instructorId"))); } catch (Exception ignored) {}
        c.setBadge(req.getParameter("badge"));
        return c;
    }
}
