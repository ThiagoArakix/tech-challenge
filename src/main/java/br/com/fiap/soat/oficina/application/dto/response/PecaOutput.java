package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.Peca;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PecaOutput(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorUnitario,
        int saldoEstoque,
        int estoqueMinimo,
        boolean estoqueAbaixoDoMinimo,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static PecaOutput deDominio(Peca peca) {
        if (peca == null) return null;
        return new PecaOutput(
                peca.getId(),
                peca.getCodigo(),
                peca.getNome(),
                peca.getDescricao(),
                peca.getValorUnitario().valor(),
                peca.getSaldoEstoque(),
                peca.getEstoqueMinimo(),
                peca.isEstoqueAbaixoDoMinimo(),
                peca.getCriadoEm(),
                peca.getAtualizadoEm()
        );
    }
}

