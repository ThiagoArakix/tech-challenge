package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para atualização cadastral de serviço do catálogo")
public record AtualizarServicoRequest(
        @Schema(description = "Novo nome do serviço", example = "Alinhamento e Balanceamento 3D Completo")
        @NotBlank(message = "O nome do serviço é obrigatório")
        @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Nova descrição técnica do serviço", example = "Alinhamento computadorizado e balanceamento das 4 rodas")
        @Size(max = 255, message = "A descrição deve conter no máximo 255 caracteres")
        String descricao,

        @Schema(description = "Novo valor unitário tabelado", example = "140.00")
        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario
) {
}

