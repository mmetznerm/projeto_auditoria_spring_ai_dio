package com.example.springai.application;

import com.example.springai.application.output.TransactionOutput;
import com.example.springai.domain.Category;
import com.example.springai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListByCategoryTransactionUseCase {
        private final TransactionRepository repository;

    public ListByCategoryTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "list-transaction-by-category", description = "Lista transações financeiras por categoria")
    public List<TransactionOutput> execute(@ToolParam(description = "Categoria de uma transação") Category category) {
        return repository
                .findAllByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
