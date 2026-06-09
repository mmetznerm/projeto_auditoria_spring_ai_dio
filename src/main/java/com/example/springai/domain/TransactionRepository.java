package com.example.springai.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
    List<Transaction> findAllByCategory(Category category);
    List<Transaction> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
