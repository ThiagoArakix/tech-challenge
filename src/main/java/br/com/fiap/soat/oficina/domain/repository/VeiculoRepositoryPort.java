package br.com.fiap.soat.oficina.domain.repository;

import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepositoryPort {

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> buscarPorId(UUID id);

    Optional<Veiculo> buscarPorPlaca(Placa placa);

    List<Veiculo> buscarPorClienteId(UUID clienteId);

    List<Veiculo> listarTodos();

    boolean existePorPlaca(Placa placa);

    void deletar(UUID id);
}

