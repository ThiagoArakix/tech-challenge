package br.com.fiap.soat.oficina.infrastructure.persistence.mapper;

import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.VeiculoJpaEntity;

public final class VeiculoMapper {

    private VeiculoMapper() {
    }

    public static Veiculo toDomain(VeiculoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Veiculo(
                entity.getId(),
                entity.getClienteId(),
                Placa.de(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAnoFabricacao(),
                entity.getAnoModelo(),
                entity.getCor(),
                entity.getQuilometragem(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static VeiculoJpaEntity toEntity(Veiculo domain) {
        if (domain == null) {
            return null;
        }

        return new VeiculoJpaEntity(
                domain.getId(),
                domain.getClienteId(),
                domain.getPlaca().valor(),
                domain.getMarca(),
                domain.getModelo(),
                domain.getAnoFabricacao(),
                domain.getAnoModelo(),
                domain.getCor(),
                domain.getQuilometragem(),
                domain.getCriadoEm(),
                domain.getAtualizadoEm()
        );
    }
}

