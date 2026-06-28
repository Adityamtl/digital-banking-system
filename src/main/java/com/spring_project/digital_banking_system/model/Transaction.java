package com.spring_project.digital_banking_system.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction model - Plain Java Object for file-based storage
 */
public class Transaction implements Serializable {
    private Long id;
    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private TransactionStatus status;

    public Transaction() {
    }

    public Transaction(Long senderWalletId, Long receiverWalletId,
                       BigDecimal amount, TransactionType type, TransactionStatus status) {
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(Long senderWalletId) { this.senderWalletId = senderWalletId; }

    public Long getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(Long receiverWalletId) { this.receiverWalletId = receiverWalletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}