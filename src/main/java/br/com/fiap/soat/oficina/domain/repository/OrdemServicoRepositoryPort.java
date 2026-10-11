package br.com.fiap.soat.oficina.domain.repository;

import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepositoryPort {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(UUID id);

    List<OrdemServico> buscarPorClienteId(UUID clienteId);

    List<OrdemServico> buscarPorVeiculoId(UUID veiculoId);

    List<OrdemServico> buscarPorStatus(StatusOS status);

    List<OrdemServico> listarTodas();

    void deletar(UUID id);
}

