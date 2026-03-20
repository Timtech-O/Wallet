package com.example.wallet;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.WalletRequest;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.WalletException;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WalletApplicationTests {

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCreateWallet() {
        ApiResponse<?> response = walletService.createWallet();

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    void shouldFundWallet() {
        ApiResponse<?> walletRes = walletService.createWallet();
        Wallet wallet = (Wallet) walletRes.getData();

        WalletRequest request = new WalletRequest();
        request.setAmount(new BigDecimal("1000"));

        ApiResponse<?> response = walletService.fund(wallet.getId(), request);

        assertTrue(response.isSuccess());
    }

    @Test
    void shouldFailWhenInsufficientBalance() {

        ApiResponse<?> walletRes = walletService.createWallet();
        Wallet wallet = (Wallet) walletRes.getData();

        WalletRequest request = new WalletRequest();
        request.setAmount(new BigDecimal("500"));

        assertThrows(WalletException.class, () -> {
            walletService.withdraw(wallet.getId(), request);
        });
    }
}