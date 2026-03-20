package com.example.wallet.service;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.WalletRequest;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.Wallet;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.enums.TransactionType;
import com.example.wallet.exception.WalletException;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyService idempotencyService;

    @Override
    public ApiResponse<?> createWallet() {
        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID().toString())
                .balance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        return ApiResponse.builder()
                .success(true)
                .message("Wallet created")
                .data(wallet)
                .build();
    }

    @Transactional
    @Override
    public ApiResponse<?> fund(String walletId, WalletRequest request) {

        validateAmount(request.getAmount());

        // Idempotency check
        Optional<Transaction> existing = idempotencyService.check(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return success("Already processed", existing.get());
        }

        Wallet wallet = getWallet(walletId);

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction txn = saveTransaction(walletId, request, TransactionType.CREDIT);

        return success("Wallet funded", txn);
    }

    @Transactional
    @Override
    public ApiResponse<?> withdraw(String walletId, WalletRequest request) {

        Wallet wallet = getWallet(walletId);

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new WalletException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction txn = saveTransaction(walletId, request, TransactionType.DEBIT);

        return success("Withdraw successful", txn);
    }

    @Transactional
    @Override
    public ApiResponse<?> transfer(String fromWalletId, WalletRequest request) {

        Wallet sender = getWallet(fromWalletId);
        Wallet receiver = getWallet(request.getToWalletId());

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new WalletException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        walletRepository.save(sender);
        walletRepository.save(receiver);

        Transaction txn = saveTransaction(fromWalletId, request, TransactionType.TRANSFER);

        return success("Transfer successful", txn);
    }

    @Override
    public ApiResponse<?> balance(String walletId) {
        return success("Balance fetched", getWallet(walletId).getBalance());
    }

    @Override
    public ApiResponse<?> transactions(String walletId, int page, int size) {

        Page<Transaction> result = transactionRepository
                .findByWalletId(walletId, PageRequest.of(page, size));

        return success("Transactions fetched", result);
    }

    @Transactional
    @Override
    public ApiResponse<?> reverse(String reference) {

        Transaction txn = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new WalletException("Transaction not found"));

        if (txn.getStatus() == TransactionStatus.REVERSED) {
            throw new WalletException("Already reversed");
        }

        Wallet wallet = getWallet(txn.getWalletId());

        if (txn.getType() == TransactionType.CREDIT) {
            wallet.setBalance(wallet.getBalance().subtract(txn.getAmount()));
        } else {
            wallet.setBalance(wallet.getBalance().add(txn.getAmount()));
        }
        walletRepository.save(wallet);

        txn.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(txn);

        saveTransaction(wallet.getId(),
                new WalletRequest(txn.getAmount(), null, null),
                TransactionType.REVERSAL);

        return success("Transaction reversed", null);
    }


    private Wallet getWallet(String id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletException("Wallet not found"));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletException("Invalid amount");
        }
    }

    private Transaction saveTransaction(String walletId, WalletRequest request, TransactionType type) {

        Transaction txn = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .reference(UUID.randomUUID().toString())
                .walletId(walletId)
                .amount(request.getAmount())
                .type(type)
                .status(TransactionStatus.SUCCESS)
                .idempotencyKey(request.getIdempotencyKey())
                .createdAt(LocalDateTime.now())
                .build();

        return transactionRepository.save(txn);
    }

    private ApiResponse<?> success(String msg, Object data) {
        return ApiResponse.builder()
                .success(true)
                .message(msg)
                .data(data)
                .build();
    }
}
