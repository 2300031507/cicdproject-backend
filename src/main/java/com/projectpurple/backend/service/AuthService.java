package com.projectpurple.backend.service;

import com.projectpurple.backend.model.User;
import com.projectpurple.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Checks if user email exists and returns status
     * @param email User's email
     * @return Map containing status and message
     */
    public Map<String, Object> checkEmailAvailability(String email) {
        Map<String, Object> result = new HashMap<>();
        boolean exists = userRepository.existsByEmail(email);
        result.put("exists", exists);
        result.put("message", exists ? "Email is already registered" : "Email is available");
        return result;
    }

    public User registerUser(String name, String email, String password) {
        // This check is redundant but kept for safety
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}