package com.example.springai.application.input;

import com.example.springai.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record TransactionInput(
        @ToolParam(description = "Descrição do gasto") String description,
        @ToolParam(description = "Valor do gasto em centavos") long amount,
        @ToolParam(description = "Categoria de uma transação") Category category
) {
}
