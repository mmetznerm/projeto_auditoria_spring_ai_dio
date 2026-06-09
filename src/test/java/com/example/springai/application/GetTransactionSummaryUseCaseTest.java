package com.example.springai.application;

import com.example.springai.domain.Category;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetTransactionSummaryUseCaseTest {

    @Test
    void should_groupTransactionsByCategoryAndCalculateTotals() {
        var repository = mock(TransactionRepository.class);
        var useCase = new GetTransactionSummaryUseCase(repository);

        when(repository.findAll()).thenReturn(List.of(
                new Transaction("Mercado", 3300, Category.GROCERIES, "maycon"),
                new Transaction("Padaria", 1190, Category.GROCERIES, "maycon"),
                new Transaction("Troca de oleo", 52540, Category.AUTO, "maycon")
        ));

        var summary = useCase.execute();

        assertThat(summary.transactionCount()).isEqualTo(3);
        assertThat(summary.totalAmount()).isEqualTo(57030.0);
        assertThat(summary.categories()).hasSize(2);
        assertThat(summary.categories())
                .extracting("category", "transactionCount", "totalAmount")
                .containsExactly(
                        tuple("AUTO", 1L, 52540.0),
                        tuple("GROCERIES", 2L, 4490.0)
                );
    }
}
