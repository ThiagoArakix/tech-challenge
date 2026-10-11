package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - ItemServicoOrdem")
class ItemServicoOrdemTest {

    @Test
    @DisplayName("Deve criar item de serviço com sucesso e calcular valor total")
    void deveCriarItemServicoComSucesso() {
        ItemServicoOrdem item = ItemServicoOrdem.criarNovo("Alinhamento", BigDecimal.valueOf(1.5), Dinheiro.de(100.00));

        assertThat(item.getId()).isNotNull();
        assertThat(item.getDescricao()).isEqualTo("Alinhamento");
        assertThat(item.getQuantidade()).isEqualByComparingTo(BigDecimal.valueOf(1.5));
        assertThat(item.getValorUnitario()).isEqualTo(Dinheiro.de(100.00));
        assertThat(item.getValorTotal()).isEqualTo(Dinheiro.de(150.00));
        assertThat(item.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar item de serviço com quantidade inteira")
    void deveCriarItemComQuantidadeInteira() {
        ItemServicoOrdem item = ItemServicoOrdem.criarNovo("Balanceamento", 2, Dinheiro.de(50.00));

        assertThat(item.getValorTotal()).isEqualTo(Dinheiro.de(100.00));
    }

    @Test
    @DisplayName("Deve reconstituir com valores pré-existentes ou nulos")
    void deveReconstituirItem() {
        UUID id = UUID.randomUUID();
        Instant criadoEm = Instant.now();
        ItemServicoOrdem item = new ItemServicoOrdem(id, "Revisão", BigDecimal.ONE, Dinheiro.de(200.00), criadoEm);
        ItemServicoOrdem item2 = new ItemServicoOrdem(null, "Revisão", BigDecimal.ONE, Dinheiro.de(200.00), null);

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getCriadoEm()).isEqualTo(criadoEm);
        assertThat(item2.getId()).isNotNull();
        assertThat(item2.getCriadoEm()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("Deve rejeitar descrição inválida")
    void deveRejeitarDescricaoInvalida(String desc) {
        assertThatThrownBy(() -> ItemServicoOrdem.criarNovo(desc, 1, Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("descrição");
    }

    @Test
    @DisplayName("Deve rejeitar quantidade nula ou negativa")
    void deveRejeitarQuantidadeInvalida() {
        assertThatThrownBy(() -> ItemServicoOrdem.criarNovo("Troca de óleo", null, Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> ItemServicoOrdem.criarNovo("Troca de óleo", BigDecimal.ZERO, Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> ItemServicoOrdem.criarNovo("Troca de óleo", -1, Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Deve rejeitar valor unitário nulo")
    void deveRejeitarValorNulo() {
        assertThatThrownBy(() -> ItemServicoOrdem.criarNovo("Troca", 1, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve verificar igualdade e hash code")
    void deveVerificarIgualdade() {
        UUID id = UUID.randomUUID();
        ItemServicoOrdem i1 = new ItemServicoOrdem(id, "A", BigDecimal.ONE, Dinheiro.de(10.00), Instant.now());
        ItemServicoOrdem i2 = new ItemServicoOrdem(id, "B", BigDecimal.TEN, Dinheiro.de(20.00), Instant.now());
        ItemServicoOrdem i3 = ItemServicoOrdem.criarNovo("C", 1, Dinheiro.de(10.00));

        assertThat(i1).isEqualTo(i1);
        assertThat(i1).isEqualTo(i2);
        assertThat(i1.hashCode()).isEqualTo(i2.hashCode());
        assertThat(i1).isNotEqualTo(i3);
        assertThat(i1).isNotEqualTo(null);
        assertThat(i1).isNotEqualTo("outro");
    }
}

