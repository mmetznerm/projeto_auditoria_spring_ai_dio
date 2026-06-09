package com.example.springai.application;

import com.example.springai.application.input.TransactionInput;
import com.example.springai.domain.Category;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistTransactionUseCaseTest {

    @Mock
    TransactionRepository repository;

    @Test
    void should_persistTransactionWithDefaultCreatedBy_when_createdByIsBlank() {
        var metadataContext = new AudioFileMetadataContext();
        var useCase = new PersistTransactionUseCase(repository, metadataContext);
        var savedTransaction = ArgumentCaptor.forClass(Transaction.class);

        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var output = useCase.execute(new TransactionInput("Compra no mercado", 3300, Category.GROCERIES, " "));

        assertThat(output.createdBy()).isEqualTo("anonymous");
        assertThat(output.sourceFileName()).isNull();
        org.mockito.Mockito.verify(repository).save(savedTransaction.capture());
        assertThat(savedTransaction.getValue().getCreatedBy()).isEqualTo("anonymous");
    }

    @Test
    void should_persistTransactionWithAudioMetadata_when_contextHasMetadata() {
        var metadataContext = new AudioFileMetadataContext();
        metadataContext.set(new AudioFileMetadata("AUDIO_UPLOAD", "audio.wav", "audio/wav", 1024L));
        var useCase = new PersistTransactionUseCase(repository, metadataContext);

        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var output = useCase.execute(new TransactionInput("Troca de oleo", 52540, Category.AUTO, "maycon"));

        assertThat(output.createdBy()).isEqualTo("maycon");
        assertThat(output.sourceType()).isEqualTo("AUDIO_UPLOAD");
        assertThat(output.sourceFileName()).isEqualTo("audio.wav");
        assertThat(output.sourceContentType()).isEqualTo("audio/wav");
        assertThat(output.sourceFileSize()).isEqualTo(1024L);
    }
}
