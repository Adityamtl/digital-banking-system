package com.spring_project.digital_banking_system.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wallet model - Plain Java Object for file-based storage
 */
public class Wallet implements Serializable {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String walletCode;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }

    public Wallet(Long userId) {
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.walletCode = "WAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getWalletCode() { return walletCode; }
    public void setWalletCode(String walletCode) { this.walletCode = walletCode; }
}