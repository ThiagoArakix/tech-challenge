package br.com.fiap.soat.oficina.application.usecase.peca;

import br.com.fiap.soat.oficina.application.dto.response.PecaOutput;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListarPecasUseCase {

    private final PecaRepositoryPort pecaRepository;

    public ListarPecasUseCase(PecaRepositoryPort pecaRepository) {
        this.pecaRepository = Objects.requireNonNull(pecaRepository);
    }

    public PecaOutput buscarPorId(UUID id) {
        Peca peca = pecaRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Peça", id));
        return PecaOutput.deDominio(peca);
    }

    public List<PecaOutput> listarTodas() {
        return pecaRepository.listarTodas().stream()
                .map(PecaOutput::deDominio)
                .toList();
    }

    public List<PecaOutput> listarAbaixoDoMinimo() {
        return pecaRepository.buscarComEstoqueAbaixoDoMinimo().stream()
                .map(PecaOutput::deDominio)
                .toList();
    }
}

