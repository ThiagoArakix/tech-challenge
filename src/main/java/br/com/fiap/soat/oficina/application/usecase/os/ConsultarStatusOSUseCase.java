package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.StatusOSOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class ConsultarStatusOSUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;

    public ConsultarStatusOSUseCase(OrdemServicoRepositoryPort ordemServicoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
    }

    public StatusOSOutput executar(UUID osId) {
        OrdemServico os = ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ordem de Serviço", osId));

        return StatusOSOutput.deDominio(os);
    }
}

