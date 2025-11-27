package com.calvin.wallet.wallet_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletResponse {
    private String walletId;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;

    // Constructors
    public WalletResponse() {}

    public WalletResponse(String walletId, Long userId, BigDecimal balance, String currency, LocalDateTime createdAt) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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
}