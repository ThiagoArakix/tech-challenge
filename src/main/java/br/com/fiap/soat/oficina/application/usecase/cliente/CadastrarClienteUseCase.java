package br.com.fiap.soat.oficina.application.usecase.cliente;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarClienteInput;
import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;

import java.util.Objects;

public class CadastrarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public CadastrarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
    }

    public ClienteOutput executar(CadastrarClienteInput input) {
        Objects.requireNonNull(input, "Os dados de cadastro do cliente são obrigatórios");

        CpfCnpj documento = CpfCnpj.de(input.documento());

        if (clienteRepository.existePorDocumento(documento)) {
            throw new DomainException(String.format(
                    "Já existe um cliente cadastrado com o documento '%s'", documento.formatado()));
        }

        if (input.email() != null && !input.email().isBlank() && clienteRepository.existePorEmail(input.email().trim().toLowerCase())) {
            throw new DomainException(String.format(
                    "Já existe um cliente cadastrado com o e-mail '%s'", input.email().trim()));
        }

        Cliente novoCliente = Cliente.criarNovo(input.nome(), documento, input.email(), input.telefone());
        Cliente salvo = clienteRepository.salvar(novoCliente);

        return ClienteOutput.deDominio(salvo);
    }
}

