package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para atualização cadastral de peça no catálogo")
public record AtualizarPecaRequest(
        @Schema(description = "Nome da peça", example = "Pastilha de Freio Cerâmica")
        @NotBlank(message = "O nome da peça é obrigatório")
        @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Descrição técnica da peça", example = "Jogo de pastilhas dianteiras de alta durabilidade")
        @Size(max = 255, message = "A descrição deve conter no máximo 255 caracteres")
        String descricao,

        @Schema(description = "Valor unitário de venda da peça", example = "185.00")
        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @Schema(description = "Estoque mínimo de segurança para alerta de reposição", example = "5")
        @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
        int estoqueMinimo
) {
}

