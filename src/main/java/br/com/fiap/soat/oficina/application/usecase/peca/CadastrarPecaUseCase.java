package br.com.fiap.soat.oficina.application.usecase.peca;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarPecaInput;
import br.com.fiap.soat.oficina.application.dto.response.PecaOutput;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.util.Objects;

public class CadastrarPecaUseCase {

    private final PecaRepositoryPort pecaRepository;

    public CadastrarPecaUseCase(PecaRepositoryPort pecaRepository) {
        this.pecaRepository = Objects.requireNonNull(pecaRepository);
    }

    public PecaOutput executar(CadastrarPecaInput input) {
        Objects.requireNonNull(input, "Os dados para cadastro da peça são obrigatórios");

        String codigoFormatado = (input.codigo() != null) ? input.codigo().trim().toUpperCase() : null;

        if (codigoFormatado != null && pecaRepository.existePorCodigo(codigoFormatado)) {
            throw new DomainException(String.format("Já existe uma peça cadastrada com o código '%s'", codigoFormatado));
        }

        Peca novaPeca = Peca.criarNova(
                codigoFormatado,
                input.nome(),
                input.descricao(),
                Dinheiro.de(input.valorUnitario()),
                input.saldoEstoque(),
                input.estoqueMinimo()
        );

        Peca salva = pecaRepository.salvar(novaPeca);
        return PecaOutput.deDominio(salva);
    }
}

