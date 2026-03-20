package com.example.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletRequest {
    private BigDecimal amount;
    private String toWalletId;
    private String idempotencyKey;
}
