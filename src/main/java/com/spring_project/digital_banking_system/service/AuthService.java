package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.model.Role;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.model.Wallet;
import com.spring_project.digital_banking_system.repository.DataRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service handling user registration, login, and logout.
 *
 * <p>Uses HTTP session-based authentication with Spring Security integration.
 * Supports role-based access control with USER and ADMIN roles. Admin registration
 * requires a master secret key for verification.</p>
 */
@Service
public class AuthService {

    private final DataRepository dataRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.master-key:CHANGE_ME_IN_PRODUCTION}")
    private String masterSecretKey;

    public AuthService(DataRepository dataRepository,
                       PasswordEncoder passwordEncoder) {
        this.dataRepository = dataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user and creates an associated wallet.
     *
     * <p>If the role is set to "ADMIN", the request must include a valid
     * {@code masterSecretKey} matching the configured admin key.</p>
     *
     * @param request a map containing {@code username}, {@code email}, {@code password},
     *                and optionally {@code role} and {@code masterSecretKey}
     * @return a map with registration confirmation, username, role, and wallet code
     * @throws IllegalArgumentException if required fields are missing, username exists,
     *                                  or admin key is invalid
     */
    public Map<String, Object> register(Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String roleStr = request.get("role");
        String masterKey = request.get("masterSecretKey");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (dataRepository.findUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = Role.USER;
        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            if (masterKey == null || !masterKey.equals(masterSecretKey)) {
                throw new IllegalArgumentException("Invalid master secret key for admin registration");
            }
            role = Role.ADMIN;
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(password),
                role
        );

        dataRepository.saveUser(user);

        Wallet wallet = new Wallet(user.getId());
        dataRepository.saveWallet(wallet);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("walletCode", wallet.getWalletCode());
        return response;
    }

    /**
     * Authenticates a user and creates an HTTP session with their credentials.
     *
     * @param request     a map containing {@code username} and {@code password}
     * @param httpRequest the HTTP request used to create a session
     * @return a map with login confirmation, username, role, session ID, and wallet code
     * @throws IllegalArgumentException if the credentials are invalid
     */
    public Map<String, Object> login(Map<String, String> request, HttpServletRequest httpRequest) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<User> userOpt = dataRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Create session and store user info
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().name());

        // Set Spring Security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<Wallet> walletOpt = dataRepository.findWalletByUserId(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("sessionId", session.getId());
        if (walletOpt.isPresent()) {
            response.put("walletCode", walletOpt.get().getWalletCode());
        }
        return response;
    }

    /**
     * Logs out the current user by invalidating their session and clearing the security context.
     *
     * @param request the HTTP request containing the session to invalidate
     * @return a map with a logout confirmation message
     */
    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        return response;
    }

    /**
     * Retrieves the currently authenticated user's ID from the HTTP session.
     *
     * @param request the HTTP request containing the active session
     * @return the current user's ID
     * @throws IllegalStateException if no active session or user ID is found
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                return userId;
            }
        }
        throw new IllegalStateException("No active session found");
    }
}