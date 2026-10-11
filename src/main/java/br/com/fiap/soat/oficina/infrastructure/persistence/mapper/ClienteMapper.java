package br.com.fiap.soat.oficina.infrastructure.persistence.mapper;

import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ClienteJpaEntity;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(ClienteJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Cliente(
                entity.getId(),
                entity.getNome(),
                CpfCnpj.de(entity.getDocumento()),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static ClienteJpaEntity toEntity(Cliente domain) {
        if (domain == null) {
            return null;
        }

        return new ClienteJpaEntity(
                domain.getId(),
                domain.getNome(),
                domain.getDocumento().valor(),
                domain.getEmail(),
                domain.getTelefone(),
                domain.getCriadoEm(),
                domain.getAtualizadoEm()
        );
    }
}

