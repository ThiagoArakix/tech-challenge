package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Aggregate Root OrdemServico")
class OrdemServicoTest {

    private UUID clienteId;
    private UUID veiculoId;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();
        ordemServico = OrdemServico.abrirNova(clienteId, veiculoId, "Barulho na suspensão dianteira ao frear");
    }

    @Nested
    @DisplayName("Criação e Estado Inicial")
    class CriacaoEEstadoInicial {

        @Test
        @DisplayName("Deve inicializar no status RECEBIDA com listas vazias e total zero")
        void deveInicializarCorretamente() {
            assertThat(ordemServico.getId()).isNotNull();
            assertThat(ordemServico.getClienteId()).isEqualTo(clienteId);
            assertThat(ordemServico.getVeiculoId()).isEqualTo(veiculoId);
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.RECEBIDA);
            assertThat(ordemServico.getDescricaoProblema()).isEqualTo("Barulho na suspensão dianteira ao frear");
            assertThat(ordemServico.getDiagnostico()).isNull();
            assertThat(ordemServico.getItensServico()).isEmpty();
            assertThat(ordemServico.getItensPeca()).isEmpty();
            assertThat(ordemServico.getValorTotalServicos()).isEqualTo(Dinheiro.ZERO);
            assertThat(ordemServico.getValorTotalPecas()).isEqualTo(Dinheiro.ZERO);
            assertThat(ordemServico.getValorTotal()).isEqualTo(Dinheiro.ZERO);
            assertThat(ordemServico.getDataAbertura()).isNotNull();
        }

        @Test
        @DisplayName("Deve rejeitar criação sem descrição do problema")
        void deveRejeitarSemDescricao() {
            assertThatThrownBy(() -> OrdemServico.abrirNova(clienteId, veiculoId, "   "))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("descrição do problema");
        }
    }

    @Nested
    @DisplayName("Gestão de Itens e Imutabilidade de Coleções")
    class GestaoItens {

        @Test
        @DisplayName("Deve adicionar e remover serviços com cálculo automático do orçamento")
        void deveGerenciarServicos() {
            ItemServicoOrdem item1 = ItemServicoOrdem.criarNovo("Troca de pastilha de freio", 1, Dinheiro.de(150.00));
            ItemServicoOrdem item2 = ItemServicoOrdem.criarNovo("Alinhamento e balanceamento", 1, Dinheiro.de(120.00));

            ordemServico.adicionarItemServico(item1);
            ordemServico.adicionarItemServico(item2);

            assertThat(ordemServico.getItensServico()).hasSize(2);
            assertThat(ordemServico.getValorTotalServicos()).isEqualTo(Dinheiro.de(270.00));
            assertThat(ordemServico.getValorTotal()).isEqualTo(Dinheiro.de(270.00));

            ordemServico.removerItemServico(item2.getId());

            assertThat(ordemServico.getItensServico()).hasSize(1);
            assertThat(ordemServico.getValorTotalServicos()).isEqualTo(Dinheiro.de(150.00));
        }

        @Test
        @DisplayName("Deve adicionar e remover peças com cálculo automático do orçamento")
        void deveGerenciarPecas() {
            UUID pecaId1 = UUID.randomUUID();
            UUID pecaId2 = UUID.randomUUID();

            ItemPecaOrdem itemPeca1 = ItemPecaOrdem.criarNovo(pecaId1, 2, Dinheiro.de(80.00)); // 160.00
            ItemPecaOrdem itemPeca2 = ItemPecaOrdem.criarNovo(pecaId2, 1, Dinheiro.de(300.00)); // 300.00

            ordemServico.adicionarItemPeca(itemPeca1);
            ordemServico.adicionarItemPeca(itemPeca2);

            assertThat(ordemServico.getItensPeca()).hasSize(2);
            assertThat(ordemServico.getValorTotalPecas()).isEqualTo(Dinheiro.de(460.00));
            assertThat(ordemServico.getValorTotal()).isEqualTo(Dinheiro.de(460.00));

            ordemServico.removerItemPeca(itemPeca1.getId());

            assertThat(ordemServico.getItensPeca()).hasSize(1);
            assertThat(ordemServico.getValorTotalPecas()).isEqualTo(Dinheiro.de(300.00));
        }

        @Test
        @DisplayName("Deve calcular corretamente o orçamento composto por serviços e peças")
        void deveCalcularTotalGeralComposto() {
            ItemServicoOrdem servico = ItemServicoOrdem.criarNovo("Mão de obra suspensão", 2, Dinheiro.de(100.00)); // 200.00
            ItemPecaOrdem peca = ItemPecaOrdem.criarNovo(UUID.randomUUID(), 4, Dinheiro.de(50.00)); // 200.00

            ordemServico.adicionarItemServico(servico);
            ordemServico.adicionarItemPeca(peca);

            assertThat(ordemServico.getValorTotalServicos()).isEqualTo(Dinheiro.de(200.00));
            assertThat(ordemServico.getValorTotalPecas()).isEqualTo(Dinheiro.de(200.00));
            assertThat(ordemServico.getValorTotal()).isEqualTo(Dinheiro.de(400.00));
        }

        @Test
        @DisplayName("Listas de serviços e peças devem ser imutáveis contra alterações externas")
        void deveGarantirImutabilidadeColecoes() {
            ItemServicoOrdem servico = ItemServicoOrdem.criarNovo("Diagnóstico", 1, Dinheiro.de(50.00));
            ordemServico.adicionarItemServico(servico);

            assertThatThrownBy(() -> ordemServico.getItensServico().clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            assertThatThrownBy(() -> ordemServico.getItensPeca().add(ItemPecaOrdem.criarNovo(UUID.randomUUID(), 1, Dinheiro.de(10.00))))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Bloqueio de Edição de Itens Após Diagnóstico")
    class BloqueioEdicaoItens {

        @Test
        @DisplayName("Deve bloquear adição e remoção de itens quando a OS sai de diagnóstico")
        void deveBloquearAlteracoesAposDiagnostico() {
            ItemServicoOrdem servico = ItemServicoOrdem.criarNovo("Revisão de freios", 1, Dinheiro.de(100.00));
            ordemServico.adicionarItemServico(servico);

            ordemServico.iniciarDiagnostico();
            ordemServico.registrarDiagnostico("Pastilhas desgastadas");
            ordemServico.enviarParaAprovacao();

            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);

            ItemServicoOrdem novoServico = ItemServicoOrdem.criarNovo("Outro serviço", 1, Dinheiro.de(50.00));
            assertThatThrownBy(() -> ordemServico.adicionarItemServico(novoServico))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Não é permitido alterar itens da Ordem de Serviço após o diagnóstico");

            assertThatThrownBy(() -> ordemServico.removerItemServico(servico.getId()))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Não é permitido alterar itens da Ordem de Serviço após o diagnóstico");

            ItemPecaOrdem novaPeca = ItemPecaOrdem.criarNovo(UUID.randomUUID(), 1, Dinheiro.de(25.00));
            assertThatThrownBy(() -> ordemServico.adicionarItemPeca(novaPeca))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Não é permitido alterar itens da Ordem de Serviço após o diagnóstico");
        }
    }

    @Nested
    @DisplayName("Máquina de Estados e Fluxo Completo de Atendimento")
    class CicloDeVidaEAtendimento {

        @Test
        @DisplayName("Deve percorrer com sucesso todo o ciclo de vida feliz da OS")
        void deveExecutarCicloCompleto() {
            // 1. Recebida -> Diagnóstico
            ordemServico.iniciarDiagnostico();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.EM_DIAGNOSTICO);

            // 2. Registra diagnóstico e itens
            ordemServico.registrarDiagnostico("Amortecedores dianteiros estourados");
            ordemServico.adicionarItemServico(ItemServicoOrdem.criarNovo("Troca de amortecedores", 1, Dinheiro.de(200.00)));
            ordemServico.adicionarItemPeca(ItemPecaOrdem.criarNovo(UUID.randomUUID(), 2, Dinheiro.de(350.00)));

            // 3. Enviar para aprovação do cliente
            ordemServico.enviarParaAprovacao();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.AGUARDANDO_APROVACAO);

            // 4. Cliente aprova -> Em Execução
            ordemServico.aprovar();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.EM_EXECUCAO);
            assertThat(ordemServico.getDataAprovacao()).isNotNull();

            // 5. Mecânico finaliza serviço -> Finalizada
            ordemServico.finalizar();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.FINALIZADA);
            assertThat(ordemServico.getDataFinalizacao()).isNotNull();

            // 6. Veículo entregue ao cliente -> Entregue
            ordemServico.entregar();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.ENTREGUE);
            assertThat(ordemServico.getDataEntrega()).isNotNull();
        }

        @Test
        @DisplayName("Deve impedir transição inválida fora do fluxo da máquina de estados")
        void deveFalharEmTransicaoInvalida() {
            assertThatThrownBy(() -> ordemServico.transicionarPara(StatusOS.FINALIZADA))
                    .isInstanceOf(TransicaoStatusInvalidaException.class)
                    .hasMessageContaining("Transição de status inválida na Ordem de Serviço: de 'RECEBIDA' para 'FINALIZADA'");
        }

        @Test
        @DisplayName("Deve impedir envio para aprovação sem diagnóstico preenchido")
        void deveImpedirAprovacaoSemDiagnostico() {
            ordemServico.iniciarDiagnostico();
            ordemServico.adicionarItemServico(ItemServicoOrdem.criarNovo("Serviço", 1, Dinheiro.de(100.00)));

            assertThatThrownBy(() -> ordemServico.enviarParaAprovacao())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("sem registrar o diagnóstico técnico");
        }

        @Test
        @DisplayName("Deve impedir envio para aprovação sem itens orçados")
        void deveImpedirAprovacaoSemItens() {
            ordemServico.iniciarDiagnostico();
            ordemServico.registrarDiagnostico("Sem defeito aparente");

            assertThatThrownBy(() -> ordemServico.enviarParaAprovacao())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("sem ao menos um serviço ou peça");
        }

        @Test
        @DisplayName("Deve permitir cancelar a OS quando em fase inicial")
        void devePermitirCancelamento() {
            ordemServico.cancelar();
            assertThat(ordemServico.getStatus()).isEqualTo(StatusOS.CANCELADA);
        }

        @Test
        @DisplayName("Deve verificar igualdade e hash code de OrdemServico")
        void deveVerificarIgualdade() {
            UUID id = UUID.randomUUID();
            OrdemServico os1 = new OrdemServico(id, UUID.randomUUID(), UUID.randomUUID(), StatusOS.RECEBIDA, "Problema 1", null, null, null, null, null, null, null, null, null);
            OrdemServico os2 = new OrdemServico(id, UUID.randomUUID(), UUID.randomUUID(), StatusOS.EM_EXECUCAO, "Problema 2", null, null, null, null, null, null, null, null, null);
            OrdemServico os3 = OrdemServico.abrirNova(UUID.randomUUID(), UUID.randomUUID(), "Problema 3");

            assertThat(os1).isEqualTo(os1);
            assertThat(os1).isEqualTo(os2);
            assertThat(os1.hashCode()).isEqualTo(os2.hashCode());
            assertThat(os1).isNotEqualTo(os3);
            assertThat(os1).isNotEqualTo(null);
            assertThat(os1).isNotEqualTo("outro");
        }

        @Test
        @DisplayName("Deve validar parâmetros obrigatórios no construtor")
        void deveValidarParametrosObrigatorios() {
            UUID cId = UUID.randomUUID();
            UUID vId = UUID.randomUUID();

            assertThatThrownBy(() -> new OrdemServico(null, null, vId, null, "Desc", null, null, null, null, null, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> new OrdemServico(null, cId, null, null, "Desc", null, null, null, null, null, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> new OrdemServico(null, cId, vId, null, null, null, null, null, null, null, null, null, null, null))
                    .isInstanceOf(DomainException.class);

            assertThatThrownBy(() -> new OrdemServico(null, cId, vId, null, "   ", null, null, null, null, null, null, null, null, null))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("Deve obter datas do ciclo de vida")
        void deveObterDatasDoCicloDeVida() {
            assertThat(ordemServico.getDataAbertura()).isNotNull();
            assertThat(ordemServico.getCriadoEm()).isNotNull();
            assertThat(ordemServico.getDataAprovacao()).isNull();
            assertThat(ordemServico.getDataFinalizacao()).isNull();
            assertThat(ordemServico.getDataEntrega()).isNull();
            assertThat(ordemServico.getAtualizadoEm()).isNull();
        }
    }
}

