package com.problemhub.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 * Ensures passwords are never stored in plain-text.
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * Hashes a raw plain-text password using BCrypt.
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return ENCODER.encode(rawPassword);
    }

    /**
     * Checks if a raw password matches the BCrypt hash.
     */
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, hashedPassword);
    }
}
