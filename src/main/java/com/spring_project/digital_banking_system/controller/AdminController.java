package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for admin-only operations.
 *
 * <p>All endpoints require the ADMIN role. Provides access to system-wide
 * data including all users, transactions, and bank-initiated transfers.</p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final WalletService walletService;

    public AdminController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Retrieves all transactions in the system.
     *
     * @return a list of all transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = walletService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves all registered users.
     *
     * @return a list of all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = walletService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Performs a bank-initiated transfer to a target wallet.
     * This adds funds without deducting from any user's wallet.
     *
     * @param request a map containing {@code targetWalletCode} and {@code amount}
     * @return a map with transfer confirmation details
     */
    @PostMapping("/bank-transfer")
    public ResponseEntity<Map<String, Object>> bankTransfer(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = walletService.bankTransfer(request);
        return ResponseEntity.ok(response);
    }
}