package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class AprovarOrcamentoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final PecaRepositoryPort pecaRepository;

    public AprovarOrcamentoUseCase(OrdemServicoRepositoryPort ordemServicoRepository,
                                  PecaRepositoryPort pecaRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
        this.pecaRepository = Objects.requireNonNull(pecaRepository);
    }

    public OrdemServicoOutput executar(UUID osId) {
        OrdemServico os = ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ordem de Serviço", osId));

        // Orquestra a baixa física de cada peça alocada no estoque da oficina
        for (ItemPecaOrdem itemPeca : os.getItensPeca()) {
            Peca peca = pecaRepository.buscarPorId(itemPeca.getPecaId())
                    .orElseThrow(() -> RecursoNaoEncontradoException.de("Peça", itemPeca.getPecaId()));

            peca.debitarEstoque(itemPeca.getQuantidade());
            pecaRepository.salvar(peca);
        }

        // Transiciona a Ordem de Serviço para EM_EXECUCAO e registra o timestamp de aprovação
        os.aprovar();
        OrdemServico salva = ordemServicoRepository.salvar(os);

        return OrdemServicoOutput.deDominio(salva);
    }
}

