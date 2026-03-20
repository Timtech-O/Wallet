package com.example.wallet.service;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.WalletRequest;

public interface WalletService {

    ApiResponse<?> createWallet();

    ApiResponse<?> fund(String walletId, WalletRequest request);

    ApiResponse<?> withdraw(String walletId, WalletRequest request);

    ApiResponse<?> transfer(String walletId, WalletRequest request);

    ApiResponse<?> balance(String walletId);

    ApiResponse<?> transactions(String walletId, int page, int size);

    ApiResponse<?> reverse(String reference);
}
