package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - ItemPecaOrdem")
class ItemPecaOrdemTest {

    private final UUID pecaId = UUID.randomUUID();

    @Test
    @DisplayName("Deve criar item de peça com sucesso e calcular total")
    void deveCriarItemPecaComSucesso() {
        ItemPecaOrdem item = ItemPecaOrdem.criarNovo(pecaId, 3, Dinheiro.de(40.00));

        assertThat(item.getId()).isNotNull();
        assertThat(item.getPecaId()).isEqualTo(pecaId);
        assertThat(item.getQuantidade()).isEqualTo(3);
        assertThat(item.getValorUnitario()).isEqualTo(Dinheiro.de(40.00));
        assertThat(item.getValorTotal()).isEqualTo(Dinheiro.de(120.00));
        assertThat(item.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve reconstituir com valores pré-definidos ou nulos")
    void deveReconstituirItem() {
        UUID id = UUID.randomUUID();
        Instant criadoEm = Instant.now();
        ItemPecaOrdem item = new ItemPecaOrdem(id, pecaId, 2, Dinheiro.de(50.00), criadoEm);
        ItemPecaOrdem item2 = new ItemPecaOrdem(null, pecaId, 2, Dinheiro.de(50.00), null);

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getCriadoEm()).isEqualTo(criadoEm);
        assertThat(item2.getId()).isNotNull();
        assertThat(item2.getCriadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve rejeitar pecaId nulo")
    void deveRejeitarPecaIdNulo() {
        assertThatThrownBy(() -> ItemPecaOrdem.criarNovo(null, 1, Dinheiro.de(10.00)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve rejeitar quantidade zero ou negativa")
    void deveRejeitarQuantidadeInvalida() {
        assertThatThrownBy(() -> ItemPecaOrdem.criarNovo(pecaId, 0, Dinheiro.de(10.00)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("quantidade");

        assertThatThrownBy(() -> ItemPecaOrdem.criarNovo(pecaId, -2, Dinheiro.de(10.00)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("quantidade");
    }

    @Test
    @DisplayName("Deve rejeitar valor unitário nulo")
    void deveRejeitarValorNulo() {
        assertThatThrownBy(() -> ItemPecaOrdem.criarNovo(pecaId, 1, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve verificar igualdade e hash code")
    void deveVerificarIgualdade() {
        UUID id = UUID.randomUUID();
        ItemPecaOrdem i1 = new ItemPecaOrdem(id, pecaId, 1, Dinheiro.de(10.00), Instant.now());
        ItemPecaOrdem i2 = new ItemPecaOrdem(id, UUID.randomUUID(), 5, Dinheiro.de(20.00), Instant.now());
        ItemPecaOrdem i3 = ItemPecaOrdem.criarNovo(pecaId, 1, Dinheiro.de(10.00));

        assertThat(i1).isEqualTo(i1);
        assertThat(i1).isEqualTo(i2);
        assertThat(i1.hashCode()).isEqualTo(i2.hashCode());
        assertThat(i1).isNotEqualTo(i3);
        assertThat(i1).isNotEqualTo(null);
        assertThat(i1).isNotEqualTo("outro");
    }
}

