package com.calvin.wallet.wallet_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String fromWalletId;

    private String toWalletId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String description;

    @Column(nullable = false)
    private String status;

    // Constructors
    public Transaction() {
        this.timestamp = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public Transaction(String transactionId, String fromWalletId, BigDecimal amount, TransactionType type) {
        this();
        this.transactionId = transactionId;
        this.fromWalletId = fromWalletId;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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