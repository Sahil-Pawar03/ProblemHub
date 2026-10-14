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
    private final com.problemhub.dao.OrganizationDAO organizationDAO;

    @Autowired
    public AuthService(UserDAO userDAO, com.problemhub.dao.OrganizationDAO organizationDAO) {
        this.userDAO = userDAO;
        this.organizationDAO = organizationDAO;
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
        if (newId > 0) {
            user.setId(newId);
            if (user.isOrganization()) {
                com.problemhub.model.Organization org = new com.problemhub.model.Organization();
                org.setUserId(newId);
                org.setOrgName(user.getFullName());
                org.setOrgType("Industry");
                org.setContactEmail(user.getEmail());
                org.setDescription(user.getBio() != null ? user.getBio() : user.getFullName() + " Industry / NGO Partner");
                org.setVerified(true);
                organizationDAO.createOrganization(org);
            }
            return true;
        }
        return false;
    }

    public boolean isEmailAvailable(String email) {
        return userDAO.findByEmail(email).isEmpty();
    }
}
