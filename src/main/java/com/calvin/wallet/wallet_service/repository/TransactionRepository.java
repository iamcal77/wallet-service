package com.calvin.wallet.wallet_service.repository;

import com.calvin.wallet.wallet_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findByFromWalletId(String walletId);
    List<Transaction> findByToWalletId(String walletId);
}