package com.example.springai.application.output;

public record CategorySummaryOutput(
        String category,
        long transactionCount,
        double totalAmount
) {
}
