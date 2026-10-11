package br.com.fiap.soat.oficina.infrastructure.persistence.repository;

import br.com.fiap.soat.oficina.infrastructure.persistence.entity.PecaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPecaRepository extends JpaRepository<PecaJpaEntity, UUID> {

    Optional<PecaJpaEntity> findByCodigo(String codigo);

    @Query("SELECT p FROM PecaJpaEntity p WHERE p.saldoEstoque < p.estoqueMinimo")
    List<PecaJpaEntity> findEstoqueAbaixoDoMinimo();

    boolean existsByCodigo(String codigo);
}

