package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.CriarOrdemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Dados para abertura de nova Ordem de Serviço")
public record CriarOrdemServicoRequest(
        @Schema(description = "ID do cliente proprietário", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @NotNull(message = "O ID do cliente é obrigatório")
        UUID clienteId,

        @Schema(description = "ID do veículo associado", example = "f1e2d3c4-b5a6-7890-1234-56789abcdef0")
        @NotNull(message = "O ID do veículo é obrigatório")
        UUID veiculoId,

        @Schema(description = "Descrição detalhada do problema relatado pelo cliente", example = "Barulho metálico na suspensão dianteira ao passar por desníveis")
        @NotBlank(message = "A descrição do problema relatado é obrigatória")
        @Size(max = 500, message = "A descrição deve conter no máximo 500 caracteres")
        String descricaoProblema
) {
    public CriarOrdemServicoInput toInput() {
        return new CriarOrdemServicoInput(clienteId, veiculoId, descricaoProblema);
    }
}

