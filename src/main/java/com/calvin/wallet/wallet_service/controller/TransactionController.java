package com.calvin.wallet.wallet_service.controller;

import com.calvin.wallet.wallet_service.dto.TransactionRequest;
import com.calvin.wallet.wallet_service.dto.TransactionResponse;
import com.calvin.wallet.wallet_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequest request) {
        try {
            // Additional validation
            if (request.getWalletId() == null || request.getWalletId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_ID", "Wallet ID is required for deposit"));
            }

            if (!isValidWalletId(request.getWalletId())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_FORMAT", "Invalid wallet ID format"));
            }

            if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", "Amount must be greater than 0"));
            }

            TransactionResponse response = transactionService.deposit(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            if (e.getMessage().contains("greater than 0")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("DEPOSIT_FAILED", "Failed to process deposit: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred during deposit"));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequest request) {
        try {
            // Additional validation
            if (request.getWalletId() == null || request.getWalletId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_ID", "Wallet ID is required for withdrawal"));
            }

            if (!isValidWalletId(request.getWalletId())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_FORMAT", "Invalid wallet ID format"));
            }

            if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", "Amount must be greater than 0"));
            }

            TransactionResponse response = transactionService.withdraw(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            if (e.getMessage().contains("Insufficient funds")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("INSUFFICIENT_FUNDS", e.getMessage()));
            }
            if (e.getMessage().contains("greater than 0")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("WITHDRAWAL_FAILED", "Failed to process withdrawal: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred during withdrawal"));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) {
        try {
            // Additional validation
            if (request.getWalletId() == null || request.getWalletId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_SOURCE_WALLET", "Source wallet ID is required for transfer"));
            }

            if (request.getToWalletId() == null || request.getToWalletId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_DESTINATION_WALLET", "Destination wallet ID is required for transfer"));
            }

            if (!isValidWalletId(request.getWalletId())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_SOURCE_WALLET_FORMAT", "Invalid source wallet ID format"));
            }

            if (!isValidWalletId(request.getToWalletId())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_DESTINATION_WALLET_FORMAT", "Invalid destination wallet ID format"));
            }

            if (request.getWalletId().equals(request.getToWalletId())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("SAME_WALLET_TRANSFER", "Cannot transfer to the same wallet"));
            }

            if (request.getAmount() == null || request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", "Amount must be greater than 0"));
            }

            TransactionResponse response = transactionService.transfer(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                if (e.getMessage().contains(request.getWalletId())) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("SOURCE_WALLET_NOT_FOUND", "Source wallet not found: " + request.getWalletId()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("DESTINATION_WALLET_NOT_FOUND", "Destination wallet not found: " + request.getToWalletId()));
                }
            }
            if (e.getMessage().contains("Insufficient funds")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("INSUFFICIENT_FUNDS", e.getMessage()));
            }
            if (e.getMessage().contains("greater than 0")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_AMOUNT", e.getMessage()));
            }
            if (e.getMessage().contains("same wallet")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("SAME_WALLET_TRANSFER", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("TRANSFER_FAILED", "Failed to process transfer: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred during transfer"));
        }
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<?> getWalletTransactions(@PathVariable String walletId) {
        try {
            // Validate wallet ID
            if (walletId == null || walletId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_ID", "Wallet ID cannot be null or empty"));
            }

            if (!isValidWalletId(walletId)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_WALLET_FORMAT", "Invalid wallet ID format"));
            }

            List<TransactionResponse> responses = transactionService.getWalletTransactions(walletId);

            if (responses.isEmpty()) {
                return ResponseEntity.ok(new EmptyResponse("No transactions found for wallet: " + walletId));
            }

            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("WALLET_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("TRANSACTION_RETRIEVAL_FAILED", "Failed to retrieve transactions: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred while retrieving transactions"));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        try {
            // Validate transaction ID
            if (transactionId == null || transactionId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_TRANSACTION_ID", "Transaction ID cannot be null or empty"));
            }

            if (!transactionId.matches("^TXN[A-Z0-9]{13}$")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("INVALID_TRANSACTION_FORMAT", "Transaction ID must be in format TXN followed by 13 alphanumeric characters"));
            }

            TransactionResponse response = transactionService.getTransaction(transactionId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("TRANSACTION_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("TRANSACTION_RETRIEVAL_FAILED", "Failed to retrieve transaction: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred while retrieving transaction"));
        }
    }

    // Utility method to validate wallet ID format
    private boolean isValidWalletId(String walletId) {
        return walletId != null && walletId.matches("^WAL[A-Z0-9]{13}$");
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

    // Response classes for structured responses
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

    public static class EmptyResponse {
        private String message;
        private long timestamp;

        public EmptyResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}