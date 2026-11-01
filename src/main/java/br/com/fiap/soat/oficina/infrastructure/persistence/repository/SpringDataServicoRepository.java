package br.com.fiap.soat.oficina.infrastructure.persistence.repository;

import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataServicoRepository extends JpaRepository<ServicoJpaEntity, UUID> {

    Optional<ServicoJpaEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}

