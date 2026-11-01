package br.com.fiap.soat.oficina.application.usecase.servico;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarServicoInput;
import br.com.fiap.soat.oficina.application.dto.response.ServicoOutput;
import br.com.fiap.soat.oficina.domain.entity.Servico;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
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
@DisplayName("Testes de Unidade - GerenciarServicoUseCase")
class ServicoUseCasesTest {

    @Mock
    private ServicoRepositoryPort servicoRepository;

    private GerenciarServicoUseCase gerenciarServicoUseCase;
    private Servico servicoExistente;

    @BeforeEach
    void setUp() {
        gerenciarServicoUseCase = new GerenciarServicoUseCase(servicoRepository);
        servicoExistente = Servico.criarNovo("SRV-01", "Troca de Óleo", "Mão de obra troca de óleo", Dinheiro.de(80.00));
    }

    @Test
    @DisplayName("Deve cadastrar serviço quando código for inédito")
    void deveCadastrarServicoComSucesso() {
        when(servicoRepository.existePorCodigo("SRV-01")).thenReturn(false);
        when(servicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        CadastrarServicoInput input = new CadastrarServicoInput("SRV-01", "Troca de Óleo", "Desc", BigDecimal.valueOf(80.00));
        ServicoOutput output = gerenciarServicoUseCase.cadastrar(input);

        assertThat(output).isNotNull();
        assertThat(output.codigo()).isEqualTo("SRV-01");
        verify(servicoRepository).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar serviço com código duplicado")
    void deveFalharCodigoDuplicado() {
        when(servicoRepository.existePorCodigo("SRV-01")).thenReturn(true);

        CadastrarServicoInput input = new CadastrarServicoInput("SRV-01", "Troca de Óleo", "Desc", BigDecimal.valueOf(80.00));

        assertThatThrownBy(() -> gerenciarServicoUseCase.cadastrar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("código");
    }

    @Test
    @DisplayName("Deve buscar serviço por ID e por código com sucesso")
    void deveBuscarPorIdECodigo() {
        when(servicoRepository.buscarPorId(servicoExistente.getId())).thenReturn(Optional.of(servicoExistente));
        when(servicoRepository.buscarPorCodigo("SRV-01")).thenReturn(Optional.of(servicoExistente));

        ServicoOutput porId = gerenciarServicoUseCase.buscarPorId(servicoExistente.getId());
        ServicoOutput porCodigo = gerenciarServicoUseCase.buscarPorCodigo("srv-01");

        assertThat(porId.id()).isEqualTo(servicoExistente.getId());
        assertThat(porCodigo.codigo()).isEqualTo("SRV-01");
    }

    @Test
    @DisplayName("Deve falhar ao buscar serviço inexistente por ID ou código")
    void deveFalharBuscarServicoInexistente() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.buscarPorId(id)).thenReturn(Optional.empty());
        when(servicoRepository.buscarPorCodigo("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gerenciarServicoUseCase.buscarPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        assertThatThrownBy(() -> gerenciarServicoUseCase.buscarPorCodigo("inexistente"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar todos os serviços")
    void deveListarServicos() {
        when(servicoRepository.listarTodos()).thenReturn(List.of(servicoExistente));

        List<ServicoOutput> lista = gerenciarServicoUseCase.listarTodos();

        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar e deletar serviço com sucesso")
    void deveAtualizarEDeletarServico() {
        when(servicoRepository.buscarPorId(servicoExistente.getId())).thenReturn(Optional.of(servicoExistente));
        when(servicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ServicoOutput atualizado = gerenciarServicoUseCase.atualizar(
                servicoExistente.getId(), "Troca de Óleo e Filtro", "Nova desc", BigDecimal.valueOf(110.00));

        assertThat(atualizado.nome()).isEqualTo("Troca de Óleo e Filtro");
        assertThat(atualizado.valorUnitario()).isEqualByComparingTo(BigDecimal.valueOf(110.00));

        gerenciarServicoUseCase.deletar(servicoExistente.getId());
        verify(servicoRepository).deletar(servicoExistente.getId());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar ou deletar serviço inexistente")
    void deveFalharAtualizarOuDeletarInexistente() {
        UUID id = UUID.randomUUID();
        when(servicoRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gerenciarServicoUseCase.atualizar(id, "Nome", "Desc", BigDecimal.TEN))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        assertThatThrownBy(() -> gerenciarServicoUseCase.deletar(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}

