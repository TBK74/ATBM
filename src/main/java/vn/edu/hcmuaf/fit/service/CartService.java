package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.CartDAO;
import vn.edu.hcmuaf.fit.dao.CourseDAO;
import vn.edu.hcmuaf.fit.dao.DocumentDAO;
import vn.edu.hcmuaf.fit.dao.EnrollmentDAO;
import vn.edu.hcmuaf.fit.dao.DocumentAccessDAO;
import vn.edu.hcmuaf.fit.model.Cart;
import vn.edu.hcmuaf.fit.model.Course;
import vn.edu.hcmuaf.fit.model.Document;

public class CartService {
    private static final CartService instance = new CartService();
    private CartService() {}
    public static CartService getInstance() { return instance; }

    public Cart getCart(int customerId) {
        return CartDAO.getInstance().getCartByCustomerId(customerId);
    }

    public boolean addCourse(int customerId, int courseId) {
        // Không cho thêm nếu đã sở hữu
        if (EnrollmentDAO.getInstance().isEnrolled(customerId, courseId)) return false;
        Cart cart = getCart(customerId);
        if (cart.contains("course", courseId)) return false;
        Course course = CourseDAO.getInstance().getById(courseId);
        if (course == null) return false;
        return CartDAO.getInstance().addItem(customerId, "course", courseId, course.getPrice());
    }

    public boolean addDocument(int customerId, int documentId) {
        // Không cho thêm nếu đã sở hữu
        if (DocumentAccessDAO.getInstance().hasAccess(customerId, documentId)) return false;
        Cart cart = getCart(customerId);
        if (cart.contains("document", documentId)) return false;
        Document doc = DocumentDAO.getInstance().getById(documentId);
        if (doc == null) return false;
        return CartDAO.getInstance().addItem(customerId, "document", documentId, doc.getPrice());
    }

    public boolean removeItem(int customerId, String itemType, int itemId) {
        return CartDAO.getInstance().removeItem(customerId, itemType, itemId);
    }

    public boolean clearCart(int customerId) {
        return CartDAO.getInstance().clearCart(customerId);
    }
}
