package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.DocumentDAO;
import vn.edu.hcmuaf.fit.model.Document;
import java.util.List;

public class DocumentService {
    private static final DocumentService instance = new DocumentService();
    private DocumentService() {}
    public static DocumentService getInstance() { return instance; }

    public List<Document> getDocuments(Integer categoryId, String fileType, String priceRange, String sort, String search, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return DocumentDAO.getInstance().getAll(categoryId, fileType, priceRange, sort, search, offset, pageSize);
    }

    public int countDocuments(Integer categoryId, String fileType, String priceRange, String search) {
        return DocumentDAO.getInstance().count(categoryId, fileType, priceRange, search);
    }

    public Document getById(int id) { return DocumentDAO.getInstance().getById(id); }
    public List<Document> getNewest(int limit) { return DocumentDAO.getInstance().getNewest(limit); }
    public List<Document> getRelated(int docId, int categoryId, int limit) { return DocumentDAO.getInstance().getRelated(docId, categoryId, limit); }
    public boolean add(Document d) { return DocumentDAO.getInstance().insert(d); }
    public boolean update(Document d) { return DocumentDAO.getInstance().update(d); }
    public boolean delete(int id) { return DocumentDAO.getInstance().softDelete(id); }
    public int countTotal() { return DocumentDAO.getInstance().countTotal(); }
    public String getFilePath(int id) { return DocumentDAO.getInstance().getFilePath(id); }
}
