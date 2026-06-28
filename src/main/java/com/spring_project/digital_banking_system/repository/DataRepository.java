package com.spring_project.digital_banking_system.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring_project.digital_banking_system.model.Transaction;
import com.spring_project.digital_banking_system.model.User;
import com.spring_project.digital_banking_system.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Centralized repository for all data operations using JSON file storage.
 *
 * <p>This repository reads and writes data to JSON files in the {@code data/} directory,
 * providing CRUD operations for {@link User}, {@link Wallet}, and {@link Transaction} entities.
 * It serves as a lightweight alternative to a traditional database.</p>
 */
@Repository
public class DataRepository {

    private static final Logger log = LoggerFactory.getLogger(DataRepository.class);

    private final ObjectMapper objectMapper;
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = "users.json";
    private static final String WALLETS_FILE = "wallets.json";
    private static final String TRANSACTIONS_FILE = "transactions.json";

    public DataRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    // ==================== USER OPERATIONS ====================
    
    /**
     * Retrieves all users from the data store.
     *
     * @return a list of all users, or an empty list if none exist
     */
    public List<User> findAllUsers() {
        return readFromFile(USERS_FILE, new TypeReference<List<User>>() {});
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param id the user ID to search for
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public Optional<User> findUserById(Long id) {
        return findAllUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the user if found, or empty otherwise
     */
    public Optional<User> findUserByUsername(String username) {
        return findAllUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with the username exists, {@code false} otherwise
     */
    public boolean existsByUsername(String username) {
        return findUserByUsername(username).isPresent();
    }

    /**
     * Saves a user to the data store. If the user has no ID, a new one is generated.
     * If the user already has an ID, the existing record is replaced.
     *
     * @param user the user to save
     * @return the saved user with an assigned ID
     */
    public User saveUser(User user) {
        List<User> users = findAllUsers();
        
        if (user.getId() == null) {
            user.setId(getNextId(users.stream().map(User::getId).collect(Collectors.toList())));
            users.add(user);
        } else {
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
        }
        
        writeToFile(USERS_FILE, users);
        return user;
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(Long id) {
        List<User> users = findAllUsers();
        users.removeIf(u -> u.getId().equals(id));
        writeToFile(USERS_FILE, users);
    }

    // ==================== WALLET OPERATIONS ====================
    
    /**
     * Retrieves all wallets from the data store.
     *
     * @return a list of all wallets, or an empty list if none exist
     */
    public List<Wallet> findAllWallets() {
        return readFromFile(WALLETS_FILE, new TypeReference<List<Wallet>>() {});
    }

    /**
     * Finds a wallet by its unique ID.
     *
     * @param id the wallet ID to search for
     * @return an {@link Optional} containing the wallet if found, or empty otherwise
     */
    public Optional<Wallet> findWalletById(Long id) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds a wallet by the ID of the user who owns it.
     *
     * @param userId the owner's user ID
     * @return an {@link Optional} containing the wallet if found, or empty otherwise
     */
    public Optional<Wallet> findWalletByUserId(Long userId) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getUserId().equals(userId))
                .findFirst();
    }

    /**
     * Finds a wallet by its unique wallet code (e.g., "WAL-A1B2C3D4").
     *
     * @param walletCode the wallet code to search for
     * @return an {@link Optional} containing the wallet if found, or empty otherwise
     */
    public Optional<Wallet> findWalletByWalletCode(String walletCode) {
        return findAllWallets().stream()
                .filter(wallet -> wallet.getWalletCode().equals(walletCode))
                .findFirst();
    }

    /**
     * Saves a wallet to the data store. If the wallet has no ID, a new one is generated.
     * If the wallet already has an ID, the existing record is replaced.
     *
     * @param wallet the wallet to save
     * @return the saved wallet with an assigned ID
     */
    public Wallet saveWallet(Wallet wallet) {
        List<Wallet> wallets = findAllWallets();
        
        if (wallet.getId() == null) {
            wallet.setId(getNextId(wallets.stream().map(Wallet::getId).collect(Collectors.toList())));
            wallets.add(wallet);
        } else {
            wallets.removeIf(w -> w.getId().equals(wallet.getId()));
            wallets.add(wallet);
        }
        
        writeToFile(WALLETS_FILE, wallets);
        return wallet;
    }

    /**
     * Deletes a wallet by its ID.
     *
     * @param id the ID of the wallet to delete
     */
    public void deleteWallet(Long id) {
        List<Wallet> wallets = findAllWallets();
        wallets.removeIf(w -> w.getId().equals(id));
        writeToFile(WALLETS_FILE, wallets);
    }

    // ==================== TRANSACTION OPERATIONS ====================
    
    /**
     * Retrieves all transactions from the data store.
     *
     * @return a list of all transactions, or an empty list if none exist
     */
    public List<Transaction> findAllTransactions() {
        return readFromFile(TRANSACTIONS_FILE, new TypeReference<List<Transaction>>() {});
    }

    /**
     * Finds a transaction by its unique ID.
     *
     * @param id the transaction ID to search for
     * @return an {@link Optional} containing the transaction if found, or empty otherwise
     */
    public Optional<Transaction> findTransactionById(Long id) {
        return findAllTransactions().stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds all transactions involving a specific wallet (as sender or receiver),
     * ordered by timestamp in descending order (newest first).
     *
     * @param walletId the wallet ID to search for
     * @return a list of matching transactions, sorted by timestamp descending
     */
    public List<Transaction> findTransactionsByWalletId(Long walletId) {
        return findAllTransactions().stream()
                .filter(t -> (t.getSenderWalletId() != null && t.getSenderWalletId().equals(walletId)) ||
                            (t.getReceiverWalletId() != null && t.getReceiverWalletId().equals(walletId)))
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all transactions ordered by timestamp in descending order.
     *
     * @return all transactions sorted by timestamp descending
     */
    public List<Transaction> findAllTransactionsOrderedByTimestamp() {
        return findAllTransactions().stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Saves a transaction to the data store. If the transaction has no ID, a new one is generated.
     * If the transaction already has an ID, the existing record is replaced.
     *
     * @param transaction the transaction to save
     * @return the saved transaction with an assigned ID
     */
    public Transaction saveTransaction(Transaction transaction) {
        List<Transaction> transactions = findAllTransactions();
        
        if (transaction.getId() == null) {
            transaction.setId(getNextId(transactions.stream().map(Transaction::getId).collect(Collectors.toList())));
            transactions.add(transaction);
        } else {
            transactions.removeIf(t -> t.getId().equals(transaction.getId()));
            transactions.add(transaction);
        }
        
        writeToFile(TRANSACTIONS_FILE, transactions);
        return transaction;
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param id the ID of the transaction to delete
     */
    public void deleteTransaction(Long id) {
        List<Transaction> transactions = findAllTransactions();
        transactions.removeIf(t -> t.getId().equals(id));
        writeToFile(TRANSACTIONS_FILE, transactions);
    }

    // ==================== HELPER METHODS ====================
    
    private <T> List<T> readFromFile(String fileName, TypeReference<List<T>> typeReference) {
        try {
            File file = new File(DATA_DIR + fileName);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            log.error("Error reading from file: {} - {}", fileName, e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void writeToFile(String fileName, List<T> data) {
        try {
            File file = new File(DATA_DIR + fileName);
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            log.error("Error writing to file: {} - {}", fileName, e.getMessage());
            throw new RuntimeException("Failed to write to file: " + fileName, e);
        }
    }

    /**
     * Generates the next unique ID by finding the maximum existing ID and adding 1.
     * This prevents duplicate IDs that would occur after deletions if using list size.
     *
     * @param existingIds the list of existing IDs
     * @return the next available ID
     */
    private Long getNextId(List<Long> existingIds) {
        return existingIds.stream()
                .filter(id -> id != null)
                .max(Long::compareTo)
                .orElse(0L) + 1L;
    }
}
