package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StatusOSOutput(
        UUID ordemServicoId,
        StatusOS status,
        String descricaoProblema,
        String diagnostico,
        Instant dataAbertura,
        Instant dataAprovacao,
        Instant dataFinalizacao,
        Instant dataEntrega,
        BigDecimal valorTotal
) {
    public static StatusOSOutput deDominio(OrdemServico os) {
        if (os == null) return null;
        return new StatusOSOutput(
                os.getId(),
                os.getStatus(),
                os.getDescricaoProblema(),
                os.getDiagnostico(),
                os.getDataAbertura(),
                os.getDataAprovacao(),
                os.getDataFinalizacao(),
                os.getDataEntrega(),
                os.getValorTotal().valor()
        );
    }
}

