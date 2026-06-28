package com.spring_project.digital_banking_system.service;

import com.spring_project.digital_banking_system.model.*;
import com.spring_project.digital_banking_system.repository.DataRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for wallet operations including deposits, withdrawals, transfers,
 * and transaction history management.
 *
 * <p>Also handles admin-specific operations such as viewing all users/transactions
 * and performing bank-initiated transfers.</p>
 */
@Service
public class WalletService {

    private final DataRepository dataRepository;

    public WalletService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Retrieves the current balance and wallet code for a user's wallet.
     *
     * @param userId the ID of the wallet owner
     * @return a map containing {@code walletCode} and {@code balance}
     * @throws RuntimeException if no wallet is found for the user
     */
    public Map<String, Object> getBalance(Long userId) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        Map<String, Object> response = new HashMap<>();
        response.put("walletCode", wallet.getWalletCode());
        response.put("balance", wallet.getBalance());
        return response;
    }

    /**
     * Deposits funds into the authenticated user's wallet.
     *
     * @param userId the ID of the wallet owner
     * @param request a map containing the {@code amount} to deposit (must be greater than zero)
     * @return a map containing success message, new balance, and transaction ID
     * @throws IllegalArgumentException if the amount is null, zero, or negative
     * @throws RuntimeException if no wallet is found for the user
     */
    public Map<String, Object> deposit(Long userId, Map<String, Object> request) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        BigDecimal amount = parseAndValidateAmount(request.get("amount"));

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        dataRepository.saveWallet(wallet);

        Transaction transaction = new Transaction(
                null,
                wallet.getId(),
                amount,
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    /**
     * Withdraws funds from the authenticated user's wallet.
     *
     * @param userId the ID of the wallet owner
     * @param request a map containing the {@code amount} to withdraw (must be greater than zero)
     * @return a map containing success message, new balance, and transaction ID
     * @throws IllegalArgumentException if the amount is null, zero, or negative
     * @throws RuntimeException if no wallet is found or if the balance is insufficient
     */
    public Map<String, Object> withdraw(Long userId, Map<String, Object> request) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        BigDecimal amount = parseAndValidateAmount(request.get("amount"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            Transaction failedTransaction = new Transaction(
                    wallet.getId(),
                    null,
                    amount,
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED
            );
            dataRepository.saveTransaction(failedTransaction);
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        dataRepository.saveWallet(wallet);

        Transaction transaction = new Transaction(
                wallet.getId(),
                null,
                amount,
                TransactionType.WITHDRAWAL,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Withdrawal successful");
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    /**
     * Transfers funds from the authenticated user's wallet to another wallet.
     *
     * @param userId the ID of the sender
     * @param request a map containing {@code targetWalletCode} and {@code amount}
     * @return a map containing success message, new balance, transaction ID, and recipient wallet code
     * @throws IllegalArgumentException if the amount is invalid or the user tries to transfer to their own wallet
     * @throws RuntimeException if either wallet is not found or if the balance is insufficient
     */
    public Map<String, Object> transfer(Long userId, Map<String, Object> request) {
        Wallet senderWallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        String targetWalletCode = request.get("targetWalletCode").toString();
        Wallet receiverWallet = dataRepository.findWalletByWalletCode(targetWalletCode)
                .orElseThrow(() -> new RuntimeException("Target wallet not found"));

        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to your own wallet");
        }

        BigDecimal amount = parseAndValidateAmount(request.get("amount"));

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            Transaction failedTransaction = new Transaction(
                    senderWallet.getId(),
                    receiverWallet.getId(),
                    amount,
                    TransactionType.TRANSFER,
                    TransactionStatus.FAILED
            );
            dataRepository.saveTransaction(failedTransaction);
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal senderNewBalance = senderWallet.getBalance().subtract(amount);
        BigDecimal receiverNewBalance = receiverWallet.getBalance().add(amount);

        senderWallet.setBalance(senderNewBalance);
        receiverWallet.setBalance(receiverNewBalance);

        dataRepository.saveWallet(senderWallet);
        dataRepository.saveWallet(receiverWallet);

        Transaction transaction = new Transaction(
                senderWallet.getId(),
                receiverWallet.getId(),
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Transfer successful");
        response.put("newBalance", senderNewBalance);
        response.put("transactionId", transaction.getId());
        response.put("recipientWalletCode", receiverWallet.getWalletCode());
        return response;
    }

    /**
     * Retrieves the transaction history for a user's wallet, ordered by timestamp descending.
     *
     * @param userId the ID of the wallet owner
     * @return a list of transactions involving the user's wallet
     * @throws RuntimeException if no wallet is found for the user
     */
    public List<Transaction> getHistory(Long userId) {
        Wallet wallet = dataRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        return dataRepository.findTransactionsByWalletId(wallet.getId());
    }

    // ==================== ADMIN OPERATIONS ====================

    /**
     * Retrieves all transactions in the system. Requires ADMIN role.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return dataRepository.findAllTransactions();
    }

    /**
     * Retrieves all registered users. Requires ADMIN role.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return dataRepository.findAllUsers();
    }

    /**
     * Performs a bank-initiated transfer to a target wallet. Requires ADMIN role.
     * This operation adds funds without deducting from any user's wallet.
     *
     * @param request a map containing {@code targetWalletCode} and {@code amount}
     * @return a map containing success message, target wallet code, amount, new balance, and transaction ID
     * @throws IllegalArgumentException if the amount is invalid
     * @throws RuntimeException if the target wallet is not found
     */
    public Map<String, Object> bankTransfer(Map<String, Object> request) {
        String targetWalletCode = request.get("targetWalletCode").toString();
        BigDecimal amount = parseAndValidateAmount(request.get("amount"));

        Wallet targetWallet = dataRepository.findWalletByWalletCode(targetWalletCode)
                .orElseThrow(() -> new RuntimeException("Target wallet not found"));

        BigDecimal newBalance = targetWallet.getBalance().add(amount);
        targetWallet.setBalance(newBalance);
        dataRepository.saveWallet(targetWallet);

        Transaction transaction = new Transaction(
                null,
                targetWallet.getId(),
                amount,
                TransactionType.BANK_TRANSFER,
                TransactionStatus.SUCCESS
        );
        dataRepository.saveTransaction(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bank transfer successful");
        response.put("targetWalletCode", targetWalletCode);
        response.put("amount", amount);
        response.put("newBalance", newBalance);
        response.put("transactionId", transaction.getId());
        return response;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Parses and validates a monetary amount from the request payload.
     *
     * @param amountObj the raw amount value from the request
     * @return the validated {@link BigDecimal} amount
     * @throws IllegalArgumentException if the amount is null, zero, or negative
     */
    private BigDecimal parseAndValidateAmount(Object amountObj) {
        if (amountObj == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountObj);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        return amount;
    }
}