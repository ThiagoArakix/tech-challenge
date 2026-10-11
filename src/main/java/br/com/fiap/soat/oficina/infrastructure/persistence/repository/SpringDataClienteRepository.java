package br.com.fiap.soat.oficina.infrastructure.persistence.repository;

import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataClienteRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    Optional<ClienteJpaEntity> findByDocumento(String documento);

    Optional<ClienteJpaEntity> findByEmail(String email);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);
}

