package br.com.fiap.soat.oficina.application.usecase.peca;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarPecaInput;
import br.com.fiap.soat.oficina.application.dto.response.PecaOutput;
import br.com.fiap.soat.oficina.domain.entity.Peca;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
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
@DisplayName("Testes de Unidade - Use Cases de Peça")
class PecaUseCasesTest {

    @Mock
    private PecaRepositoryPort pecaRepository;

    private CadastrarPecaUseCase cadastrarPecaUseCase;
    private GerenciarEstoquePecaUseCase gerenciarEstoquePecaUseCase;
    private ListarPecasUseCase listarPecasUseCase;

    private Peca pecaExistente;

    @BeforeEach
    void setUp() {
        cadastrarPecaUseCase = new CadastrarPecaUseCase(pecaRepository);
        gerenciarEstoquePecaUseCase = new GerenciarEstoquePecaUseCase(pecaRepository);
        listarPecasUseCase = new ListarPecasUseCase(pecaRepository);

        pecaExistente = Peca.criarNova("FIL-01", "Filtro de Óleo", "Desc", Dinheiro.de(50.00), 10, 3);
    }

    @Test
    @DisplayName("Deve cadastrar peça quando código for inédito")
    void deveCadastrarPecaComSucesso() {
        when(pecaRepository.existePorCodigo("FIL-01")).thenReturn(false);
        when(pecaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        CadastrarPecaInput input = new CadastrarPecaInput("FIL-01", "Filtro de Óleo", "Desc", BigDecimal.valueOf(50.00), 10, 3);
        PecaOutput output = cadastrarPecaUseCase.executar(input);

        assertThat(output).isNotNull();
        assertThat(output.codigo()).isEqualTo("FIL-01");
        verify(pecaRepository).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar peça com código duplicado")
    void deveFalharCodigoDuplicado() {
        when(pecaRepository.existePorCodigo("FIL-01")).thenReturn(true);

        CadastrarPecaInput input = new CadastrarPecaInput("FIL-01", "Filtro", "Desc", BigDecimal.valueOf(50.00), 10, 3);

        assertThatThrownBy(() -> cadastrarPecaUseCase.executar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("código");
    }

    @Test
    @DisplayName("Deve repor estoque de peça com sucesso")
    void deveReporEstoqueComSucesso() {
        when(pecaRepository.buscarPorId(pecaExistente.getId())).thenReturn(Optional.of(pecaExistente));
        when(pecaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        PecaOutput output = gerenciarEstoquePecaUseCase.reporEstoque(pecaExistente.getId(), 5);

        assertThat(output).isNotNull();
        assertThat(output.saldoEstoque()).isEqualTo(15);
    }

    @Test
    @DisplayName("Deve debitar estoque de peça com sucesso")
    void deveDebitarEstoqueComSucesso() {
        when(pecaRepository.buscarPorId(pecaExistente.getId())).thenReturn(Optional.of(pecaExistente));
        when(pecaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        PecaOutput output = gerenciarEstoquePecaUseCase.debitarEstoque(pecaExistente.getId(), 4);

        assertThat(output).isNotNull();
        assertThat(output.saldoEstoque()).isEqualTo(6);
    }

    @Test
    @DisplayName("Deve listar todas as peças e buscar por ID")
    void deveListarPecas() {
        when(pecaRepository.listarTodas()).thenReturn(List.of(pecaExistente));
        when(pecaRepository.buscarPorId(pecaExistente.getId())).thenReturn(Optional.of(pecaExistente));

        List<PecaOutput> lista = listarPecasUseCase.listarTodas();
        assertThat(lista).hasSize(1);

        PecaOutput porId = listarPecasUseCase.buscarPorId(pecaExistente.getId());
        assertThat(porId.codigo()).isEqualTo("FIL-01");

        UUID inexistente = UUID.randomUUID();
        when(pecaRepository.buscarPorId(inexistente)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listarPecasUseCase.buscarPorId(inexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar peças com estoque baixo")
    void deveListarPecasEstoqueBaixo() {
        when(pecaRepository.buscarComEstoqueAbaixoDoMinimo()).thenReturn(List.of(pecaExistente));

        List<PecaOutput> lista = listarPecasUseCase.listarAbaixoDoMinimo();

        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar e deletar peça com sucesso")
    void deveAtualizarEDeletarPeca() {
        when(pecaRepository.buscarPorId(pecaExistente.getId())).thenReturn(Optional.of(pecaExistente));
        when(pecaRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        PecaOutput atualizada = gerenciarEstoquePecaUseCase.atualizar(
                pecaExistente.getId(), "Filtro Sintético", "Nova desc", BigDecimal.valueOf(70.00), 5);
        assertThat(atualizada.nome()).isEqualTo("Filtro Sintético");
        assertThat(atualizada.valorUnitario()).isEqualByComparingTo(BigDecimal.valueOf(70.00));

        gerenciarEstoquePecaUseCase.deletar(pecaExistente.getId());
        verify(pecaRepository).deletar(pecaExistente.getId());

        UUID inexistente = UUID.randomUUID();
        when(pecaRepository.buscarPorId(inexistente)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> gerenciarEstoquePecaUseCase.atualizar(inexistente, "Nome", "Desc", BigDecimal.TEN, 1))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        assertThatThrownBy(() -> gerenciarEstoquePecaUseCase.deletar(inexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
