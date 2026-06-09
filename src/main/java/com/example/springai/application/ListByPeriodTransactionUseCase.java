package com.example.springai.application;

import com.example.springai.application.output.TransactionOutput;
import com.example.springai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ListByPeriodTransactionUseCase {
    private final TransactionRepository repository;

    public ListByPeriodTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "list-transactions-by-period", description = "Lista transacoes financeiras registradas entre duas datas")
    public List<TransactionOutput> execute(
            @ToolParam(description = "Data inicial no formato yyyy-MM-dd") LocalDate from,
            @ToolParam(description = "Data final no formato yyyy-MM-dd") LocalDate to
    ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Data inicial nao pode ser posterior a data final");
        }

        var startDate = from.atStartOfDay();
        var endDate = to.atTime(LocalTime.MAX);

        return repository
                .findAllByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
