package com.example.springai.infrastructure.http.response;

import com.example.springai.application.output.TransactionSummaryOutput;

import java.util.List;

public record TransactionSummaryResponse(
        long transactionCount,
        double totalAmount,
        List<CategorySummaryResponse> categories
) {
    public static TransactionSummaryResponse from(TransactionSummaryOutput output) {
        return new TransactionSummaryResponse(
                output.transactionCount(),
                output.totalAmount(),
                output.categories().stream().map(CategorySummaryResponse::from).toList()
        );
    }
}
