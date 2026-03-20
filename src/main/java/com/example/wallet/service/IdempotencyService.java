package com.example.wallet.service;

import com.example.wallet.entity.Transaction;
import com.example.wallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    @Autowired
    private final TransactionRepository transactionRepository;



    public Optional<Transaction> check(String key) {
        if (key == null) return Optional.empty();
        return transactionRepository.findByIdempotencyKey(key);
    }
}
