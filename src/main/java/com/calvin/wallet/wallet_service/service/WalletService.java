package com.calvin.wallet.wallet_service.service;

import com.calvin.wallet.wallet_service.dto.WalletRequest;
import com.calvin.wallet.wallet_service.dto.WalletResponse;
import com.calvin.wallet.wallet_service.model.Wallet;
import com.calvin.wallet.wallet_service.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public WalletResponse createWallet(WalletRequest request) {
        // Check if user already has a wallet
        if (walletRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("User already has a wallet");
        }

        String walletId = generateWalletId();
        Wallet wallet = new Wallet(walletId, request.getUserId());

        if (request.getCurrency() != null) {
            wallet.setCurrency(request.getCurrency());
        }

        Wallet savedWallet = walletRepository.save(wallet);

        return mapToWalletResponse(savedWallet);
    }

    public WalletResponse getWallet(String walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        return mapToWalletResponse(wallet);
    }

    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

        return mapToWalletResponse(wallet);
    }

    public BigDecimal getBalance(String walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        return wallet.getBalance();
    }

    @Transactional
    public void updateBalance(String walletId, BigDecimal amount, boolean isCredit) {
        Wallet wallet = walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));

        BigDecimal newBalance;
        if (isCredit) {
            newBalance = wallet.getBalance().add(amount);
        } else {
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds");
            }
            newBalance = wallet.getBalance().subtract(amount);
        }

        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(java.time.LocalDateTime.now());
        walletRepository.save(wallet);
    }

    private String generateWalletId() {
        return "WAL" + UUID.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase();
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getWalletId(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getCreatedAt()
        );
    }
}