package com.calvin.wallet.wallet_service.repository;

import com.calvin.wallet.wallet_service.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByWalletId(String walletId);
    Optional<Wallet> findByUserId(Long userId);
    boolean existsByWalletId(String walletId);
    boolean existsByUserId(Long userId);
}