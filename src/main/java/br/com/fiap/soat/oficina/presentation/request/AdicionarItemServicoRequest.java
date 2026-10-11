package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para inclusão de serviço (mão de obra) na Ordem de Serviço")
public record AdicionarItemServicoRequest(
        @Schema(description = "Descrição detalhada do serviço prestado", example = "Substituição de pastilhas de freio dianteiras")
        @NotBlank(message = "A descrição do serviço é obrigatória")
        @Size(max = 200, message = "A descrição do serviço deve conter no máximo 200 caracteres")
        String descricao,

        @Schema(description = "Quantidade de horas ou execuções do serviço", example = "1.50")
        @NotNull(message = "A quantidade é obrigatória")
        @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @Schema(description = "Valor unitário do serviço", example = "150.00")
        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario
) {
    public AdicionarItemServicoInput toInput() {
        return new AdicionarItemServicoInput(descricao, quantidade, valorUnitario);
    }
}

