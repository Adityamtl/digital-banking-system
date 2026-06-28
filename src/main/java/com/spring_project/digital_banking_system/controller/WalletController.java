package com.spring_project.digital_banking_system.controller;

import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.service.AuthService;
import com.spring_project.digital_banking_system.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for wallet operations.
 *
 * <p>Provides endpoints for balance inquiries, deposits, withdrawals, transfers,
 * and transaction history. All endpoints require an authenticated user session.</p>
 */
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;

    public WalletController(WalletService walletService, AuthService authService) {
        this.walletService = walletService;
        this.authService = authService;
    }

    /**
     * Retrieves the current balance of the authenticated user's wallet.
     *
     * @param request the HTTP request containing the user's session
     * @return a map with wallet code and current balance
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deposits funds into the authenticated user's wallet.
     *
     * @param depositRequest a map containing the {@code amount} to deposit
     * @param request        the HTTP request containing the user's session
     * @return a map with success message, new balance, and transaction ID
     */
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody Map<String, Object> depositRequest,
                                                       HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.deposit(userId, depositRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Withdraws funds from the authenticated user's wallet.
     *
     * @param withdrawRequest a map containing the {@code amount} to withdraw
     * @param request         the HTTP request containing the user's session
     * @return a map with success message, new balance, and transaction ID
     */
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@RequestBody Map<String, Object> withdrawRequest,
                                                        HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.withdraw(userId, withdrawRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Transfers funds from the authenticated user's wallet to another wallet.
     *
     * @param transferRequest a map containing {@code targetWalletCode} and {@code amount}
     * @param request         the HTTP request containing the user's session
     * @return a map with success message, new balance, transaction ID, and recipient wallet code
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody Map<String, Object> transferRequest,
                                                        HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        Map<String, Object> response = walletService.transfer(userId, transferRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the transaction history of the authenticated user's wallet.
     *
     * @param request the HTTP request containing the user's session
     * @return a list of transactions involving the user's wallet, ordered by timestamp descending
     */
    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getHistory(HttpServletRequest request) {
        Long userId = authService.getCurrentUserId(request);
        List<Transaction> history = walletService.getHistory(userId);
        return ResponseEntity.ok(history);
    }
}