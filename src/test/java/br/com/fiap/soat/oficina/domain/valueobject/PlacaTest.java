package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Value Object Placa")
class PlacaTest {

    @Nested
    @DisplayName("Padrão Mercosul")
    class PadraoMercosul {

        @ParameterizedTest(name = "Deve aceitar placa Mercosul válida: {0}")
        @ValueSource(strings = {"ABC1D23", "bra2e19", "XYZ9Z99", "kmi8f02"})
        void deveCriarPlacaMercosulComSucesso(String valor) {
            Placa placa = Placa.de(valor);

            assertThat(placa).isNotNull();
            assertThat(placa.isMercosul()).isTrue();
            assertThat(placa.isTradicional()).isFalse();
            assertThat(placa.padrao()).isEqualTo(Placa.PadraoPlaca.MERCOSUL);
            assertThat(placa.valor()).isEqualTo(valor.trim().toUpperCase());
        }
    }

    @Nested
    @DisplayName("Padrão Tradicional")
    class PadraoTradicional {

        @ParameterizedTest(name = "Deve aceitar placa tradicional com e sem hífen: {0}")
        @ValueSource(strings = {"ABC-1234", "ABC1234", "xyz-9876", "kmi0001"})
        void deveCriarPlacaTradicionalComSucesso(String valor) {
            Placa placa = Placa.de(valor);

            assertThat(placa).isNotNull();
            assertThat(placa.isTradicional()).isTrue();
            assertThat(placa.isMercosul()).isFalse();
            assertThat(placa.padrao()).isEqualTo(Placa.PadraoPlaca.TRADICIONAL);
            assertThat(placa.formatada()).matches("^[A-Z]{3}-\\d{4}$");
        }

        @Test
        @DisplayName("Deve formatar com hífen ao chamar formatada()")
        void deveFormatarPlacaTradicional() {
            Placa placa = Placa.de("ABC1234");
            assertThat(placa.formatada()).isEqualTo("ABC-1234");
        }
    }

    @Nested
    @DisplayName("Placas Inválidas")
    class PlacasInvalidas {

        @ParameterizedTest(name = "Deve rejeitar placa com formato inválido: {0}")
        @ValueSource(strings = {
                "AB-1234",       // Poucas letras
                "ABCD-1234",     // Muitas letras
                "ABC-123",       // Poucos números
                "ABC-12345",     // Muitos números
                "123-ABCD",      // Invertido
                "ABC123D",       // Formato incorreto
                "ABC-1D23"       // Mercosul com hífen
        })
        void deveRejeitarPlacasInvalidas(String valor) {
            assertThatThrownBy(() -> Placa.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("é inválida");
        }

        @ParameterizedTest(name = "Deve rejeitar placa vazia: ''{0}''")
        @ValueSource(strings = {"", "   ", "\t"})
        void deveRejeitarPlacaVazia(String valor) {
            assertThatThrownBy(() -> Placa.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("A placa do veículo não pode ser nula ou vazia");
        }

        @Test
        @DisplayName("Deve rejeitar placa nula")
        void deveRejeitarPlacaNula() {
            assertThatThrownBy(() -> Placa.de(null))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("A placa do veículo não pode ser nula ou vazia");
        }
    }
}

