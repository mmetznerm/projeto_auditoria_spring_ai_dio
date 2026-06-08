package com.example.springai.infrastructure.http.response;

import com.example.springai.application.output.TransactionOutput;

public record TransactionResponse(String id, String category, String description, double amount) {
    public static TransactionResponse from(TransactionOutput transaction) {
        return new TransactionResponse(
            transaction.id(),
                transaction.category(),
                transaction.description(),
                transaction.amount()
        );
    }
}
