package br.com.fiap.soat.oficina.infrastructure.persistence.repository;

import br.com.fiap.soat.oficina.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataOrdemServicoRepository extends JpaRepository<OrdemServicoJpaEntity, UUID> {

    List<OrdemServicoJpaEntity> findByClienteId(UUID clienteId);

    List<OrdemServicoJpaEntity> findByVeiculoId(UUID veiculoId);

    List<OrdemServicoJpaEntity> findByStatus(String status);
}

