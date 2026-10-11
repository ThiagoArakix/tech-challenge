package br.com.fiap.soat.oficina.infrastructure.config;

import br.com.fiap.soat.oficina.application.usecase.cliente.AtualizarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.BuscarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.CadastrarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.ListarClientesUseCase;
import br.com.fiap.soat.oficina.application.usecase.peca.CadastrarPecaUseCase;
import br.com.fiap.soat.oficina.application.usecase.peca.GerenciarEstoquePecaUseCase;
import br.com.fiap.soat.oficina.application.usecase.peca.ListarPecasUseCase;
import br.com.fiap.soat.oficina.application.usecase.veiculo.BuscarVeiculoUseCase;
import br.com.fiap.soat.oficina.application.usecase.veiculo.CadastrarVeiculoUseCase;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrudUseCaseBeanConfig {

    // =========================================================================
    // Use Cases de Clientes
    // =========================================================================

    @Bean
    public CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        return new CadastrarClienteUseCase(clienteRepository);
    }

    @Bean
    public BuscarClienteUseCase buscarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        return new BuscarClienteUseCase(clienteRepository);
    }

    @Bean
    public AtualizarClienteUseCase atualizarClienteUseCase(ClienteRepositoryPort clienteRepository) {
        return new AtualizarClienteUseCase(clienteRepository);
    }

    @Bean
    public ListarClientesUseCase listarClientesUseCase(ClienteRepositoryPort clienteRepository) {
        return new ListarClientesUseCase(clienteRepository);
    }

    // =========================================================================
    // Use Cases de Veículos
    // =========================================================================

    @Bean
    public CadastrarVeiculoUseCase cadastrarVeiculoUseCase(
            VeiculoRepositoryPort veiculoRepository,
            ClienteRepositoryPort clienteRepository) {
        return new CadastrarVeiculoUseCase(veiculoRepository, clienteRepository);
    }

    @Bean
    public BuscarVeiculoUseCase buscarVeiculoUseCase(VeiculoRepositoryPort veiculoRepository) {
        return new BuscarVeiculoUseCase(veiculoRepository);
    }

    // =========================================================================
    // Use Cases de Peças
    // =========================================================================

    @Bean
    public CadastrarPecaUseCase cadastrarPecaUseCase(PecaRepositoryPort pecaRepository) {
        return new CadastrarPecaUseCase(pecaRepository);
    }

    @Bean
    public GerenciarEstoquePecaUseCase gerenciarEstoquePecaUseCase(PecaRepositoryPort pecaRepository) {
        return new GerenciarEstoquePecaUseCase(pecaRepository);
    }

    @Bean
    public ListarPecasUseCase listarPecasUseCase(PecaRepositoryPort pecaRepository) {
        return new ListarPecasUseCase(pecaRepository);
    }
}

