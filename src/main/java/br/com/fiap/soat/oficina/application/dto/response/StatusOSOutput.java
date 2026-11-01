package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StatusOSOutput(
        UUID ordemServicoId,
        StatusOS status,
        String descricaoProblema,
        String diagnostico,
        List<ItemServicoOutput> itensServico,
        List<ItemPecaOutput> itensPeca,
        BigDecimal valorTotalServicos,
        BigDecimal valorTotalPecas,
        BigDecimal valorTotal,
        Instant dataAbertura,
        Instant dataAprovacao,
        Instant dataFinalizacao,
        Instant dataEntrega
) {
    public static StatusOSOutput deDominio(OrdemServico os) {
        if (os == null) return null;

        List<ItemServicoOutput> servicos = os.getItensServico().stream()
                .map(ItemServicoOutput::deDominio)
                .toList();

        List<ItemPecaOutput> pecas = os.getItensPeca().stream()
                .map(ItemPecaOutput::deDominio)
                .toList();

        return new StatusOSOutput(
                os.getId(),
                os.getStatus(),
                os.getDescricaoProblema(),
                os.getDiagnostico(),
                servicos,
                pecas,
                os.getValorTotalServicos().valor(),
                os.getValorTotalPecas().valor(),
                os.getValorTotal().valor(),
                os.getDataAbertura(),
                os.getDataAprovacao(),
                os.getDataFinalizacao(),
                os.getDataEntrega()
        );
    }
}
