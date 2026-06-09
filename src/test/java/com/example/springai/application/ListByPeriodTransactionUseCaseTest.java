package com.example.springai.application;

import com.example.springai.domain.Category;
import com.example.springai.domain.Transaction;
import com.example.springai.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListByPeriodTransactionUseCaseTest {

    @Test
    void should_listTransactionsBetweenStartAndEndOfSelectedDates() {
        var repository = mock(TransactionRepository.class);
        var useCase = new ListByPeriodTransactionUseCase(repository);
        var fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        var toCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        var from = LocalDate.of(2026, 6, 1);
        var to = LocalDate.of(2026, 6, 9);

        when(repository.findAllByCreatedAtBetween(from.atStartOfDay(), to.atTime(LocalTime.MAX)))
                .thenReturn(List.of(new Transaction("Mercado", 3300, Category.GROCERIES, "maycon")));

        var output = useCase.execute(from, to);

        assertThat(output).hasSize(1);
        assertThat(output.getFirst().description()).isEqualTo("Mercado");
        verify(repository).findAllByCreatedAtBetween(fromCaptor.capture(), toCaptor.capture());
        assertThat(fromCaptor.getValue()).isEqualTo(LocalDateTime.of(2026, 6, 1, 0, 0));
        assertThat(toCaptor.getValue()).isEqualTo(to.atTime(LocalTime.MAX));
    }

    @Test
    void should_rejectPeriod_when_fromIsAfterTo() {
        var repository = mock(TransactionRepository.class);
        var useCase = new ListByPeriodTransactionUseCase(repository);

        assertThatThrownBy(() -> useCase.execute(LocalDate.of(2026, 6, 9), LocalDate.of(2026, 6, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data inicial nao pode ser posterior a data final");
    }
}
