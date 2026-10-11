package br.com.fiap.soat.oficina.infrastructure.persistence.repository;

import br.com.fiap.soat.oficina.infrastructure.persistence.entity.VeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataVeiculoRepository extends JpaRepository<VeiculoJpaEntity, UUID> {

    Optional<VeiculoJpaEntity> findByPlaca(String placa);

    List<VeiculoJpaEntity> findByClienteId(UUID clienteId);

    boolean existsByPlaca(String placa);
}

