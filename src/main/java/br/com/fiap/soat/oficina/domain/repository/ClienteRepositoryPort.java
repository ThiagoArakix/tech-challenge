package br.com.fiap.soat.oficina.domain.repository;

import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    Optional<Cliente> buscarPorDocumento(CpfCnpj documento);

    Optional<Cliente> buscarPorEmail(String email);

    List<Cliente> listarTodos();

    boolean existePorDocumento(CpfCnpj documento);

    boolean existePorEmail(String email);

    void deletar(UUID id);
}

