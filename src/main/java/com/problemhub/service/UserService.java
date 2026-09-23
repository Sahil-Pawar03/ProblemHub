package com.problemhub.service;

import com.problemhub.dao.UserDAO;
import com.problemhub.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for user profile and directory management.
 */
@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> getUserById(int id) {
        return userDAO.findById(id);
    }

    public boolean updateProfile(User user) {
        return userDAO.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public int getTotalUsersCount() {
        return userDAO.countAll();
    }

    public int getStudentsCount() {
        return userDAO.countByRole("STUDENT");
    }
}
