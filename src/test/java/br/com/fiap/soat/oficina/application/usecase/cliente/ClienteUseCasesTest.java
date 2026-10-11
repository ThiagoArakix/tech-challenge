package br.com.fiap.soat.oficina.application.usecase.cliente;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarClienteInput;
import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Use Cases de Cliente")
class ClienteUseCasesTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    private CadastrarClienteUseCase cadastrarClienteUseCase;
    private BuscarClienteUseCase buscarClienteUseCase;
    private ListarClientesUseCase listarClientesUseCase;
    private AtualizarClienteUseCase atualizarClienteUseCase;

    private final String cpfStr = "52998224725";
    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        cadastrarClienteUseCase = new CadastrarClienteUseCase(clienteRepository);
        buscarClienteUseCase = new BuscarClienteUseCase(clienteRepository);
        listarClientesUseCase = new ListarClientesUseCase(clienteRepository);
        atualizarClienteUseCase = new AtualizarClienteUseCase(clienteRepository);

        clienteExistente = new Cliente(UUID.randomUUID(), "João Silva", CpfCnpj.de(cpfStr), "joao@email.com", "11999990000", Instant.now(), null);
    }

    @Test
    @DisplayName("Deve cadastrar cliente quando documento e email forem inéditos")
    void deveCadastrarClienteComSucesso() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.existePorEmail(any())).thenReturn(false);
        when(clienteRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CadastrarClienteInput input = new CadastrarClienteInput("João Silva", cpfStr, "joao@email.com", "11999990000");
        ClienteOutput output = cadastrarClienteUseCase.executar(input);

        assertThat(output).isNotNull();
        assertThat(output.nome()).isEqualTo("João Silva");
        verify(clienteRepository).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar cliente com documento duplicado")
    void deveFalharDocumentoDuplicado() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(true);

        CadastrarClienteInput input = new CadastrarClienteInput("João Silva", cpfStr, "novo@email.com", "11999990000");

        assertThatThrownBy(() -> cadastrarClienteUseCase.executar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("documento");
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar cliente com email duplicado")
    void deveFalharEmailDuplicado() {
        when(clienteRepository.existePorDocumento(any())).thenReturn(false);
        when(clienteRepository.existePorEmail("joao@email.com")).thenReturn(true);

        CadastrarClienteInput input = new CadastrarClienteInput("João Silva", "11144477735", "joao@email.com", "11999990000");

        assertThatThrownBy(() -> cadastrarClienteUseCase.executar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("e-mail");
    }

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpf() {
        when(clienteRepository.buscarPorDocumento(any())).thenReturn(Optional.of(clienteExistente));

        ClienteOutput output = buscarClienteUseCase.buscarPorDocumento(cpfStr);

        assertThat(output).isNotNull();
        assertThat(output.documento()).isEqualTo(cpfStr);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorId() {
        when(clienteRepository.buscarPorId(clienteExistente.getId())).thenReturn(Optional.of(clienteExistente));

        ClienteOutput output = buscarClienteUseCase.buscarPorId(clienteExistente.getId());

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(clienteExistente.getId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar cliente por CPF inexistente")
    void deveFalharBuscarCpfInexistente() {
        when(clienteRepository.buscarPorDocumento(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarClienteUseCase.buscarPorDocumento(cpfStr))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarClientes() {
        when(clienteRepository.listarTodos()).thenReturn(List.of(clienteExistente));

        List<ClienteOutput> lista = listarClientesUseCase.executar();

        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        when(clienteRepository.buscarPorId(clienteExistente.getId())).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.salvar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ClienteOutput output = atualizarClienteUseCase.executar(clienteExistente.getId(), "João Atualizado", "novo@email.com", "11988880000");

        assertThat(output.nome()).isEqualTo("João Atualizado");
        assertThat(output.email()).isEqualTo("novo@email.com");
    }

    @Test
    @DisplayName("Deve falhar ao atualizar cliente inexistente")
    void deveFalharAtualizarInexistente() {
        UUID id = UUID.randomUUID();
        when(clienteRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atualizarClienteUseCase.executar(id, "Nome", "email@test.com", "11999990000"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
