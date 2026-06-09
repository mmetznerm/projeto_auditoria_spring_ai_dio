package com.example.springai.infrastructure.persistence.repository;

import com.example.springai.domain.Category;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import com.example.springai.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAll() {
        var transactions = new ArrayList<Transaction>();
        transactionEntityRepository.findAll().forEach(entity -> transactions.add(entity.toDomain()));
        return transactions;
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository
                .findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to) {
        return transactionEntityRepository
                .findAllByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
