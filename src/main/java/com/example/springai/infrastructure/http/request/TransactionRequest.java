package com.example.springai.infrastructure.http.request;

import com.example.springai.application.input.TransactionInput;
import com.example.springai.domain.Category;

public record TransactionRequest(String description, Category category, long amount, String createdBy) {
    public TransactionInput toInput() {
        return new TransactionInput(
                description,
                amount,
                category,
                createdBy
        );
    }
}
