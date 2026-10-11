package br.com.fiap.soat.oficina.infrastructure.persistence.adapter;

import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.VeiculoJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.mapper.VeiculoMapper;
import br.com.fiap.soat.oficina.infrastructure.persistence.repository.SpringDataVeiculoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class VeiculoRepositoryAdapter implements VeiculoRepositoryPort {

    private final SpringDataVeiculoRepository repository;

    public VeiculoRepositoryAdapter(SpringDataVeiculoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        VeiculoJpaEntity entity = VeiculoMapper.toEntity(veiculo);
        VeiculoJpaEntity salva = repository.save(entity);
        return VeiculoMapper.toDomain(salva);
    }

    @Override
    public Optional<Veiculo> buscarPorId(UUID id) {
        return repository.findById(id).map(VeiculoMapper::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(Placa placa) {
        return repository.findByPlaca(placa.valor()).map(VeiculoMapper::toDomain);
    }

    @Override
    public List<Veiculo> buscarPorClienteId(UUID clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(VeiculoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Veiculo> listarTodos() {
        return repository.findAll().stream()
                .map(VeiculoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorPlaca(Placa placa) {
        return repository.existsByPlaca(placa.valor());
    }

    @Override
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}

