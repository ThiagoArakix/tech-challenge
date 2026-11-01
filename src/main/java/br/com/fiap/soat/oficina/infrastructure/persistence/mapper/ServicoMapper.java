package br.com.fiap.soat.oficina.infrastructure.persistence.mapper;

import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ServicoJpaEntity;

public final class ServicoMapper {

    private ServicoMapper() {
    }

    public static Servico toDomain(ServicoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Servico(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getDescricao(),
                Dinheiro.de(entity.getValorUnitario()),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static ServicoJpaEntity toEntity(Servico domain) {
        if (domain == null) {
            return null;
        }

        return new ServicoJpaEntity(
                domain.getId(),
                domain.getCodigo(),
                domain.getNome(),
                domain.getDescricao(),
                domain.getValorUnitario().valor(),
                domain.getCriadoEm(),
                domain.getAtualizadoEm()
        );
    }
}

