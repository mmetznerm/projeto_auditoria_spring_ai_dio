package com.example.springai.application.input;

import com.example.springai.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

public record TransactionInput(
        @ToolParam(description = "Descricao do gasto") String description,
        @ToolParam(description = "Valor do gasto em centavos") long amount,
        @ToolParam(description = "Categoria de uma transacao") Category category,
        @ToolParam(description = "Usuario responsavel pelo registro da transacao") String createdBy
) {
}
