package com.calvin.wallet.wallet_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransactionRequest {
    @NotNull(message = "Wallet ID is required")
    private String walletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String toWalletId; // For transfers
    private String description;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(String walletId, BigDecimal amount, String toWalletId, String description) {
        this.walletId = walletId;
        this.amount = amount;
        this.toWalletId = toWalletId;
        this.description = description;
    }

    // Getters and Setters
    public String getWalletId() { return walletId; }
    public void setWalletId(String walletId) { this.walletId = walletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getToWalletId() { return toWalletId; }
    public void setToWalletId(String toWalletId) { this.toWalletId = toWalletId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}