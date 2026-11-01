package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarServicoInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de novo serviço no catálogo da oficina")
public record CriarServicoRequest(
        @Schema(description = "Código identificador único do serviço", example = "SRV-ALINHAMENTO-01")
        @NotBlank(message = "O código do serviço é obrigatório")
        @Size(max = 50, message = "O código deve conter no máximo 50 caracteres")
        String codigo,

        @Schema(description = "Nome do serviço", example = "Alinhamento e Balanceamento 3D")
        @NotBlank(message = "O nome do serviço é obrigatório")
        @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres")
        String nome,

        @Schema(description = "Descrição técnica detalhada do serviço", example = "Alinhamento computadorizado das 4 rodas e balanceamento")
        @Size(max = 255, message = "A descrição deve conter no máximo 255 caracteres")
        String descricao,

        @Schema(description = "Valor unitário tabelado da mão de obra", example = "120.00")
        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario
) {
    public CadastrarServicoInput toInput() {
        return new CadastrarServicoInput(codigo, nome, descricao, valorUnitario);
    }
}

