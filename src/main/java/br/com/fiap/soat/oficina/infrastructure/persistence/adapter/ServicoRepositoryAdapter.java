package br.com.fiap.soat.oficina.infrastructure.persistence.adapter;

import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.repository.ServicoRepositoryPort;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.ServicoJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.mapper.ServicoMapper;
import br.com.fiap.soat.oficina.infrastructure.persistence.repository.SpringDataServicoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServicoRepositoryAdapter implements ServicoRepositoryPort {

    private final SpringDataServicoRepository repository;

    public ServicoRepositoryAdapter(SpringDataServicoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Servico salvar(Servico servico) {
        ServicoJpaEntity entity = ServicoMapper.toEntity(servico);
        ServicoJpaEntity salva = repository.save(entity);
        return ServicoMapper.toDomain(salva);
    }

    @Override
    public Optional<Servico> buscarPorId(UUID id) {
        return repository.findById(id).map(ServicoMapper::toDomain);
    }

    @Override
    public Optional<Servico> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(ServicoMapper::toDomain);
    }

    @Override
    public List<Servico> listarTodos() {
        return repository.findAll().stream()
                .map(ServicoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return repository.existsByCodigo(codigo);
    }

    @Override
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}

