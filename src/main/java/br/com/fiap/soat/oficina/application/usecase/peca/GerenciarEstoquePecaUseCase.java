package br.com.fiap.soat.oficina.application.usecase.peca;

import br.com.fiap.soat.oficina.application.dto.response.PecaOutput;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class GerenciarEstoquePecaUseCase {

    private final PecaRepositoryPort pecaRepository;

    public GerenciarEstoquePecaUseCase(PecaRepositoryPort pecaRepository) {
        this.pecaRepository = Objects.requireNonNull(pecaRepository);
    }

    public PecaOutput reporEstoque(UUID pecaId, int quantidade) {
        Peca peca = pecaRepository.buscarPorId(pecaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Peça", pecaId));

        peca.reporEstoque(quantidade);
        Peca salva = pecaRepository.salvar(peca);

        return PecaOutput.deDominio(salva);
    }

    public PecaOutput debitarEstoque(UUID pecaId, int quantidade) {
        Peca peca = pecaRepository.buscarPorId(pecaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Peça", pecaId));

        peca.debitarEstoque(quantidade);
        Peca salva = pecaRepository.salvar(peca);

        return PecaOutput.deDominio(salva);
    }
}

