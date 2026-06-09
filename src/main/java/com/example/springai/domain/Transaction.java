package com.example.springai.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private LocalDateTime createdAt;
    private String createdBy;
    private String sourceType;
    private String sourceFileName;
    private String sourceContentType;
    private Long sourceFileSize;

    public Transaction(String description, long amount, Category category, String createdBy) {
        this(description, amount, category, createdBy, null, null, null, null);
    }

    public Transaction(
            String description,
            long amount,
            Category category,
            String createdBy,
            String sourceType,
            String sourceFileName,
            String sourceContentType,
            Long sourceFileSize
    ) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.sourceType = sourceType;
        this.sourceFileName = sourceFileName;
        this.sourceContentType = sourceContentType;
        this.sourceFileSize = sourceFileSize;
    }
}
