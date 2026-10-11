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
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - CriarOrdemServicoUseCase")
class CriarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.criarNovo("Carlos Silva", CpfCnpj.de("52998224725"), "carlos@email.com", "11988887777");
        veiculo = Veiculo.criarNovo(cliente.getId(), Placa.de("ABC1D23"), "Fiat", "Uno", 2020, 2021, "Branco", 50000);
    }

    @Test
    @DisplayName("Deve criar Ordem de Serviço com sucesso quando veículo pertence ao cliente")
    void deveCriarOrdemServicoComSucesso() {
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculo.getId())).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CriarOrdemServicoInput input = new CriarOrdemServicoInput(cliente.getId(), veiculo.getId(), "Revisão preventiva de 50 mil km");
        OrdemServicoOutput output = useCase.executar(input);

        assertThat(output).isNotNull();
        assertThat(output.clienteId()).isEqualTo(cliente.getId());
        assertThat(output.veiculoId()).isEqualTo(veiculo.getId());
        assertThat(output.status()).isEqualTo(StatusOS.RECEBIDA);
        assertThat(output.descricaoProblema()).isEqualTo("Revisão preventiva de 50 mil km");

        verify(ordemServicoRepository).salvar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar OS para veículo que não pertence ao cliente informado")
    void deveFalharQuandoVeiculoNaoPertenceAoCliente() {
        UUID outroClienteId = UUID.randomUUID();
        when(clienteRepository.buscarPorId(outroClienteId)).thenReturn(Optional.of(
                Cliente.criarNovo("Outro Cliente", CpfCnpj.de("11144477735"), "outro@email.com", "11999998888")));
        when(veiculoRepository.buscarPorId(veiculo.getId())).thenReturn(Optional.of(veiculo));

        CriarOrdemServicoInput input = new CriarOrdemServicoInput(outroClienteId, veiculo.getId(), "Troca de óleo");

        assertThatThrownBy(() -> useCase.executar(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("não pertence ao cliente informado");
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando cliente não for encontrado")
    void deveFalharQuandoClienteNaoExiste() {
        UUID clienteInexistente = UUID.randomUUID();
        when(clienteRepository.buscarPorId(clienteInexistente)).thenReturn(Optional.empty());

        CriarOrdemServicoInput input = new CriarOrdemServicoInput(clienteInexistente, veiculo.getId(), "Revisão");

        assertThatThrownBy(() -> useCase.executar(input))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Cliente");
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando veículo não for encontrado")
    void deveFalharQuandoVeiculoNaoExiste() {
        UUID veiculoInexistente = UUID.randomUUID();
        when(clienteRepository.buscarPorId(cliente.getId())).thenReturn(Optional.of(cliente));
        when(veiculoRepository.buscarPorId(veiculoInexistente)).thenReturn(Optional.empty());

        CriarOrdemServicoInput input = new CriarOrdemServicoInput(cliente.getId(), veiculoInexistente, "Revisão");

        assertThatThrownBy(() -> useCase.executar(input))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Veículo");
    }
}

