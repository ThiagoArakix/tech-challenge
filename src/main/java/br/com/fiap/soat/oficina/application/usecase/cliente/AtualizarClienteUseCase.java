package br.com.fiap.soat.oficina.application.usecase.cliente;

import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class AtualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepository;

    public AtualizarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
    }

    public ClienteOutput executar(UUID id, String novoNome, String novoEmail, String novoTelefone) {
        Cliente cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", id));

        cliente.atualizar(novoNome, novoEmail, novoTelefone);
        Cliente salvo = clienteRepository.salvar(cliente);

        return ClienteOutput.deDominio(salvo);
    }

    public void deletar(UUID id) {
        if (clienteRepository.buscarPorId(id).isEmpty()) {
            throw RecursoNaoEncontradoException.de("Cliente", id);
        }
        clienteRepository.deletar(id);
    }
}

