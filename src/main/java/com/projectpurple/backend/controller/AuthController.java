package com.projectpurple.backend.controller;

import com.projectpurple.backend.config.JwtTokenProvider;
import com.projectpurple.backend.model.User;
import com.projectpurple.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Checks if an email is available for registration
     * This allows frontend to show 'user creating' message for new emails
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        try {
            Map<String, Object> result = authService.checkEmailAvailability(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Failed to check email availability"), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            // Check if email exists first
            if (authService.getUserByEmail(email).isPresent()) {
                return new ResponseEntity<>(Map.of(
                        "error", "Email is already registered",
                        "status", "email_exists"
                ), HttpStatus.BAD_REQUEST);
            }

            // This will show 'user creating' in the frontend
            User user = authService.registerUser(name, email, password);
            
            // Authenticate the newly registered user to generate JWT token
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            return new ResponseEntity<>(Map.of(
                    "token", jwt,
                    "user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                    ),
                    "status", "user_created"
            ), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of(
                    "error", e.getMessage(),
                    "status", "registration_failed"
            ), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();
            
            return new ResponseEntity<>(Map.of(
                    "token", jwt,
                    "user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                    )
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Invalid email or password"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(Map.of("message", "Logged out successfully"), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(Map.of("error", "Not authenticated"), HttpStatus.UNAUTHORIZED);
        }

        User user = (User) authentication.getPrincipal();
        return new ResponseEntity<>(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
        ), HttpStatus.OK);
    }
}