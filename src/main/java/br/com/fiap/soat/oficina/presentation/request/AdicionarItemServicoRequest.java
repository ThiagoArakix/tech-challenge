package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para inclusão de serviço (mão de obra) na Ordem de Serviço (via catálogo servicoId ou descrição/valor avulsos)")
public record AdicionarItemServicoRequest(
        @Schema(description = "ID opcional do serviço no catálogo (preenche descrição e valor automaticamente se omitidos)", example = "b2c3d4e5-f6a7-8901-2345-6789abcdef01")
        UUID servicoId,

        @Schema(description = "Descrição detalhada do serviço prestado (obrigatória se servicoId não for informado)", example = "Substituição de pastilhas de freio dianteiras")
        @Size(max = 200, message = "A descrição do serviço deve conter no máximo 200 caracteres")
        String descricao,

        @Schema(description = "Quantidade de horas ou execuções do serviço", example = "1.50")
        @NotNull(message = "A quantidade é obrigatória")
        @DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @Schema(description = "Valor unitário do serviço (utiliza o valor do catálogo se servicoId for informado e este campo for omitido)", example = "150.00")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario
) {
    public AdicionarItemServicoRequest(String descricao, BigDecimal quantidade, BigDecimal valorUnitario) {
        this(null, descricao, quantidade, valorUnitario);
    }

    public AdicionarItemServicoInput toInput() {
        return new AdicionarItemServicoInput(servicoId, descricao, quantidade, valorUnitario);
    }
}
