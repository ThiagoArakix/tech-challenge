package br.com.fiap.soat.oficina.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Unidade - Enum StatusOS (Máquina de Estados)")
class StatusOSTest {

    @Nested
    @DisplayName("Transições Permitidas no Ciclo de Vida da OS")
    class TransicoesPermitidas {

        @Test
        @DisplayName("RECEBIDA pode transicionar para EM_DIAGNOSTICO ou CANCELADA")
        void transicoesRecebida() {
            assertThat(StatusOS.RECEBIDA.podeTransicionarPara(StatusOS.EM_DIAGNOSTICO)).isTrue();
            assertThat(StatusOS.RECEBIDA.podeTransicionarPara(StatusOS.CANCELADA)).isTrue();
        }

        @Test
        @DisplayName("EM_DIAGNOSTICO pode transicionar para AGUARDANDO_APROVACAO ou CANCELADA")
        void transicoesEmDiagnostico() {
            assertThat(StatusOS.EM_DIAGNOSTICO.podeTransicionarPara(StatusOS.AGUARDANDO_APROVACAO)).isTrue();
            assertThat(StatusOS.EM_DIAGNOSTICO.podeTransicionarPara(StatusOS.CANCELADA)).isTrue();
        }

        @Test
        @DisplayName("AGUARDANDO_APROVACAO pode transicionar para EM_EXECUCAO ou CANCELADA")
        void transicoesAguardandoAprovacao() {
            assertThat(StatusOS.AGUARDANDO_APROVACAO.podeTransicionarPara(StatusOS.EM_EXECUCAO)).isTrue();
            assertThat(StatusOS.AGUARDANDO_APROVACAO.podeTransicionarPara(StatusOS.CANCELADA)).isTrue();
        }

        @Test
        @DisplayName("EM_EXECUCAO pode transicionar para FINALIZADA")
        void transicoesEmExecucao() {
            assertThat(StatusOS.EM_EXECUCAO.podeTransicionarPara(StatusOS.FINALIZADA)).isTrue();
        }

        @Test
        @DisplayName("FINALIZADA pode transicionar para ENTREGUE")
        void transicoesFinalizada() {
            assertThat(StatusOS.FINALIZADA.podeTransicionarPara(StatusOS.ENTREGUE)).isTrue();
        }
    }

    @Nested
    @DisplayName("Transições Inválidas e Estados Terminais")
    class TransicoesInvalidas {

        @Test
        @DisplayName("ENTREGUE não pode transicionar para nenhum estado (terminal)")
        void entregueEstadoTerminal() {
            for (StatusOS destino : StatusOS.values()) {
                assertThat(StatusOS.ENTREGUE.podeTransicionarPara(destino)).isFalse();
            }
        }

        @Test
        @DisplayName("CANCELADA não pode transicionar para nenhum estado (terminal)")
        void canceladaEstadoTerminal() {
            for (StatusOS destino : StatusOS.values()) {
                assertThat(StatusOS.CANCELADA.podeTransicionarPara(destino)).isFalse();
            }
        }

        @Test
        @DisplayName("Não deve permitir pular etapas da máquina de estados (ex: RECEBIDA para ENTREGUE)")
        void saltosInvalidos() {
            assertThat(StatusOS.RECEBIDA.podeTransicionarPara(StatusOS.EM_EXECUCAO)).isFalse();
            assertThat(StatusOS.RECEBIDA.podeTransicionarPara(StatusOS.FINALIZADA)).isFalse();
            assertThat(StatusOS.RECEBIDA.podeTransicionarPara(StatusOS.ENTREGUE)).isFalse();
            assertThat(StatusOS.EM_DIAGNOSTICO.podeTransicionarPara(StatusOS.FINALIZADA)).isFalse();
            assertThat(StatusOS.EM_EXECUCAO.podeTransicionarPara(StatusOS.ENTREGUE)).isFalse();
        }

        @Test
        @DisplayName("Não deve permitir retroceder etapas (ex: EM_EXECUCAO para EM_DIAGNOSTICO)")
        void retrocessosInvalidos() {
            assertThat(StatusOS.EM_DIAGNOSTICO.podeTransicionarPara(StatusOS.RECEBIDA)).isFalse();
            assertThat(StatusOS.AGUARDANDO_APROVACAO.podeTransicionarPara(StatusOS.EM_DIAGNOSTICO)).isFalse();
            assertThat(StatusOS.EM_EXECUCAO.podeTransicionarPara(StatusOS.AGUARDANDO_APROVACAO)).isFalse();
            assertThat(StatusOS.FINALIZADA.podeTransicionarPara(StatusOS.EM_EXECUCAO)).isFalse();
        }

        @ParameterizedTest(name = "Qualquer status com destino nulo deve retornar falso: {0}")
        @EnumSource(StatusOS.class)
        void transicaoParaNulo(StatusOS origem) {
            assertThat(origem.podeTransicionarPara(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Permissão de Edição de Itens da OS")
    class PermissaoEdicao {

        @Test
        @DisplayName("Apenas RECEBIDA e EM_DIAGNOSTICO devem permitir alteração de itens")
        void edicaoPermitidaApenasAteDiagnostico() {
            assertThat(StatusOS.RECEBIDA.permiteEdicaoItens()).isTrue();
            assertThat(StatusOS.EM_DIAGNOSTICO.permiteEdicaoItens()).isTrue();

            assertThat(StatusOS.AGUARDANDO_APROVACAO.permiteEdicaoItens()).isFalse();
            assertThat(StatusOS.EM_EXECUCAO.permiteEdicaoItens()).isFalse();
            assertThat(StatusOS.FINALIZADA.permiteEdicaoItens()).isFalse();
            assertThat(StatusOS.ENTREGUE.permiteEdicaoItens()).isFalse();
            assertThat(StatusOS.CANCELADA.permiteEdicaoItens()).isFalse();
        }
    }
}

