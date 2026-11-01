package br.com.fiap.soat.oficina.integration;

import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.entity.ItemPecaOrdem;
import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.ServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - Adaptadores de Persistência com Banco H2 e Flyway")
class RepositoriosAdapterIntegrationTest {

    @Autowired
    private ClienteRepositoryPort clienteRepository;

    @Autowired
    private VeiculoRepositoryPort veiculoRepository;

    @Autowired
    private PecaRepositoryPort pecaRepository;

    @Autowired
    private ServicoRepositoryPort servicoRepository;

    @Autowired
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Test
    @DisplayName("Deve persistir e recuperar Cliente com integridade")
    void devePersistirCliente() {
        Cliente cliente = Cliente.criarNovo("Mariana Costa", CpfCnpj.de("52998224725"), "mariana@email.com", "11977778888");
        Cliente salvo = clienteRepository.salvar(cliente);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isEqualTo(cliente.getId());

        Optional<Cliente> recuperado = clienteRepository.buscarPorId(cliente.getId());
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getNome()).isEqualTo("Mariana Costa");
        assertThat(recuperado.get().getDocumento().formatado()).isEqualTo("529.982.247-25");

        assertThat(clienteRepository.existePorDocumento(CpfCnpj.de("52998224725"))).isTrue();
    }

    @Test
    @DisplayName("Deve persistir e recuperar Veículo associado a um cliente")
    void devePersistirVeiculo() {
        Cliente cliente = clienteRepository.salvar(
                Cliente.criarNovo("Roberto Lima", CpfCnpj.de("11144477735"), "roberto@email.com", "11988889999"));

        Veiculo veiculo = Veiculo.criarNovo(cliente.getId(), Placa.de("BRA2E19"), "Chevrolet", "Onix", 2022, 2023, "Prata", 15000);
        Veiculo salvo = veiculoRepository.salvar(veiculo);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPlaca().valor()).isEqualTo("BRA2E19");

        Optional<Veiculo> recuperado = veiculoRepository.buscarPorPlaca(Placa.de("BRA2E19"));
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getMarca()).isEqualTo("Chevrolet");
    }

    @Test
    @DisplayName("Deve persistir e recuperar Peça e controlar estoque")
    void devePersistirPeca() {
        Peca peca = Peca.criarNova("AMORT-01", "Amortecedor Dianteiro", "Amortecedor a gás", Dinheiro.de(320.00), 5, 2);
        Peca salva = pecaRepository.salvar(peca);

        assertThat(salva).isNotNull();
        assertThat(salva.getCodigo()).isEqualTo("AMORT-01");

        salva.debitarEstoque(4);
        pecaRepository.salvar(salva);

        List<Peca> pecasAbaixoDoMinimo = pecaRepository.buscarComEstoqueAbaixoDoMinimo();
        assertThat(pecasAbaixoDoMinimo).isNotEmpty();
        assertThat(pecasAbaixoDoMinimo.stream().anyMatch(p -> p.getCodigo().equals("AMORT-01"))).isTrue();
    }

    @Test
    @DisplayName("Deve persistir e recuperar Serviço do catálogo")
    void devePersistirServico() {
        Servico servico = Servico.criarNovo("SRV-BALANCEAMENTO", "Balanceamento de Rodas", "Balanceamento computadorizado", Dinheiro.de(90.00));
        Servico salvo = servicoRepository.salvar(servico);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getCodigo()).isEqualTo("SRV-BALANCEAMENTO");
        assertThat(servicoRepository.existePorCodigo("SRV-BALANCEAMENTO")).isTrue();
        assertThat(servicoRepository.buscarPorCodigo("SRV-BALANCEAMENTO")).isPresent();
        assertThat(servicoRepository.buscarPorId(salvo.getId())).isPresent();
        assertThat(servicoRepository.listarTodos()).isNotEmpty();

        servicoRepository.deletar(salvo.getId());
        assertThat(servicoRepository.buscarPorId(salvo.getId())).isEmpty();
    }

    @Test
    @DisplayName("Deve persistir Ordem de Serviço completa com serviços e peças associadas")
    void devePersistirOrdemServicoCompleta() {
        Cliente cliente = clienteRepository.salvar(
                Cliente.criarNovo("Ana Paula", CpfCnpj.de("85387431041"), "ana@email.com", "11911112222"));

        Veiculo veiculo = veiculoRepository.salvar(
                Veiculo.criarNovo(cliente.getId(), Placa.de("XYZ9Z99"), "Toyota", "Corolla", 2021, 2022, "Preto", 30000));

        Peca peca = pecaRepository.salvar(
                Peca.criarNova("OLEO-5W30", "Óleo Sintético 5W30", "Litro de óleo", Dinheiro.de(65.00), 20, 5));

        OrdemServico os = OrdemServico.abrirNova(cliente.getId(), veiculo.getId(), "Troca de óleo e filtro");
        os.iniciarDiagnostico();
        os.registrarDiagnostico("Troca regular por tempo");
        os.adicionarItemServico(ItemServicoOrdem.criarNovo("Mão de obra troca de óleo", 1, Dinheiro.de(50.00)));
        os.adicionarItemPeca(ItemPecaOrdem.criarNovo(peca.getId(), 4, peca.getValorUnitario())); // 260.00
        os.enviarParaAprovacao();

        OrdemServico osSalva = ordemServicoRepository.salvar(os);

        assertThat(osSalva).isNotNull();
        assertThat(osSalva.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);

        Optional<OrdemServico> osRecuperada = ordemServicoRepository.buscarPorId(osSalva.getId());
        assertThat(osRecuperada).isPresent();
        assertThat(osRecuperada.get().getItensServico()).hasSize(1);
        assertThat(osRecuperada.get().getItensPeca()).hasSize(1);
        assertThat(osRecuperada.get().getValorTotal()).isEqualTo(Dinheiro.de(310.00));

        // Testar consultas adicionais dos adaptadores
        assertThat(clienteRepository.listarTodos()).isNotEmpty();
        assertThat(clienteRepository.buscarPorEmail("ana@email.com")).isPresent();

        assertThat(veiculoRepository.buscarPorClienteId(cliente.getId())).isNotEmpty();
        assertThat(veiculoRepository.listarTodos()).isNotEmpty();
        assertThat(veiculoRepository.existePorPlaca(Placa.de("XYZ9Z99"))).isTrue();

        assertThat(pecaRepository.listarTodas()).isNotEmpty();
        assertThat(pecaRepository.buscarPorCodigo("OLEO-5W30")).isPresent();
        assertThat(pecaRepository.existePorCodigo("OLEO-5W30")).isTrue();

        assertThat(ordemServicoRepository.buscarPorStatus(StatusOS.AGUARDANDO_APROVACAO)).isNotEmpty();
        assertThat(ordemServicoRepository.buscarPorClienteId(cliente.getId())).isNotEmpty();
        assertThat(ordemServicoRepository.buscarPorVeiculoId(veiculo.getId())).isNotEmpty();
    }
}

