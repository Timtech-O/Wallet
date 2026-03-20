package com.example.wallet.controller;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.WalletRequest;
import com.example.wallet.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/wallets")

public class WalletController {


    private final WalletService walletService;



    @PostMapping
    public ApiResponse<?> create() {
        return walletService.createWallet();
    }

    @PostMapping("/{id}/fund")
    public ApiResponse<?> fund(@PathVariable String id,
                               @RequestBody WalletRequest request) {
        return walletService.fund(id, request);
    }

    @PostMapping("/{id}/withdraw")
    public ApiResponse<?> withdraw(@PathVariable String id,
                                   @RequestBody WalletRequest request) {
        return walletService.withdraw(id, request);
    }

    @PostMapping("/{id}/transfer")
    public ApiResponse<?> transfer(@PathVariable String id,
                                   @RequestBody WalletRequest request) {
        return walletService.transfer(id, request);
    }

    @GetMapping("/{id}/balance")
    public ApiResponse<?> balance(@PathVariable String id) {
        return walletService.balance(id);
    }

    @GetMapping("/{id}/transactions")
    public ApiResponse<?> transactions(@PathVariable String id,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        return walletService.transactions(id, page, size);
    }

    @PostMapping("/reverse/{reference}")
    public ApiResponse<?> reverse(@PathVariable String reference) {
        return walletService.reverse(reference);
    }
}
