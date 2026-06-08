package com.example.springai.application;

import com.example.springai.application.input.TransactionInput;
import com.example.springai.application.output.TransactionOutput;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
        private final TransactionRepository repository;

    public PersistTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "persist-transaction", description = "Permite uma nova transação financeira")
    public TransactionOutput execute(TransactionInput transactionInput) {
        var transaction = repository.save(
                new Transaction(transactionInput.description(), transactionInput.amount(), transactionInput.category()));

        return TransactionOutput.from(transaction);
    }
}
