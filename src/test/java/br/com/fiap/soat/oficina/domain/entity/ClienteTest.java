package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Entidade Cliente")
class ClienteTest {

    private final CpfCnpj cpfValido = CpfCnpj.de("52998224725");

    @Nested
    @DisplayName("Criação de Cliente")
    class CriacaoCliente {

        @Test
        @DisplayName("Deve criar cliente com sucesso com todos os parâmetros válidos")
        void deveCriarClienteComSucesso() {
            Cliente cliente = Cliente.criarNovo("Carlos Silva", cpfValido, "carlos@email.com", "11988887777");

            assertThat(cliente.getId()).isNotNull();
            assertThat(cliente.getNome()).isEqualTo("Carlos Silva");
            assertThat(cliente.getDocumento()).isEqualTo(cpfValido);
            assertThat(cliente.getEmail()).isEqualTo("carlos@email.com");
            assertThat(cliente.getTelefone()).isEqualTo("11988887777");
            assertThat(cliente.getCriadoEm()).isNotNull();
            assertThat(cliente.getAtualizadoEm()).isNull();
        }

        @Test
        @DisplayName("Deve reconstituir cliente com id e datas pré-definidos")
        void deveReconstituirCliente() {
            UUID id = UUID.randomUUID();
            Instant criadoEm = Instant.now().minusSeconds(3600);
            Instant atualizadoEm = Instant.now();

            Cliente cliente = new Cliente(id, "Carlos Silva", cpfValido, "carlos@email.com", "11988887777", criadoEm, atualizadoEm);

            assertThat(cliente.getId()).isEqualTo(id);
            assertThat(cliente.getCriadoEm()).isEqualTo(criadoEm);
            assertThat(cliente.getAtualizadoEm()).isEqualTo(atualizadoEm);
        }

        @Test
        @DisplayName("Deve gerar id e criadoEm caso sejam nulos no construtor")
        void deveGerarIdECriadoEmQuandoNulos() {
            Cliente cliente = new Cliente(null, "Carlos Silva", cpfValido, "carlos@email.com", "11988887777", null, null);

            assertThat(cliente.getId()).isNotNull();
            assertThat(cliente.getCriadoEm()).isNotNull();
        }

        @Test
        @DisplayName("Deve falhar se documento for nulo")
        void deveFalharSeDocumentoNulo() {
            assertThatThrownBy(() -> Cliente.criarNovo("Carlos Silva", null, "carlos@email.com", "11988887777"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("documento");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Deve rejeitar cliente com nome nulo ou vazio")
        void deveRejeitarNomeNuloOuVazio(String nomeInvalido) {
            assertThatThrownBy(() -> Cliente.criarNovo(nomeInvalido, cpfValido, "carlos@email.com", "11988887777"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("nome do cliente não pode ser vazio ou nulo");
        }

        @Test
        @DisplayName("Deve rejeitar cliente com nome menor que 2 caracteres")
        void deveRejeitarNomeMuitoCurto() {
            assertThatThrownBy(() -> Cliente.criarNovo("A", cpfValido, "carlos@email.com", "11988887777"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("ao menos 2 caracteres");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "invalido", "email@", "@dominio.com", "carlos.com"})
        @DisplayName("Deve rejeitar cliente com e-mail inválido")
        void deveRejeitarEmailInvalido(String emailInvalido) {
            assertThatThrownBy(() -> Cliente.criarNovo("Carlos Silva", cpfValido, emailInvalido, "11988887777"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("E-mail inválido informado");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Deve rejeitar cliente com telefone nulo ou vazio")
        void deveRejeitarTelefoneNuloOuVazio(String telefoneInvalido) {
            assertThatThrownBy(() -> Cliente.criarNovo("Carlos Silva", cpfValido, "carlos@email.com", telefoneInvalido))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("telefone do cliente não pode ser vazio ou nulo");
        }
    }

    @Nested
    @DisplayName("Atualização de Cliente")
    class AtualizacaoCliente {

        @Test
        @DisplayName("Deve atualizar dados de contato com sucesso")
        void deveAtualizarDadosComSucesso() {
            Cliente cliente = Cliente.criarNovo("Carlos Silva", cpfValido, "carlos@email.com", "11988887777");

            cliente.atualizar("Carlos Eduardo Silva", "carloseduardo@email.com", "11977776666");

            assertThat(cliente.getNome()).isEqualTo("Carlos Eduardo Silva");
            assertThat(cliente.getEmail()).isEqualTo("carloseduardo@email.com");
            assertThat(cliente.getTelefone()).isEqualTo("11977776666");
            assertThat(cliente.getAtualizadoEm()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Igualdade e HashCode")
    class Igualdade {

        @Test
        @DisplayName("Deve considerar iguais clientes com mesmo ID")
        void deveCompararPorId() {
            UUID id = UUID.randomUUID();
            Cliente c1 = new Cliente(id, "Carlos", cpfValido, "carlos@email.com", "11988887777", Instant.now(), null);
            Cliente c2 = new Cliente(id, "Outro Nome", cpfValido, "outro@email.com", "11999990000", Instant.now(), null);
            Cliente c3 = Cliente.criarNovo("Carlos", cpfValido, "carlos@email.com", "11988887777");

            assertThat(c1).isEqualTo(c1);
            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
            assertThat(c1).isNotEqualTo(c3);
            assertThat(c1).isNotEqualTo(null);
            assertThat(c1).isNotEqualTo("String");
        }
    }
}

