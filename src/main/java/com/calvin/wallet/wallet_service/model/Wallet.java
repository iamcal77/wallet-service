package com.calvin.wallet.wallet_service.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String walletId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal balance;

    private String currency;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.currency = "USD";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Wallet(String walletId, Long userId) {
        this();
        this.walletId = walletId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}