package com.example.springai.application;

import com.example.springai.application.input.TransactionInput;
import com.example.springai.application.output.TransactionOutput;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUseCase {
    private static final String DEFAULT_CREATED_BY = "anonymous";

    private final TransactionRepository repository;
    private final AudioFileMetadataContext audioFileMetadataContext;

    public PersistTransactionUseCase(
            TransactionRepository repository,
            AudioFileMetadataContext audioFileMetadataContext
    ) {
        this.repository = repository;
        this.audioFileMetadataContext = audioFileMetadataContext;
    }

    @Tool(name = "persist-transaction", description = "Permite uma nova transacao financeira")
    public TransactionOutput execute(TransactionInput transactionInput) {
        var createdBy = transactionInput.createdBy() == null || transactionInput.createdBy().isBlank()
                ? DEFAULT_CREATED_BY
                : transactionInput.createdBy();

        var metadata = audioFileMetadataContext.get();

        var transaction = repository.save(metadata
                .map(audioFileMetadata -> new Transaction(
                        transactionInput.description(),
                        transactionInput.amount(),
                        transactionInput.category(),
                        createdBy,
                        audioFileMetadata.sourceType(),
                        audioFileMetadata.fileName(),
                        audioFileMetadata.contentType(),
                        audioFileMetadata.fileSize()
                ))
                .orElseGet(() -> new Transaction(
                        transactionInput.description(),
                        transactionInput.amount(),
                        transactionInput.category(),
                        createdBy
                )));

        return TransactionOutput.from(transaction);
    }
}
