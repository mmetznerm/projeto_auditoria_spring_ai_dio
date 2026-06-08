package com.example.springai.infrastructure.http.response;

import com.example.springai.application.output.TransactionOutput;

import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String category,
        String description,
        double amount,
        LocalDateTime createdAt,
        String createdBy
) {
    public static TransactionResponse from(TransactionOutput transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.category(),
                transaction.description(),
                transaction.amount(),
                transaction.createdAt(),
                transaction.createdBy()
        );
    }
}
