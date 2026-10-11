package br.com.fiap.soat.oficina.application.usecase.veiculo;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarVeiculoInput;
import br.com.fiap.soat.oficina.application.dto.response.VeiculoOutput;
import br.com.fiap.soat.oficina.domain.entity.Cliente;
import br.com.fiap.soat.oficina.domain.entity.Veiculo;
import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.ClienteRepositoryPort;
import br.com.fiap.soat.oficina.domain.repository.VeiculoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
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
@DisplayName("Testes de Unidade - Use Cases de Veículo")
class VeiculoUseCasesTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private BuscarVeiculoUseCase buscarVeiculoUseCase;

    private final UUID clienteId = UUID.randomUUID();
    private Veiculo veiculoExistente;

    @BeforeEach
    void setUp() {
        cadastrarVeiculoUseCase = new CadastrarVeiculoUseCase(veiculoRepository, clienteRepository);
        buscarVeiculoUseCase = new BuscarVeiculoUseCase(veiculoRepository);

        veiculoExistente = new Veiculo(UUID.randomUUID(), clienteId, Placa.de("ABC1D23"), "Honda", "Civic",
                2021, 2022, "Prata", 20000, Instant.now(), null);
    }

    @Test
    @DisplayName("Deve cadastrar veículo com sucesso quando cliente existir e placa for inédita")
    void deveCadastrarVeiculoComSucesso() {
        Cliente cliente = new Cliente(clienteId, "João", CpfCnpj.de("52998224725"), "joao@email.com", "11999990000", Instant.now(), null);
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.existePorPlaca(any())).thenReturn(false);
        when(veiculoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        CadastrarVeiculoInput input = new CadastrarVeiculoInput(clienteId, "ABC1D23", "Honda", "Civic", 2021, 2022, "Prata", 20000);
        VeiculoOutput output = cadastrarVeiculoUseCase.executar(input);

        assertThat(output).isNotNull();
        assertThat(output.placa()).isEqualTo("ABC1D23");
        verify(veiculoRepository).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar veículo para cliente inexistente")
    void deveFalharClienteInexistente() {
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.empty());

        CadastrarVeiculoInput input = new CadastrarVeiculoInput(clienteId, "ABC1D23", "Honda", "Civic", 2021, 2022, "Prata", 20000);

        assertThatThrownBy(() -> cadastrarVeiculoUseCase.executar(input))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar veículo com placa duplicada")
    void deveFalharPlacaDuplicada() {
        Cliente cliente = new Cliente(clienteId, "João", CpfCnpj.de("52998224725"), "joao@email.com", "11999990000", Instant.now(), null);
        when(clienteRepository.buscarPorId(clienteId)).thenReturn(Optional.of(cliente));
        when(veiculoRepository.existePorPlaca(any())).thenReturn(true);

        CadastrarVeiculoInput input = new CadastrarVeiculoInput(clienteId, "ABC1D23", "Honda", "Civic", 2021, 2022, "Prata", 20000);

        assertThatThrownBy(() -> cadastrarVeiculoUseCase.executar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("placa");
    }

    @Test
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void deveBuscarVeiculoPorPlaca() {
        when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.of(veiculoExistente));

        VeiculoOutput output = buscarVeiculoUseCase.buscarPorPlaca("ABC1D23");

        assertThat(output).isNotNull();
        assertThat(output.placa()).isEqualTo("ABC1D23");
    }

    @Test
    @DisplayName("Deve buscar veículo por id com sucesso")
    void deveBuscarVeiculoPorId() {
        when(veiculoRepository.buscarPorId(veiculoExistente.getId())).thenReturn(Optional.of(veiculoExistente));

        VeiculoOutput output = buscarVeiculoUseCase.buscarPorId(veiculoExistente.getId());

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(veiculoExistente.getId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar veículo por placa inexistente")
    void deveFalharBuscarPlacaInexistente() {
        when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buscarVeiculoUseCase.buscarPorPlaca("ABC1D23"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar veículos por cliente")
    void deveListarVeiculosPorCliente() {
        when(veiculoRepository.buscarPorClienteId(clienteId)).thenReturn(List.of(veiculoExistente));

        List<VeiculoOutput> lista = buscarVeiculoUseCase.listarPorCliente(clienteId);

        assertThat(lista).hasSize(1);
    }
}
