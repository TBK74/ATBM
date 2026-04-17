package vn.edu.hcmuaf.fit.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import vn.edu.hcmuaf.fit.dao.UserDAO;
import vn.edu.hcmuaf.fit.model.User;

public class UserService {
    private static final UserService instance = new UserService();
    public static UserService getInstance() { return instance; }
    private UserService() {}

    public User checkLogin(String username, String password) {
        UserDAO dao = new UserDAO();
        User user = dao.getUserByUsername(username);
        if (user == null) return null;

        boolean matched = BCrypt.verifyer()
                .verify(password.toCharArray(), user.getPasswordHash())
                .verified;
        return matched ? user : null;
    }

    public void register(String username, String password, String email) {
        new UserDAO().register(username, hashPassword(password), email);
    }

    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}