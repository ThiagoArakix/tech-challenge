package br.com.fiap.soat.oficina.application.usecase.veiculo;

import br.com.fiap.soat.oficina.application.dto.response.VeiculoOutput;
import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BuscarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;

    public BuscarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository) {
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository);
    }

    public VeiculoOutput buscarPorId(UUID id) {
        Veiculo veiculo = veiculoRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Veículo", id));
        return VeiculoOutput.deDominio(veiculo);
    }

    public VeiculoOutput buscarPorPlaca(String placaInformada) {
        Placa placa = Placa.de(placaInformada);
        Veiculo veiculo = veiculoRepository.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado para a placa: " + placa.formatada()));
        return VeiculoOutput.deDominio(veiculo);
    }

    public List<VeiculoOutput> listarPorCliente(UUID clienteId) {
        return veiculoRepository.buscarPorClienteId(clienteId).stream()
                .map(VeiculoOutput::deDominio)
                .toList();
    }

    public List<VeiculoOutput> listarTodos() {
        return veiculoRepository.listarTodos().stream()
                .map(VeiculoOutput::deDominio)
                .toList();
    }

    public VeiculoOutput atualizar(UUID id, String novaMarca, String novoModelo, String novaCor, Integer novaQuilometragem) {
        Veiculo veiculo = veiculoRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Veículo", id));

        veiculo.atualizarDados(novaMarca, novoModelo, novaCor);
        if (novaQuilometragem != null) {
            veiculo.atualizarQuilometragem(novaQuilometragem);
        }

        Veiculo salvo = veiculoRepository.salvar(veiculo);
        return VeiculoOutput.deDominio(salvo);
    }

    public void deletar(UUID id) {
        if (veiculoRepository.buscarPorId(id).isEmpty()) {
            throw RecursoNaoEncontradoException.de("Veículo", id);
        }
        veiculoRepository.deletar(id);
    }
}

