package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para movimentação de saldo de estoque de peça")
public record AjusteEstoqueRequest(
        @Schema(description = "Quantidade de itens para movimentação (débito ou reposição)", example = "5")
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item")
        Integer quantidade
) {
}

