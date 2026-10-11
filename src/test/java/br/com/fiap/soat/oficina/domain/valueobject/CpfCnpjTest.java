package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Value Object CpfCnpj")
class CpfCnpjTest {

    @Nested
    @DisplayName("Validação de CPF")
    class ValidacaoCpf {

        @ParameterizedTest(name = "Deve aceitar CPF válido: {0}")
        @ValueSource(strings = {
                "52998224725",
                "529.982.247-25",
                "11144477735",
                "111.444.777-35",
                "85387431041",
                "853.874.310-41"
        })
        void deveCriarCpfValidoComSucesso(String valor) {
            CpfCnpj documento = CpfCnpj.de(valor);

            assertThat(documento).isNotNull();
            assertThat(documento.isCpf()).isTrue();
            assertThat(documento.isCnpj()).isFalse();
            assertThat(documento.tipo()).isEqualTo(CpfCnpj.TipoDocumento.CPF);
            assertThat(documento.valor()).doesNotContain(".", "-");
        }

        @ParameterizedTest(name = "Deve rejeitar CPF com dígitos iguais: {0}")
        @ValueSource(strings = {
                "00000000000",
                "11111111111",
                "222.222.222-22",
                "333.333.333-33",
                "999.999.999-99"
        })
        void deveRejeitarCpfComDigitosRepetidos(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Número de CPF inválido");
        }

        @ParameterizedTest(name = "Deve rejeitar CPF com dígitos verificadores incorretos: {0}")
        @ValueSource(strings = {
                "52998224720",
                "11144477739",
                "12345678901",
                "529.982.247-99"
        })
        void deveRejeitarCpfComDigitosVerificadoresIncorretos(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Número de CPF inválido");
        }

        @Test
        @DisplayName("Deve formatar CPF com máscara corretamente")
        void deveFormatarCpf() {
            CpfCnpj documento = CpfCnpj.de("52998224725");
            assertThat(documento.formatado()).isEqualTo("529.982.247-25");
        }
    }

    @Nested
    @DisplayName("Validação de CNPJ")
    class ValidacaoCnpj {

        @ParameterizedTest(name = "Deve aceitar CNPJ válido: {0}")
        @ValueSource(strings = {
                "11222333000181",
                "11.222.333/0001-81",
                "06990590000123",
                "06.990.590/0001-23",
                "19131243000197",
                "19.131.243/0001-97"
        })
        void deveCriarCnpjValidoComSucesso(String valor) {
            CpfCnpj documento = CpfCnpj.de(valor);

            assertThat(documento).isNotNull();
            assertThat(documento.isCnpj()).isTrue();
            assertThat(documento.isCpf()).isFalse();
            assertThat(documento.tipo()).isEqualTo(CpfCnpj.TipoDocumento.CNPJ);
            assertThat(documento.valor()).doesNotContain(".", "/", "-");
        }

        @ParameterizedTest(name = "Deve rejeitar CNPJ com dígitos iguais: {0}")
        @ValueSource(strings = {
                "00000000000000",
                "11111111111111",
                "22.222.222/2222-22"
        })
        void deveRejeitarCnpjComDigitosRepetidos(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Número de CNPJ inválido");
        }

        @ParameterizedTest(name = "Deve rejeitar CNPJ com dígitos verificadores incorretos: {0}")
        @ValueSource(strings = {
                "11222333000189",
                "06990590000100",
                "11.222.333/0001-00"
        })
        void deveRejeitarCnpjComDigitoInvalido(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Número de CNPJ inválido");
        }

        @Test
        @DisplayName("Deve formatar CNPJ com máscara corretamente")
        void deveFormatarCnpj() {
            CpfCnpj documento = CpfCnpj.de("11222333000181");
            assertThat(documento.formatado()).isEqualTo("11.222.333/0001-81");
        }
    }

    @Nested
    @DisplayName("Validações Gerais e Casos de Borda")
    class CasosDeBorda {

        @ParameterizedTest(name = "Deve rejeitar documento nulo ou vazio: ''{0}''")
        @ValueSource(strings = {"", "   ", "\t"})
        void deveRejeitarDocumentoVazio(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Documento de identificação (CPF/CNPJ) não pode ser nulo ou vazio");
        }

        @Test
        @DisplayName("Deve rejeitar valor nulo")
        void deveRejeitarDocumentoNulo() {
            assertThatThrownBy(() -> CpfCnpj.de(null))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Documento de identificação (CPF/CNPJ) não pode ser nulo ou vazio");
        }

        @ParameterizedTest(name = "Deve rejeitar documento com quantidade inválida de dígitos: {0}")
        @ValueSource(strings = {"123", "1234567890", "123456789012", "123456789012345"})
        void deveRejeitarQuantidadeInvalidaDigitos(String valor) {
            assertThatThrownBy(() -> CpfCnpj.de(valor))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Documento deve conter 11 dígitos para CPF ou 14 dígitos para CNPJ");
        }
    }
}

