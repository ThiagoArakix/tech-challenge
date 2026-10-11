package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemServicoOutput(
        UUID id,
        String descricao,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        Instant criadoEm
) {
    public static ItemServicoOutput deDominio(ItemServicoOrdem item) {
        if (item == null) return null;
        return new ItemServicoOutput(
                item.getId(),
                item.getDescricao(),
                item.getQuantidade(),
                item.getValorUnitario().valor(),
                item.getValorTotal().valor(),
                item.getCriadoEm()
        );
    }
}

