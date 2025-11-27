package com.calvin.wallet.wallet_service.dto;


import com.calvin.wallet.wallet_service.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private String transactionId;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String description;
    private String status;

    // Constructors
    public TransactionResponse() {}

    public TransactionResponse(String transactionId, String fromWalletId, String toWalletId,
                               BigDecimal amount, TransactionType type, LocalDateTime timestamp,
                               String description, String status) {
        this.transactionId = transactionId;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getFromWalletId() { return fromWalletId; }
    public void setFromWalletId(String fromWalletId) { this.fromWalletId = fromWalletId; }

    public String getToWalletId() { return toWalletId; }
    public void setToWalletId(String toWalletId) { this.toWalletId = toWalletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}