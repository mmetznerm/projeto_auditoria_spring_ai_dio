package com.example.springai.infrastructure.http.response;

import com.example.springai.application.output.CategorySummaryOutput;

public record CategorySummaryResponse(
        String category,
        long transactionCount,
        double totalAmount
) {
    public static CategorySummaryResponse from(CategorySummaryOutput output) {
        return new CategorySummaryResponse(
                output.category(),
                output.transactionCount(),
                output.totalAmount()
        );
    }
}
