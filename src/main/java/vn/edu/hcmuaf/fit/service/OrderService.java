package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.CartDAO;
import vn.edu.hcmuaf.fit.service.OrderSignatureService;
import vn.edu.hcmuaf.fit.dao.EnrollmentDAO;
import vn.edu.hcmuaf.fit.dao.DocumentAccessDAO;
import vn.edu.hcmuaf.fit.dao.OrderDAO;
import vn.edu.hcmuaf.fit.model.*;

import java.util.Date;

public class OrderService {
    private static final OrderService instance = new OrderService();
    private OrderService() {}
    public static OrderService getInstance() { return instance; }

    public int placeOrder(int customerId, String recipientName, String paymentMethod, String promoCode) {
        Cart cart = CartService.getInstance().getCart(customerId);
        if (cart == null || cart.isEmpty()) return -1;

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus("Pending");
        order.setRecipientName(recipientName);
        order.setPaymentMethod(paymentMethod);

        double total = cart.getTotalPrice();
        double discount = 0;

        if (promoCode != null && !promoCode.isBlank()) {
            PromoCode promo = PromoCodeService.getInstance().findByCode(promoCode.trim().toUpperCase());
            if (promo != null && promo.isActive()) {
                Date now = new Date();
                boolean valid = (promo.getStartAt() == null || !now.before(promo.getStartAt()))
                        && (promo.getEndAt() == null || !now.after(promo.getEndAt()))
                        && (promo.getUsageLimit() <= 0 || promo.getUsedCount() < promo.getUsageLimit())
                        && total >= promo.getMinOrderValue();
                if (valid) {
                    discount = "percent".equals(promo.getType())
                            ? Math.min(total * promo.getAmount() / 100, total)
                            : Math.min(promo.getAmount(), total);
                    order.setPromoCode(promoCode.trim().toUpperCase());
                    order.setDiscountAmount(discount);
                    PromoCodeService.getInstance().incrementUsedCount(promo.getId());
                }
            }
        }

        order.setTotalAmount(total - discount);
        int orderId = OrderDAO.getInstance().createOrder(order, cart);

        if (orderId != -1) {
            OrderSignatureService.getInstance().initHash(orderId);
            for (CartItem item : cart.getData().values()) {
                if ("course".equals(item.getItemType())) {
                    EnrollmentDAO.getInstance().enroll(customerId, item.getItemId(), orderId);
                } else if ("document".equals(item.getItemType())) {
                    DocumentAccessDAO.getInstance().grantAccess(customerId, item.getItemId(), orderId);
                }
            }
            CartService.getInstance().clearCart(customerId);
        }
        return orderId;
    }

    public boolean confirmOrder(int orderId) {
        return OrderDAO.getInstance().updateStatus(orderId, "Completed");
    }
}