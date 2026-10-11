package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;
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

@DisplayName("Testes de Unidade - Entidade Veiculo")
class VeiculoTest {

    private final UUID clienteId = UUID.randomUUID();
    private final Placa placaValida = Placa.de("ABC1D23");

    @Nested
    @DisplayName("Criação de Veículo")
    class CriacaoVeiculo {

        @Test
        @DisplayName("Deve criar veículo com sucesso com parâmetros válidos")
        void deveCriarVeiculoComSucesso() {
            Veiculo veiculo = Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000);

            assertThat(veiculo.getId()).isNotNull();
            assertThat(veiculo.getClienteId()).isEqualTo(clienteId);
            assertThat(veiculo.getPlaca()).isEqualTo(placaValida);
            assertThat(veiculo.getMarca()).isEqualTo("Honda");
            assertThat(veiculo.getModelo()).isEqualTo("Civic");
            assertThat(veiculo.getAnoFabricacao()).isEqualTo(2021);
            assertThat(veiculo.getAnoModelo()).isEqualTo(2022);
            assertThat(veiculo.getCor()).isEqualTo("Prata");
            assertThat(veiculo.getQuilometragem()).isEqualTo(25000);
            assertThat(veiculo.getCriadoEm()).isNotNull();
            assertThat(veiculo.getAtualizadoEm()).isNull();
        }

        @Test
        @DisplayName("Deve reconstituir veículo com id e datas pré-definidos")
        void deveReconstituirVeiculo() {
            UUID id = UUID.randomUUID();
            Instant criadoEm = Instant.now().minusSeconds(3600);
            Instant atualizadoEm = Instant.now();

            Veiculo veiculo = new Veiculo(id, clienteId, placaValida, "Honda", "Civic",
                    2021, 2022, "Prata", 25000, criadoEm, atualizadoEm);

            assertThat(veiculo.getId()).isEqualTo(id);
            assertThat(veiculo.getCriadoEm()).isEqualTo(criadoEm);
            assertThat(veiculo.getAtualizadoEm()).isEqualTo(atualizadoEm);
        }

        @Test
        @DisplayName("Deve gerar id e criadoEm caso sejam nulos no construtor")
        void deveGerarIdECriadoEmQuandoNulos() {
            Veiculo veiculo = new Veiculo(null, clienteId, placaValida, "Honda", "Civic",
                    2021, 2022, "Prata", 25000, null, null);

            assertThat(veiculo.getId()).isNotNull();
            assertThat(veiculo.getCriadoEm()).isNotNull();
        }

        @Test
        @DisplayName("Deve falhar se clienteId for nulo")
        void deveFalharSeClienteIdNulo() {
            assertThatThrownBy(() -> Veiculo.criarNovo(null, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("cliente");
        }

        @Test
        @DisplayName("Deve falhar se placa for nula")
        void deveFalharSePlacaNula() {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, null, "Honda", "Civic", 2021, 2022, "Prata", 25000))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("placa");
        }

        @Test
        @DisplayName("Deve rejeitar ano de fabricação menor ou igual a 1900")
        void deveRejeitarAnoFabricacaoInvalido() {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, placaValida, "Ford", "T", 1900, 1900, "Preto", 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Ano de fabricação");
        }

        @Test
        @DisplayName("Deve rejeitar ano do modelo anterior ao ano de fabricação")
        void deveRejeitarAnoModeloAnteriorAoDeFabricacao() {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2022, 2021, "Preto", 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Ano do modelo");
        }

        @Test
        @DisplayName("Deve rejeitar quilometragem negativa")
        void deveRejeitarQuilometragemNegativa() {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Preto", -50))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("quilometragem não pode ser negativa");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Deve rejeitar marca nula ou vazia")
        void deveRejeitarMarcaInvalida(String marcaInvalida) {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, placaValida, marcaInvalida, "Civic", 2021, 2022, "Preto", 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("marca");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("Deve rejeitar modelo nulo ou vazio")
        void deveRejeitarModeloInvalido(String modeloInvalido) {
            assertThatThrownBy(() -> Veiculo.criarNovo(clienteId, placaValida, "Honda", modeloInvalido, 2021, 2022, "Preto", 0))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("modelo");
        }
    }

    @Nested
    @DisplayName("Atualizações de Veículo")
    class AtualizacoesVeiculo {

        @Test
        @DisplayName("Deve atualizar quilometragem com sucesso quando for maior ou igual")
        void deveAtualizarQuilometragemComSucesso() {
            Veiculo veiculo = Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000);

            veiculo.atualizarQuilometragem(30000);

            assertThat(veiculo.getQuilometragem()).isEqualTo(30000);
            assertThat(veiculo.getAtualizadoEm()).isNotNull();
        }

        @Test
        @DisplayName("Deve rejeitar quilometragem inferior à registrada anteriormente")
        void deveRejeitarQuilometragemRegressiva() {
            Veiculo veiculo = Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000);

            assertThatThrownBy(() -> veiculo.atualizarQuilometragem(24999))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("inferior à registrada anteriormente");
        }

        @Test
        @DisplayName("Deve atualizar dados cadastrais do veículo")
        void deveAtualizarDadosDoVeiculo() {
            Veiculo veiculo = Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000);

            veiculo.atualizarDados("Honda", "Civic Touring", "Branco");

            assertThat(veiculo.getModelo()).isEqualTo("Civic Touring");
            assertThat(veiculo.getCor()).isEqualTo("Branco");
            assertThat(veiculo.getAtualizadoEm()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Igualdade e HashCode")
    class Igualdade {

        @Test
        @DisplayName("Deve considerar iguais veículos com mesmo ID")
        void deveCompararPorId() {
            UUID id = UUID.randomUUID();
            Veiculo v1 = new Veiculo(id, clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000, Instant.now(), null);
            Veiculo v2 = new Veiculo(id, clienteId, placaValida, "Toyota", "Corolla", 2021, 2022, "Preto", 30000, Instant.now(), null);
            Veiculo v3 = Veiculo.criarNovo(clienteId, placaValida, "Honda", "Civic", 2021, 2022, "Prata", 25000);

            assertThat(v1).isEqualTo(v1);
            assertThat(v1).isEqualTo(v2);
            assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
            assertThat(v1).isNotEqualTo(v3);
            assertThat(v1).isNotEqualTo(null);
            assertThat(v1).isNotEqualTo("String");
        }
    }
}

