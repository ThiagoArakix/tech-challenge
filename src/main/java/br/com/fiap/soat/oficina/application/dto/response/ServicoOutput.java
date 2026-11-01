package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.Servico;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServicoOutput(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorUnitario,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ServicoOutput deDominio(Servico servico) {
        if (servico == null) return null;
        return new ServicoOutput(
                servico.getId(),
                servico.getCodigo(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getValorUnitario().valor(),
                servico.getCriadoEm(),
                servico.getAtualizadoEm()
        );
    }
}

