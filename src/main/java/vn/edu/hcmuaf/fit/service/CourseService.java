package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.CourseDAO;
import vn.edu.hcmuaf.fit.model.Course;
import java.util.List;

public class CourseService {
    private static final CourseService instance = new CourseService();
    private CourseService() {}
    public static CourseService getInstance() { return instance; }

    public List<Course> getCourses(Integer categoryId, String level, String priceRange, String sort, String search, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return CourseDAO.getInstance().getAll(categoryId, level, priceRange, sort, search, offset, pageSize);
    }

    public int countCourses(Integer categoryId, String level, String priceRange, String search) {
        return CourseDAO.getInstance().count(categoryId, level, priceRange, search);
    }

    public Course getById(int id) { return CourseDAO.getInstance().getById(id); }

    public List<Course> getFeatured(int limit) { return CourseDAO.getInstance().getFeatured(limit); }

    public List<Course> getRelated(int courseId, int categoryId, int limit) {
        return CourseDAO.getInstance().getRelated(courseId, categoryId, limit);
    }

    public boolean add(Course c) { return CourseDAO.getInstance().insert(c); }
    public boolean update(Course c) { return CourseDAO.getInstance().update(c); }
    public boolean delete(int id) { return CourseDAO.getInstance().softDelete(id); }
    public int countTotal() { return CourseDAO.getInstance().countTotal(); }

    public List<Course> getAdminList(String search, Integer categoryId, String level, int page, int pageSize) {
        return CourseDAO.getInstance().getAdminList(search, categoryId, level, (page-1)*pageSize, pageSize);
    }
}
