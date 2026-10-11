package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ItemPecaOutput(
        UUID id,
        UUID pecaId,
        int quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        Instant criadoEm
) {
    public static ItemPecaOutput deDominio(ItemPecaOrdem item) {
        if (item == null) return null;
        return new ItemPecaOutput(
                item.getId(),
                item.getPecaId(),
                item.getQuantidade(),
                item.getValorUnitario().valor(),
                item.getValorTotal().valor(),
                item.getCriadoEm()
        );
    }
}

