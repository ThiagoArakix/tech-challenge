package br.com.fiap.soat.oficina.application.dto.request;

import java.math.BigDecimal;

public record AdicionarItemServicoInput(
        String descricao,
        BigDecimal quantidade,
        BigDecimal valorUnitario
) {
}

