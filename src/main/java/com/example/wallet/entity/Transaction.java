package com.example.wallet.entity;

import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = @Index(columnList = "reference", unique = true))
public class Transaction {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String reference;

    private String walletId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String idempotencyKey;

    private LocalDateTime createdAt;
}
