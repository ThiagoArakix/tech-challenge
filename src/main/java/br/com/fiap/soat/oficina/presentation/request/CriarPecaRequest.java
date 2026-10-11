package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarPecaInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de nova peça no catálogo")
public record CriarPecaRequest(
        @Schema(description = "Código identificador único da peça", example = "AMORT-01")
        @NotBlank(message = "O código da peça é obrigatório")
        String codigo,

        @Schema(description = "Nome comercial da peça", example = "Amortecedor Dianteiro")
        @NotBlank(message = "O nome da peça é obrigatório")
        String nome,

        @Schema(description = "Descrição técnica detalhada", example = "Amortecedor pressurizado a gás")
        String descricao,

        @Schema(description = "Valor unitário de venda", example = "350.00")
        @NotNull(message = "O valor unitário é obrigatório")
        @DecimalMin(value = "0.0", message = "O valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @Schema(description = "Saldo inicial disponível em estoque", example = "10")
        @NotNull(message = "O saldo inicial em estoque é obrigatório")
        @Min(value = 0, message = "O saldo inicial não pode ser negativo")
        Integer saldoEstoque,

        @Schema(description = "Quantidade de estoque mínimo para alerta de reposição", example = "2")
        @NotNull(message = "O estoque mínimo é obrigatório")
        @Min(value = 0, message = "O estoque mínimo não pode ser negativo")
        Integer estoqueMinimo
) {
    public CadastrarPecaInput toInput() {
        return new CadastrarPecaInput(codigo, nome, descricao, valorUnitario, saldoEstoque, estoqueMinimo);
    }
}

