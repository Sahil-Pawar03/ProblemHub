package com.problemhub.service;

import com.problemhub.dao.UserDAO;
import com.problemhub.model.User;
import com.problemhub.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for user authentication, registration, and security operations.
 */
@Service
public class AuthService {

    private final UserDAO userDAO;

    @Autowired
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) {
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive() && PasswordUtil.checkPassword(rawPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean register(User user, String rawPassword) {
        if (user == null || rawPassword == null || rawPassword.trim().isEmpty()) {
            return false;
        }

        // Check if email already registered
        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        // Hash password with BCrypt
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        user.setPasswordHash(hashedPassword);

        int newId = userDAO.createUser(user);
        return newId > 0;
    }

    public boolean isEmailAvailable(String email) {
        return userDAO.findByEmail(email).isEmpty();
    }
}
