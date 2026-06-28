package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for authentication operations.
 *
 * <p>Handles user registration, login, and logout. All endpoints under
 * {@code /api/auth/} are publicly accessible (no authentication required).</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account and creates an associated wallet.
     *
     * @param request a map containing {@code username}, {@code email}, {@code password},
     *                and optionally {@code role} and {@code masterSecretKey}
     * @return a map with registration confirmation details
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and creates an HTTP session.
     *
     * @param request     a map containing {@code username} and {@code password}
     * @param httpRequest the HTTP request used to create a session
     * @return a map with login confirmation, session ID, and wallet code
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request,
                                                     HttpServletRequest httpRequest) {
        Map<String, Object> response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the current user by invalidating their session.
     *
     * @param request the HTTP request containing the session to invalidate
     * @return a map with a logout confirmation message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = authService.logout(request);
        return ResponseEntity.ok(response);
    }
}