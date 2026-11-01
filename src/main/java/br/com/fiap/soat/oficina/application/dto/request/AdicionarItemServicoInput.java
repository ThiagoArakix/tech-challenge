package br.com.fiap.soat.oficina.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionarItemServicoInput(
        UUID servicoId,
        String descricao,
        BigDecimal quantidade,
        BigDecimal valorUnitario
) {
    public AdicionarItemServicoInput(String descricao, BigDecimal quantidade, BigDecimal valorUnitario) {
        this(null, descricao, quantidade, valorUnitario);
    }
}
