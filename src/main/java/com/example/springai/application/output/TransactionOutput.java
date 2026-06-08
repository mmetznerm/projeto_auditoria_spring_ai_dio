package com.example.springai.application.output;

import com.example.springai.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public record TransactionOutput(
        String id,
        String description,
        String category,
        double amount,
        LocalDateTime createdAt,
        String createdBy
) {
    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().id().toString(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                BigDecimal.valueOf(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                transaction.getCreatedAt(),
                transaction.getCreatedBy()
        );
    }
}
