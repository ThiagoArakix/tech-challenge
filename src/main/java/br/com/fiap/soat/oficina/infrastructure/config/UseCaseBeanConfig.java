package br.com.fiap.soat.oficina.infrastructure.config;

import br.com.fiap.soat.oficina.application.usecase.os.AdicionarItensOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.AprovarOrcamentoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.AtualizarStatusOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.CalcularTempoMedioExecucaoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.ConsultarStatusOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.CriarOrdemServicoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.ListarOrdensServicoUseCase;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseBeanConfig {

    @Bean
    public CriarOrdemServicoUseCase criarOrdemServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository,
            ClienteRepositoryPort clienteRepository,
            VeiculoRepositoryPort veiculoRepository) {
        return new CriarOrdemServicoUseCase(ordemServicoRepository, clienteRepository, veiculoRepository);
    }

    @Bean
    public AdicionarItensOSUseCase adicionarItensOSUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository,
            PecaRepositoryPort pecaRepository) {
        return new AdicionarItensOSUseCase(ordemServicoRepository, pecaRepository);
    }

    @Bean
    public AprovarOrcamentoUseCase aprovarOrcamentoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository,
            PecaRepositoryPort pecaRepository) {
        return new AprovarOrcamentoUseCase(ordemServicoRepository, pecaRepository);
    }

    @Bean
    public AtualizarStatusOSUseCase atualizarStatusOSUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository) {
        return new AtualizarStatusOSUseCase(ordemServicoRepository);
    }

    @Bean
    public ConsultarStatusOSUseCase consultarStatusOSUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository) {
        return new ConsultarStatusOSUseCase(ordemServicoRepository);
    }

    @Bean
    public CalcularTempoMedioExecucaoUseCase calcularTempoMedioExecucaoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository) {
        return new CalcularTempoMedioExecucaoUseCase(ordemServicoRepository);
    }

    @Bean
    public ListarOrdensServicoUseCase listarOrdensServicoUseCase(
            OrdemServicoRepositoryPort ordemServicoRepository) {
        return new ListarOrdensServicoUseCase(ordemServicoRepository);
    }
}

