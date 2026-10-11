package br.com.fiap.soat.oficina.application.usecase.veiculo;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarVeiculoInput;
import br.com.fiap.soat.oficina.application.dto.response.VeiculoOutput;
import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;

import java.util.Objects;

public class CadastrarVeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepository;
    private final ClienteRepositoryPort clienteRepository;

    public CadastrarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository,
                                  ClienteRepositoryPort clienteRepository) {
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository);
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
    }

    public VeiculoOutput executar(CadastrarVeiculoInput input) {
        Objects.requireNonNull(input, "Os dados de entrada para cadastro do veículo são obrigatórios");

        // Validação da existência do cliente proprietário
        if (clienteRepository.buscarPorId(input.clienteId()).isEmpty()) {
            throw RecursoNaoEncontradoException.de("Cliente", input.clienteId());
        }

        Placa placa = Placa.de(input.placa());

        // Validação de duplicidade de placa
        if (veiculoRepository.existePorPlaca(placa)) {
            throw new DomainException(String.format("Já existe um veículo cadastrado com a placa '%s'", placa.formatada()));
        }

        Veiculo novoVeiculo = Veiculo.criarNovo(
                input.clienteId(),
                placa,
                input.marca(),
                input.modelo(),
                input.anoFabricacao(),
                input.anoModelo(),
                input.cor(),
                input.quilometragem()
        );

        Veiculo salvo = veiculoRepository.salvar(novoVeiculo);
        return VeiculoOutput.deDominio(salvo);
    }
}

