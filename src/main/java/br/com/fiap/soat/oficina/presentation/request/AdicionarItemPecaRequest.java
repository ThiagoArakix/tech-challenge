package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemPecaInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dados para inclusão de peça na Ordem de Serviço")
public record AdicionarItemPecaRequest(
        @Schema(description = "ID da peça cadastrada no catálogo", example = "b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e")
        @NotNull(message = "O ID da peça é obrigatório")
        UUID pecaId,

        @Schema(description = "Quantidade de unidades da peça a aplicar", example = "2")
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 peça")
        Integer quantidade
) {
    public AdicionarItemPecaInput toInput() {
        return new AdicionarItemPecaInput(pecaId, quantidade);
    }
}

