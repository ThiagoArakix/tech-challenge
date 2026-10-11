package br.com.fiap.soat.oficina.application.dto.request;

import java.math.BigDecimal;

public record CadastrarPecaInput(
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorUnitario,
        int saldoEstoque,
        int estoqueMinimo
) {
}

