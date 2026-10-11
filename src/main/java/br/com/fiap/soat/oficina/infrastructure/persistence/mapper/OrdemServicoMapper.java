package br.com.fiap.soat.oficina.infrastructure.persistence.mapper;

import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;
import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ItemPecaJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ItemServicoJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.OrdemServicoJpaEntity;

import java.util.ArrayList;
import java.util.List;

public final class OrdemServicoMapper {

    private OrdemServicoMapper() {
    }

    public static OrdemServico toDomain(OrdemServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        List<ItemServicoOrdem> itensServico = (entity.getItensServico() != null)
                ? entity.getItensServico().stream()
                .map(item -> new ItemServicoOrdem(
                        item.getId(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        Dinheiro.de(item.getValorUnitario()),
                        item.getCriadoEm()))
                .toList()
                : new ArrayList<>();

        List<ItemPecaOrdem> itensPeca = (entity.getItensPeca() != null)
                ? entity.getItensPeca().stream()
                .map(item -> new ItemPecaOrdem(
                        item.getId(),
                        item.getPecaId(),
                        item.getQuantidade(),
                        Dinheiro.de(item.getValorUnitario()),
                        item.getCriadoEm()))
                .toList()
                : new ArrayList<>();

        StatusOS status = StatusOS.valueOf(entity.getStatus());

        return new OrdemServico(
                entity.getId(),
                entity.getClienteId(),
                entity.getVeiculoId(),
                status,
                entity.getDescricaoProblema(),
                entity.getDiagnostico(),
                itensServico,
                itensPeca,
                entity.getDataAbertura(),
                entity.getDataAprovacao(),
                entity.getDataFinalizacao(),
                entity.getDataEntrega(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static OrdemServicoJpaEntity toEntity(OrdemServico domain) {
        if (domain == null) {
            return null;
        }

        OrdemServicoJpaEntity entity = new OrdemServicoJpaEntity(
                domain.getId(),
                domain.getClienteId(),
                domain.getVeiculoId(),
                domain.getStatus().name(),
                domain.getDescricaoProblema(),
                domain.getDiagnostico(),
                domain.getValorTotal().valor(),
                domain.getDataAbertura(),
                domain.getDataAprovacao(),
                domain.getDataFinalizacao(),
                domain.getDataEntrega(),
                domain.getCriadoEm(),
                domain.getAtualizadoEm()
        );

        if (domain.getItensServico() != null) {
            for (ItemServicoOrdem itemServico : domain.getItensServico()) {
                ItemServicoJpaEntity itemEntity = new ItemServicoJpaEntity(
                        itemServico.getId(),
                        entity,
                        itemServico.getDescricao(),
                        itemServico.getQuantidade(),
                        itemServico.getValorUnitario().valor(),
                        itemServico.getValorTotal().valor(),
                        itemServico.getCriadoEm()
                );
                entity.adicionarItemServico(itemEntity);
            }
        }

        if (domain.getItensPeca() != null) {
            for (ItemPecaOrdem itemPeca : domain.getItensPeca()) {
                ItemPecaJpaEntity itemEntity = new ItemPecaJpaEntity(
                        itemPeca.getId(),
                        entity,
                        itemPeca.getPecaId(),
                        itemPeca.getQuantidade(),
                        itemPeca.getValorUnitario().valor(),
                        itemPeca.getValorTotal().valor(),
                        itemPeca.getCriadoEm()
                );
                entity.adicionarItemPeca(itemEntity);
            }
        }

        return entity;
    }
}

