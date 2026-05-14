package vn.edu.hcmuaf.fit.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.model.Document;
import vn.edu.hcmuaf.fit.service.*;

import java.io.IOException;

@WebServlet(name = "AdminDocumentController", value = "/admin/documents")
public class AdminDocumentController extends HttpServlet {
    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        try { page = Math.max(1, Integer.parseInt(req.getParameter("page"))); } catch (Exception ignored) {}
        String search = req.getParameter("search");

        req.setAttribute("documents", DocumentService.getInstance().getDocuments(null, null, null, null, search, page, PAGE_SIZE));
        req.setAttribute("categories", CategoryService.getInstance().getAll());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", (int) Math.ceil((double) DocumentService.getInstance().countTotal() / PAGE_SIZE));
        req.getRequestDispatcher("/Admin/documents.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            DocumentService.getInstance().delete(Integer.parseInt(req.getParameter("id")));
        } else if ("add".equals(action)) {
            DocumentService.getInstance().add(buildFromRequest(req));
        } else if ("update".equals(action)) {
            Document d = buildFromRequest(req);
            d.setId(Integer.parseInt(req.getParameter("id")));
            DocumentService.getInstance().update(d);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/documents");
    }

    private Document buildFromRequest(HttpServletRequest req) {
        Document d = new Document();
        d.setTitle(req.getParameter("title"));
        d.setAuthor(req.getParameter("author"));
        d.setDescription(req.getParameter("description"));
        d.setThumbnailUrl(req.getParameter("thumbnailUrl"));
        d.setFileType(req.getParameter("fileType"));
        try { d.setPrice(Double.parseDouble(req.getParameter("price"))); } catch (Exception ignored) {}
        try { d.setOldPrice(Double.parseDouble(req.getParameter("oldPrice"))); } catch (Exception ignored) {}
        try { d.setCategoryId(Integer.parseInt(req.getParameter("categoryId"))); } catch (Exception ignored) {}
        try { d.setPageCount(Integer.parseInt(req.getParameter("pageCount"))); } catch (Exception ignored) {}
        try { d.setFileSizeKb(Integer.parseInt(req.getParameter("fileSizeKb"))); } catch (Exception ignored) {}
        d.setBadge(req.getParameter("badge"));
        return d;
    }
}
