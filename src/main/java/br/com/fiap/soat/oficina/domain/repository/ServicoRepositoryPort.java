package br.com.fiap.soat.oficina.domain.repository;

import br.com.fiap.soat.oficina.domain.entity.Servico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoRepositoryPort {

    Servico salvar(Servico servico);

    Optional<Servico> buscarPorId(UUID id);

    Optional<Servico> buscarPorCodigo(String codigo);

    List<Servico> listarTodos();

    boolean existePorCodigo(String codigo);

    void deletar(UUID id);
}

