package vn.edu.hcmuaf.fit.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private int customerID;
    private int accountID;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;

    public Customer() {}

    public Customer(int customerID, int accountID, String fullName, String phoneNumber, String avatarUrl) {
        this.customerID = customerID;
        this.accountID = accountID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @Override
    public String toString() {
        return "Customer{customerID=" + customerID + ", accountID=" + accountID +
                ", fullName='" + fullName + '\'' + ", phoneNumber='" + phoneNumber + '\'' + '}';
    }
}
