package br.com.fiap.soat.oficina.application.usecase.os;

import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.application.dto.response.StatusOSOutput;
import br.com.fiap.soat.oficina.domain.entity.ItemServicoOrdem;
import br.com.fiap.soat.oficina.domain.entity.OrdemServico;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.repository.OrdemServicoRepositoryPort;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Unidade - Atualizar e Consultar Status OS UseCases")
class AtualizarStatusOSUseCaseTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    private AtualizarStatusOSUseCase atualizarStatusUseCase;
    private ConsultarStatusOSUseCase consultarStatusUseCase;

    private OrdemServico os;

    @BeforeEach
    void setUp() {
        atualizarStatusUseCase = new AtualizarStatusOSUseCase(ordemServicoRepository);
        consultarStatusUseCase = new ConsultarStatusOSUseCase(ordemServicoRepository);

        os = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Problema no freio");
    }

    @Test
    @DisplayName("Deve iniciar e registrar diagnóstico com sucesso")
    void deveIniciarERegistrarDiagnostico() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput output1 = atualizarStatusUseCase.iniciarDiagnostico(os.getId());
        assertThat(output1.status()).isEqualTo(StatusOS.EM_DIAGNOSTICO);

        OrdemServicoOutput output2 = atualizarStatusUseCase.registrarDiagnostico(os.getId(), "Pastilhas gastas");
        assertThat(output2.diagnostico()).isEqualTo("Pastilhas gastas");
    }

    @Test
    @DisplayName("Deve submeter para aprovação após preenchimento de diagnóstico e itens")
    void deveSubmeterParaAprovacao() {
        os.iniciarDiagnostico();
        os.registrarDiagnostico("Pastilhas gastas");
        os.adicionarItemServico(ItemServicoOrdem.criarNovo("Troca de pastilhas", 1, Dinheiro.de(100.00)));

        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput output = atualizarStatusUseCase.submeterParaAprovacao(os.getId());
        assertThat(output.status()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);
    }

    @Test
    @DisplayName("Deve finalizar execução e registrar entrega")
    void deveFinalizarExecucaoERegistrarEntrega() {
        os.iniciarDiagnostico();
        os.registrarDiagnostico("Troca de pastilhas");
        os.adicionarItemServico(ItemServicoOrdem.criarNovo("Troca de pastilhas", 1, Dinheiro.de(100.00)));
        os.enviarParaAprovacao();
        os.aprovar();

        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput outputFinalizada = atualizarStatusUseCase.finalizarExecucao(os.getId());
        assertThat(outputFinalizada.status()).isEqualTo(StatusOS.FINALIZADA);

        OrdemServicoOutput outputEntregue = atualizarStatusUseCase.registrarEntrega(os.getId());
        assertThat(outputEntregue.status()).isEqualTo(StatusOS.ENTREGUE);
    }

    @Test
    @DisplayName("Deve cancelar a OS com sucesso")
    void deveCancelarOS() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));
        when(ordemServicoRepository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoOutput output = atualizarStatusUseCase.cancelar(os.getId());
        assertThat(output.status()).isEqualTo(StatusOS.CANCELADA);
    }

    @Test
    @DisplayName("Deve consultar status da OS com sucesso")
    void deveConsultarStatusOS() {
        when(ordemServicoRepository.buscarPorId(os.getId())).thenReturn(Optional.of(os));

        StatusOSOutput statusOutput = consultarStatusUseCase.executar(os.getId());
        assertThat(statusOutput).isNotNull();
        assertThat(statusOutput.status()).isEqualTo(StatusOS.RECEBIDA);
    }

    @Test
    @DisplayName("Deve falhar ao consultar status de OS inexistente")
    void deveFalharConsultarOSInexistente() {
        UUID id = UUID.randomUUID();
        when(ordemServicoRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultarStatusUseCase.executar(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}

