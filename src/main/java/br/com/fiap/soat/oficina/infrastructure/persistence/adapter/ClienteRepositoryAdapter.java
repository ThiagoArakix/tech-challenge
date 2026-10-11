package br.com.fiap.soat.oficina.infrastructure.persistence.adapter;

import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ClienteJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.mapper.ClienteMapper;
import br.com.fiap.soat.oficina.infrastructure.persistence.repository.SpringDataClienteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository repository;

    public ClienteRepositoryAdapter(SpringDataClienteRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteJpaEntity entity = ClienteMapper.toEntity(cliente);
        ClienteJpaEntity salva = repository.save(entity);
        return ClienteMapper.toDomain(salva);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return repository.findById(id).map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(CpfCnpj documento) {
        return repository.findByDocumento(documento.valor()).map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return repository.findAll().stream()
                .map(ClienteMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorDocumento(CpfCnpj documento) {
        return repository.existsByDocumento(documento.valor());
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}

