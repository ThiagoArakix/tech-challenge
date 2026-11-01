package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemPecaInput;
import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.ServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - AdicionarItensOSUseCase")
class AdicionarItensOSUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private PecaRepositoryPort pecaRepository;

    @Mock
    private ServicoRepositoryPort servicoRepository;

    private AdicionarItensOSUseCase useCase;

    private OrdemServico os;
    private Peca peca;
    private Servico servicoCatalogo;

    @BeforeEach
    void setUp() {
        useCase = new AdicionarItensOSUseCase(ordemServicoRepository, pecaRepository, servicoRepository);

        os = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Revisão periódica");
        os.iniciarDiagnostico();

        peca = Peca.criarNova("AMORT-01", "Amortecedor Dianteiro", "Desc", Dinheiro.de(250.00), 10, 2);
        servicoCatalogo = Servico.criarNovo("SRV-ALIN", "Alinhamento 3D", "Desc", Dinheiro.de(120.00));
    }

    @Test
    @DisplayName("Deve adicionar itens de serviço e peças com sucesso")
    void deveAdicionarItensComSucesso() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(pecaRepository.buscarPorId(peca.getId())).thenReturn(Optional.of(peca));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        List<AdicionarItemServicoInput> servicos = List.of(
                new AdicionarItemServicoInput("Mão de obra troca amortecedor", BigDecimal.valueOf(2), BigDecimal.valueOf(100.00))
        );
        List<AdicionarItemPecaInput> pecas = List.of(
                new AdicionarItemPecaInput(peca.getId(), 2)
        );

        OrdemServicoOutput output = useCase.executar(os.getId(), servicos, pecas);

        assertThat(output).isNotNull();
        assertThat(output.itensServico()).hasSize(1);
        assertThat(output.itensPeca()).hasSize(1);
        assertThat(output.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(700.00)); // 200 + 500
        verify(ordemServicoRepository).salvar(any());
    }

    @Test
    @DisplayName("Deve adicionar serviço pelo servicoId do catálogo e permitir remoção de itens")
    void deveAdicionarServicoDoCatalogoERemoverItens() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(servicoRepository.buscarPorId(servicoCatalogo.getId())).thenReturn(Optional.of(servicoCatalogo));
        when(pecaRepository.buscarPorId(peca.getId())).thenReturn(Optional.of(peca));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput aposServico = useCase.adicionarServico(
                os.getId(),
                new AdicionarItemServicoInput(servicoCatalogo.getId(), null, BigDecimal.ONE, null));
        assertThat(aposServico.itensServico()).hasSize(1);
        assertThat(aposServico.itensServico().getFirst().descricao()).isEqualTo("Alinhamento 3D");
        assertThat(aposServico.valorTotalServicos()).isEqualByComparingTo(BigDecimal.valueOf(120.00));

        OrdemServicoOutput aposPeca = useCase.adicionarPeca(os.getId(), new AdicionarItemPecaInput(peca.getId(), 1));
        assertThat(aposPeca.itensPeca()).hasSize(1);

        UUID itemServicoId = aposServico.itensServico().getFirst().id();
        UUID itemPecaId = aposPeca.itensPeca().getFirst().id();

        OrdemServicoOutput semServico = useCase.removerServico(os.getId(), itemServicoId);
        assertThat(semServico.itensServico()).isEmpty();

        OrdemServicoOutput semPeca = useCase.removerPeca(os.getId(), itemPecaId);
        assertThat(semPeca.itensPeca()).isEmpty();
    }

    @Test
    @DisplayName("Deve falhar ao adicionar itens quando OS não for encontrada")
    void deveFalharOsNaoEncontrada() {
        UUID osInexistente = UUID.randomUUID();
        when(ordemServicoRepository.buscarPorId(osInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(osInexistente, null, null))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve falhar quando peça informada não existir no catálogo")
    void deveFalharPecaNaoEncontrada() {
        UUID pecaInexistente = UUID.randomUUID();
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(pecaRepository.buscarPorId(pecaInexistente)).thenReturn(Optional.empty());

        List<AdicionarItemPecaInput> pecas = List.of(
                new AdicionarItemPecaInput(pecaInexistente, 1)
        );

        assertThatThrownBy(() -> useCase.executar(os.getId(), null, pecas))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve falhar quando saldo de estoque for inferior à quantidade solicitada")
    void deveFalharSaldoEstoqueInsuficiente() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(pecaRepository.buscarPorId(peca.getId())).thenReturn(Optional.of(peca));

        List<AdicionarItemPecaInput> pecas = List.of(
                new AdicionarItemPecaInput(peca.getId(), 15) // estoque tem 10
        );

        assertThatThrownBy(() -> useCase.executar(os.getId(), null, pecas))
                .isInstanceOf(SaldoEstoqueInsuficienteException.class);
    }
}

