package br.com.fiap.soat.oficina.application.usecase.cliente;

import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ListarClientesUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public ListarClientesUseCase(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
    }

    public List<ClienteOutput> executar() {
        return clienteRepository.listarTodos().stream()
                .map(ClienteOutput::deDominio)
                .toList();
    }
}

