package com.calvin.wallet.wallet_service.service;

import com.calvin.wallet.wallet_service.dto.TransactionRequest;
import com.calvin.wallet.wallet_service.dto.TransactionResponse;
import com.calvin.wallet.wallet_service.model.Transaction;
import com.calvin.wallet.wallet_service.model.TransactionType;
import com.calvin.wallet.wallet_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    public TransactionResponse deposit(TransactionRequest request) {
        validateAmount(request.getAmount());

        // Update wallet balance
        walletService.updateBalance(request.getWalletId(), request.getAmount(), true);

        // Create transaction record
        Transaction transaction = new Transaction(
                generateTransactionId(),
                request.getWalletId(),
                request.getAmount(),
                TransactionType.DEPOSIT
        );

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        } else {
            transaction.setDescription("Deposit to wallet");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    public TransactionResponse withdraw(TransactionRequest request) {
        validateAmount(request.getAmount());

        // Update wallet balance
        walletService.updateBalance(request.getWalletId(), request.getAmount(), false);

        // Create transaction record
        Transaction transaction = new Transaction(
                generateTransactionId(),
                request.getWalletId(),
                request.getAmount(),
                TransactionType.WITHDRAWAL
        );

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        } else {
            transaction.setDescription("Withdrawal from wallet");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    public TransactionResponse transfer(TransactionRequest request) {
        validateAmount(request.getAmount());

        if (request.getToWalletId() == null || request.getToWalletId().isEmpty()) {
            throw new RuntimeException("Destination wallet ID is required for transfer");
        }

        if (request.getWalletId().equals(request.getToWalletId())) {
            throw new RuntimeException("Cannot transfer to the same wallet");
        }

        // Check if destination wallet exists
        walletService.getWallet(request.getToWalletId());

        // Withdraw from source wallet
        walletService.updateBalance(request.getWalletId(), request.getAmount(), false);

        // Deposit to destination wallet
        walletService.updateBalance(request.getToWalletId(), request.getAmount(), true);

        // Create transaction record
        Transaction transaction = new Transaction(
                generateTransactionId(),
                request.getWalletId(),
                request.getAmount(),
                TransactionType.TRANSFER
        );

        transaction.setToWalletId(request.getToWalletId());

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        } else {
            transaction.setDescription(String.format("Transfer to wallet %s", request.getToWalletId()));
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    public List<TransactionResponse> getWalletTransactions(String walletId) {
        List<Transaction> transactions = transactionRepository.findByFromWalletId(walletId);
        transactions.addAll(transactionRepository.findByToWalletId(walletId));

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        return mapToTransactionResponse(transaction);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getFromWalletId(),
                transaction.getToWalletId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp(),
                transaction.getDescription(),
                transaction.getStatus()
        );
    }
}