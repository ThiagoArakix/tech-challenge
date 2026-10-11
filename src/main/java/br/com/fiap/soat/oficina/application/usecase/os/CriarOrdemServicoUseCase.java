package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.request.CriarOrdemServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;

import java.util.Objects;

public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final VeiculoRepositoryPort veiculoRepository;

    public CriarOrdemServicoUseCase(OrdemServicoRepositoryPort ordemServicoRepository,
                                   ClienteRepositoryPort clienteRepository,
                                   VeiculoRepositoryPort veiculoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
        this.clienteRepository = Objects.requireNonNull(clienteRepository);
        this.veiculoRepository = Objects.requireNonNull(veiculoRepository);
    }

    public OrdemServicoOutput executar(CriarOrdemServicoInput input) {
        Objects.requireNonNull(input, "Os dados de entrada para abertura da OS são obrigatórios");

        Cliente cliente = clienteRepository.buscarPorId(input.clienteId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", input.clienteId()));

        Veiculo veiculo = veiculoRepository.buscarPorId(input.veiculoId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Veículo", input.veiculoId()));

        // Regra de negócio: validação de posse do veículo pelo cliente
        if (!veiculo.getClienteId().equals(cliente.getId())) {
            throw new DomainException(String.format(
                    "O veículo de placa '%s' não pertence ao cliente informado '%s'",
                    veiculo.getPlaca().formatada(), cliente.getNome()));
        }

        OrdemServico novaOrdem = OrdemServico.abrirNova(cliente.getId(), veiculo.getId(), input.descricaoProblema());
        OrdemServico salva = ordemServicoRepository.salvar(novaOrdem);

        return OrdemServicoOutput.deDominio(salva);
    }
}

