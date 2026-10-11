package br.com.fiap.soat.oficina.infrastructure.persistence.mapper;

import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.PecaJpaEntity;

public final class PecaMapper {

    private PecaMapper() {
    }

    public static Peca toDomain(PecaJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Peca(
                entity.getId(),
                entity.getCodigo(),
                entity.getNome(),
                entity.getDescricao(),
                Dinheiro.de(entity.getValorUnitario()),
                entity.getSaldoEstoque(),
                entity.getEstoqueMinimo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static PecaJpaEntity toEntity(Peca domain) {
        if (domain == null) {
            return null;
        }

        return new PecaJpaEntity(
                domain.getId(),
                domain.getCodigo(),
                domain.getNome(),
                domain.getDescricao(),
                domain.getValorUnitario().valor(),
                domain.getSaldoEstoque(),
                domain.getEstoqueMinimo(),
                domain.getCriadoEm(),
                domain.getAtualizadoEm()
        );
    }
}

