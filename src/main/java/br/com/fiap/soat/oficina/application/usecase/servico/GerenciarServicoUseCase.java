package br.com.fiap.soat.oficina.application.usecase.servico;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.ServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GerenciarServicoUseCase {

    private final ServicoRepositoryPort servicoRepository;

    public GerenciarServicoUseCase(ServicoRepositoryPort servicoRepository) {
        this.servicoRepository = Objects.requireNonNull(servicoRepository);
    }

    public ServicoOutput cadastrar(CadastrarServicoInput input) {
        Objects.requireNonNull(input, "Os dados para cadastro do serviço são obrigatórios");

        String codigoFormatado = (input.codigo() != null) ? input.codigo().trim().toUpperCase() : null;

        if (codigoFormatado != null && servicoRepository.existePorCodigo(codigoFormatado)) {
            throw new DomainException(String.format("Já existe um serviço cadastrado com o código '%s'", codigoFormatado));
        }

        Servico novoServico = Servico.criarNovo(
                codigoFormatado,
                input.nome(),
                input.descricao(),
                Dinheiro.de(input.valorUnitario())
        );

        Servico salvo = servicoRepository.salvar(novoServico);
        return ServicoOutput.deDominio(salvo);
    }

    public ServicoOutput buscarPorId(UUID id) {
        Servico servico = servicoRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Serviço", id));
        return ServicoOutput.deDominio(servico);
    }

    public ServicoOutput buscarPorCodigo(String codigo) {
        String codigoFormatado = (codigo != null) ? codigo.trim().toUpperCase() : "";
        Servico servico = servicoRepository.buscarPorCodigo(codigoFormatado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado para o código: " + codigoFormatado));
        return ServicoOutput.deDominio(servico);
    }

    public List<ServicoOutput> listarTodos() {
        return servicoRepository.listarTodos().stream()
                .map(ServicoOutput::deDominio)
                .toList();
    }

    public ServicoOutput atualizar(UUID id, String novoNome, String novaDescricao, BigDecimal novoValorUnitario) {
        Servico servico = servicoRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Serviço", id));

        servico.atualizarDados(novoNome, novaDescricao, Dinheiro.de(novoValorUnitario));
        Servico salvo = servicoRepository.salvar(servico);
        return ServicoOutput.deDominio(salvo);
    }

    public void deletar(UUID id) {
        if (servicoRepository.buscarPorId(id).isEmpty()) {
            throw RecursoNaoEncontradoException.de("Serviço", id);
        }
        servicoRepository.deletar(id);
    }
}

