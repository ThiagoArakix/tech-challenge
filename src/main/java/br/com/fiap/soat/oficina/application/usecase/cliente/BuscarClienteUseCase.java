package br.com.fiap.soat.oficina.application.usecase.cliente;

import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;

import java.util.Objects;
import java.util.UUID;

public class BuscarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public BuscarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
    }

    public ClienteOutput buscarPorId(UUID id) {
        Cliente cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", id));
        return ClienteOutput.deDominio(cliente);
    }

    public ClienteOutput buscarPorDocumento(String documento) {
        CpfCnpj cpfCnpj = CpfCnpj.de(documento);
        Cliente cliente = clienteRepository.buscarPorDocumento(cpfCnpj)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com o documento: " + cpfCnpj.formatado()));
        return ClienteOutput.deDominio(cliente);
    }
}

