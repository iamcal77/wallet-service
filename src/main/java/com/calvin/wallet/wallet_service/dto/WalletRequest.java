package com.calvin.wallet.wallet_service.dto;

import jakarta.validation.constraints.NotNull;

public class WalletRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    private String currency;

    // Constructors
    public WalletRequest() {}

    public WalletRequest(Long userId, String currency) {
        this.userId = userId;
        this.currency = currency;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}