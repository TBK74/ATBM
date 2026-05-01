package vn.edu.hcmuaf.fit.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    // key = "course_1" hoặc "document_3"
    private Map<String, CartItem> data = new LinkedHashMap<>();

    public String buildKey(String type, int id) {
        return type + "_" + id;
    }

    public void addCourse(Course course) {
        String key = buildKey("course", course.getId());
        if (!data.containsKey(key)) {
            data.put(key, new CartItem(course));
        }
    }

    public void addDocument(Document doc) {
        String key = buildKey("document", doc.getId());
        if (!data.containsKey(key)) {
            data.put(key, new CartItem(doc));
        }
    }

    public void remove(String type, int id) {
        data.remove(buildKey(type, id));
    }

    public boolean contains(String type, int id) {
        return data.containsKey(buildKey(type, id));
    }

    public Map<String, CartItem> getData() { return data; }

    public int getTotalQuantity() { return data.size(); }

    public double getTotalPrice() {
        return data.values().stream().mapToDouble(CartItem::getPrice).sum();
    }

    public boolean isEmpty() { return data.isEmpty(); }
}
