package vn.edu.hcmuaf.fit.controller.custome;

import vn.edu.hcmuaf.fit.model.Customer;
import vn.edu.hcmuaf.fit.model.User;
import vn.edu.hcmuaf.fit.service.CustomerService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "CustomerController", value = "/update-profile")
public class CustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("auth");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/Customer/Profile/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("auth");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        // Cho phép cập nhật từng trường riêng lẻ — không bắt buộc điền đủ tất cả.
        // Chỉ validate trường nào người dùng thực sự điền.
        if (phone != null && !phone.isBlank() && !phone.matches("\\d{10,11}")) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (phải là 10-11 số)!");
            request.getRequestDispatcher("/Customer/Profile/profile.jsp").forward(request, response);
            return;
        }

        boolean isUpdated = CustomerService.getInstance().updateCustomerInfo(
                user.getAccountID(), fullName, phone);

        if (isUpdated) {
            Customer newInfo = CustomerService.getInstance().getCustomerByAccountId(user.getAccountID());
            session.setAttribute("customer", newInfo);
            request.setAttribute("message", "Cập nhật thông tin thành công!");
        } else {
            request.setAttribute("error", "Cập nhật thất bại. Vui lòng thử lại!");
        }

        request.getRequestDispatcher("/Customer/Profile/profile.jsp").forward(request, response);
    }
}
