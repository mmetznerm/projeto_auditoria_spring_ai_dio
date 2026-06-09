package com.example.springai.application;

import com.example.springai.application.output.CategorySummaryOutput;
import com.example.springai.application.output.TransactionSummaryOutput;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class GetTransactionSummaryUseCase {
    private final TransactionRepository repository;

    public GetTransactionSummaryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get-transaction-summary", description = "Retorna o resumo financeiro das transacoes por categoria")
    public TransactionSummaryOutput execute() {
        var transactions = repository.findAll();
        var totalAmount = transactions.stream().mapToLong(Transaction::getAmount).sum();

        var categories = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory))
                .entrySet()
                .stream()
                .map(entry -> new CategorySummaryOutput(
                        entry.getKey().name(),
                        entry.getValue().size(),
                        toDecimalAmount(entry.getValue().stream().mapToLong(Transaction::getAmount).sum())
                ))
                .sorted(Comparator.comparing(CategorySummaryOutput::category))
                .toList();

        return new TransactionSummaryOutput(
                transactions.size(),
                toDecimalAmount(totalAmount),
                categories
        );
    }

    private double toDecimalAmount(long amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
