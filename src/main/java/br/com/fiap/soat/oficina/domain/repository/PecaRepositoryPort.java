package br.com.fiap.soat.oficina.domain.repository;

import br.com.fiap.soat.oficina.domain.entity.Peca;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PecaRepositoryPort {

    Peca salvar(Peca peca);

    Optional<Peca> buscarPorId(UUID id);

    Optional<Peca> buscarPorCodigo(String codigo);

    List<Peca> listarTodas();

    List<Peca> buscarComEstoqueAbaixoDoMinimo();

    boolean existePorCodigo(String codigo);

    void deletar(UUID id);
}

