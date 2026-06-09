package com.example.springai.application.output;

import java.util.List;

public record TransactionSummaryOutput(
        long transactionCount,
        double totalAmount,
        List<CategorySummaryOutput> categories
) {
}
