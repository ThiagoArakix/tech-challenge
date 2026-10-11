package br.com.fiap.soat.oficina.infrastructure.persistence.adapter;

import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.infrastructure.persistence.entity.PecaJpaEntity;
import br.com.fiap.soat.oficina.infrastructure.persistence.mapper.PecaMapper;
import br.com.fiap.soat.oficina.infrastructure.persistence.repository.SpringDataPecaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class PecaRepositoryAdapter implements PecaRepositoryPort {

    private final SpringDataPecaRepository repository;

    public PecaRepositoryAdapter(SpringDataPecaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Peca salvar(Peca peca) {
        PecaJpaEntity entity = PecaMapper.toEntity(peca);
        PecaJpaEntity salva = repository.save(entity);
        return PecaMapper.toDomain(salva);
    }

    @Override
    public Optional<Peca> buscarPorId(UUID id) {
        return repository.findById(id).map(PecaMapper::toDomain);
    }

    @Override
    public Optional<Peca> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(PecaMapper::toDomain);
    }

    @Override
    public List<Peca> listarTodas() {
        return repository.findAll().stream()
                .map(PecaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Peca> buscarComEstoqueAbaixoDoMinimo() {
        return repository.findEstoqueAbaixoDoMinimo().stream()
                .map(PecaMapper::toDomain)
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

