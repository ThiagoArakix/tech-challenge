package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Caso de uso de leitura administrativa de Ordens de Serviço.
 * Garante que a camada de apresentação nunca acesse portas de repositório
 * nem manipule entidades de domínio diretamente.
 */
public class ListarOrdensServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public ListarOrdensServicoUseCase(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
    }

    public List<OrdemServicoOutput> listar(StatusOS status) {
        var ordens = (status != null)
                ? ordemServicoRepository.buscarPorStatus(status)
                : ordemServicoRepository.listarTodas();
        return ordens.stream().map(OrdemServicoOutput::deDominio).toList();
    }

    public OrdemServicoOutput buscarPorId(UUID id) {
        return ordemServicoRepository.buscarPorId(id)
                .map(OrdemServicoOutput::deDominio)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ordem de Serviço", id));
    }
}

