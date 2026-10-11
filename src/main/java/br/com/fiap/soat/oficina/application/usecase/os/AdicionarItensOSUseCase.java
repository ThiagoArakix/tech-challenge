package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemPecaInput;
import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;
import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AdicionarItensOSUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final PecaRepositoryPort pecaRepository;

    public AdicionarItensOSUseCase(OrdemServicoRepositoryPort ordemServicoRepository,
                                  PecaRepositoryPort pecaRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
        this.pecaRepository = Objects.requireNonNull(pecaRepository);
    }

    public OrdemServicoOutput executar(UUID osId,
                                       List<AdicionarItemServicoInput> servicos,
                                       List<AdicionarItemPecaInput> pecas) {
        OrdemServico os = ordemServicoRepository.buscarPorId(osId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Ordem de Serviço", osId));

        if (servicos != null) {
            for (AdicionarItemServicoInput servicoInput : servicos) {
                ItemServicoOrdem itemServico = ItemServicoOrdem.criarNovo(
                        servicoInput.descricao(),
                        servicoInput.quantidade(),
                        Dinheiro.de(servicoInput.valorUnitario())
                );
                os.adicionarItemServico(itemServico);
            }
        }

        if (pecas != null) {
            for (AdicionarItemPecaInput pecaInput : pecas) {
                Peca peca = pecaRepository.buscarPorId(pecaInput.pecaId())
                        .orElseThrow(() -> RecursoNaoEncontradoException.de("Peça", pecaInput.pecaId()));

                // Verificação prévia de disponibilidade de saldo em estoque
                if (peca.getSaldoEstoque() < pecaInput.quantidade()) {
                    throw new SaldoEstoqueInsuficienteException(peca.getSaldoEstoque(), pecaInput.quantidade());
                }

                ItemPecaOrdem itemPeca = ItemPecaOrdem.criarNovo(
                        peca.getId(),
                        pecaInput.quantidade(),
                        peca.getValorUnitario()
                );
                os.adicionarItemPeca(itemPeca);
            }
        }

        OrdemServico salva = ordemServicoRepository.salvar(os);
        return OrdemServicoOutput.deDominio(salva);
    }

    public OrdemServicoOutput adicionarServico(UUID osId, AdicionarItemServicoInput input) {
        return executar(osId, List.of(input), List.of());
    }

    public OrdemServicoOutput adicionarPeca(UUID osId, AdicionarItemPecaInput input) {
        return executar(osId, List.of(), List.of(input));
    }
}

