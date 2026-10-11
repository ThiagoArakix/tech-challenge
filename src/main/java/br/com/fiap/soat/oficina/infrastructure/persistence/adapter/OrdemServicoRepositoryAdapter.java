package br.com.fiap.soat.oficina.infrastructure.persistence.adapter;

import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.mapper.OrdemServicoMapper;
import br.com.fiap.soat.oficina.infrastructure.persistence.repository.SpringDataOrdemServicoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrdemServicoRepositoryAdapter implements OrdemServicoRepositoryPort {

    private final SpringDataOrdemServicoRepository repository;

    public OrdemServicoRepositoryAdapter(SpringDataOrdemServicoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Transactional
    public OrdemServico salvar(OrdemServico ordemServico) {
        OrdemServicoJpaEntity entity = OrdemServicoMapper.toEntity(ordemServico);
        OrdemServicoJpaEntity salva = repository.save(entity);
        return OrdemServicoMapper.toDomain(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemServico> buscarPorId(UUID id) {
        return repository.findById(id).map(OrdemServicoMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorClienteId(UUID clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(OrdemServicoMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorVeiculoId(UUID veiculoId) {
        return repository.findByVeiculoId(veiculoId).stream()
                .map(OrdemServicoMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> buscarPorStatus(StatusOS status) {
        return repository.findByStatus(status.name()).stream()
                .map(OrdemServicoMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        return repository.findAll().stream()
                .map(OrdemServicoMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}

