package vn.edu.hcmuaf.fit.service;

import vn.edu.hcmuaf.fit.dao.CustomerDAO;
import vn.edu.hcmuaf.fit.model.Customer;

public class CustomerService {
    private static final CustomerService instance = new CustomerService();
    public static CustomerService getInstance() { return instance; }
    private CustomerService() {}

    public boolean updateCustomerInfo(int accountId, String fullName, String phone) {
        return new CustomerDAO().updateCustomerInfo(accountId, fullName, phone);
    }

    public boolean updateAvatar(int accountId, String avatarUrl) {
        return new CustomerDAO().updateAvatar(accountId, avatarUrl);
    }

    public Customer getCustomerByAccountId(int accountId) {
        return new CustomerDAO().getCustomerByAccountId(accountId);
    }

    public void ensureCustomerRecord(int accountId) {
        Customer existing = getCustomerByAccountId(accountId);
        if (existing == null) {
            new CustomerDAO().createCustomerRecord(accountId);
        }
    }
}