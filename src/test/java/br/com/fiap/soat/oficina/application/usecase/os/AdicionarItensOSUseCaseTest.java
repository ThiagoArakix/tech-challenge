package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemPecaInput;
import br.com.fiap.soat.oficina.application.dto.request.AdicionarItemServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.PecaRepositoryPort;
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

    private AdicionarItensOSUseCase useCase;

    private OrdemServico os;
    private Peca peca;

    @BeforeEach
    void setUp() {
        useCase = new AdicionarItensOSUseCase(ordemServicoRepository, pecaRepository);

        os = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Revisão periódica");
        os.iniciarDiagnostico();

        peca = Peca.criarNova("AMORT-01", "Amortecedor Dianteiro", "Desc", Dinheiro.de(250.00), 10, 2);
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

