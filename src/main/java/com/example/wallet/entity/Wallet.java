package com.example.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    private String id;

    @Version
    private Long version;

    @Column(nullable = false)
    private BigDecimal balance;

    private LocalDateTime createdAt;

}