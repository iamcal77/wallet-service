package com.calvin.wallet.wallet_service.controller;

import com.calvin.wallet.wallet_service.dto.WalletRequest;
import com.calvin.wallet.wallet_service.dto.WalletResponse;
import com.calvin.wallet.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<?> createWallet(@Valid @RequestBody WalletRequest request) {
        try {
            WalletResponse response = walletService.createWallet(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already has a wallet")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("WALLET_ALREADY_EXISTS", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("WALLET_CREATION_FAILED", "Failed to create wallet: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<?> getWallet(@PathVariable String walletId) {
        try {
            // Validate wallet ID format
            if (walletId == null || walletId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_ID", "Wallet ID cannot be null or empty"));
            }

            if (!walletId.matches("^WAL[A-Z0-9]{13}$")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_FORMAT", "Wallet ID must be in format WAL followed by 13 alphanumeric characters"));
            }

            WalletResponse response = walletService.getWallet(walletId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("WALLET_RETRIEVAL_FAILED", "Failed to retrieve wallet: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getWalletByUserId(@PathVariable Long userId) {
        try {
            // Validate user ID
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_USER_ID", "User ID cannot be null"));
            }

            if (userId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_USER_ID", "User ID must be a positive number"));
            }

            WalletResponse response = walletService.getWalletByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("WALLET_RETRIEVAL_FAILED", "Failed to retrieve wallet: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String walletId) {
        try {
            // Validate wallet ID format
            if (walletId == null || walletId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_ID", "Wallet ID cannot be null or empty"));
            }

            if (!walletId.matches("^WAL[A-Z0-9]{13}$")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_FORMAT", "Wallet ID must be in format WAL followed by 13 alphanumeric characters"));
            }

            BigDecimal balance = walletService.getBalance(walletId);
            return ResponseEntity.ok(new BalanceResponse(walletId, balance));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("BALANCE_RETRIEVAL_FAILED", "Failed to retrieve balance: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
        }
    }

    // Global exception handler for validation errors
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");

        return new ErrorResponse("VALIDATION_ERROR", errorMessage);
    }

    // Exception handler for general exceptions
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllExceptions(Exception ex) {
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: " + ex.getMessage());
    }

    // Response classes for structured error responses
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private long timestamp;

        public ErrorResponse(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and setters
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class BalanceResponse {
        private String walletId;
        private BigDecimal balance;
        private String currency;

        public BalanceResponse(String walletId, BigDecimal balance) {
            this.walletId = walletId;
            this.balance = balance;
            this.currency = "USD"; // You can make this dynamic based on wallet currency
        }

        // Getters and setters
        public String getWalletId() { return walletId; }
        public void setWalletId(String walletId) { this.walletId = walletId; }

        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}