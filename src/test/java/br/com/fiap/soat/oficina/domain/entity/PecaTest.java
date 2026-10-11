package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Entidade Peca")
class PecaTest {

    private Peca peca;

    @BeforeEach
    void setUp() {
        peca = Peca.criarNova("FIL-001", "Filtro de Óleo", "Filtro de óleo para motor 1.0/1.6",
                Dinheiro.de(45.00), 10, 3);
    }

    @Test
    @DisplayName("Deve debitar estoque com sucesso quando saldo for suficiente")
    void deveDebitarEstoqueComSucesso() {
        peca.debitarEstoque(4);

        assertThat(peca.getSaldoEstoque()).isEqualTo(6);
        assertThat(peca.isEstoqueAbaixoDoMinimo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar SaldoEstoqueInsuficienteException quando débito for maior que saldo")
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        assertThatThrownBy(() -> peca.debitarEstoque(15))
                .isInstanceOf(SaldoEstoqueInsuficienteException.class)
                .hasMessageContaining("Saldo insuficiente em estoque: saldo atual é 10, mas foi solicitada a baixa de 15");

        assertThat(peca.getSaldoEstoque()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve repor estoque corretamente")
    void deveReporEstoqueComSucesso() {
        peca.reporEstoque(5);

        assertThat(peca.getSaldoEstoque()).isEqualTo(15);
    }

    @Test
    @DisplayName("Deve identificar quando estoque está abaixo do mínimo")
    void deveIdentificarEstoqueAbaixoDoMinimo() {
        peca.debitarEstoque(8);

        assertThat(peca.getSaldoEstoque()).isEqualTo(2);
        assertThat(peca.isEstoqueAbaixoDoMinimo()).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar operações com quantidades menores ou iguais a zero")
    void deveRejeitarQuantidadesInvalidas() {
        assertThatThrownBy(() -> peca.debitarEstoque(0))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> peca.debitarEstoque(-2))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> peca.reporEstoque(0))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> peca.reporEstoque(-5))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Deve validar campos no construtor de Peca")
    void deveValidarCamposNoConstrutor() {
        assertThatThrownBy(() -> Peca.criarNova(null, "Nome", "Desc", Dinheiro.de(10.00), 10, 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("  ", "Nome", "Desc", Dinheiro.de(10.00), 10, 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("COD", null, "Desc", Dinheiro.de(10.00), 10, 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("COD", "   ", "Desc", Dinheiro.de(10.00), 10, 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("COD", "Nome", "Desc", Dinheiro.de(10.00), -1, 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("COD", "Nome", "Desc", Dinheiro.de(10.00), 10, -1))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Peca.criarNova("COD", "Nome", "Desc", null, 10, 5))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve atualizar preço e dados cadastrais")
    void deveAtualizarPrecoEDados() {
        peca.atualizarPreco(Dinheiro.de(55.00));
        assertThat(peca.getValorUnitario()).isEqualTo(Dinheiro.de(55.00));
        assertThat(peca.getAtualizadoEm()).isNotNull();

        assertThatThrownBy(() -> peca.atualizarPreco(null))
                .isInstanceOf(NullPointerException.class);

        peca.atualizarDados("Filtro Novo", "Nova Descrição", 5);
        assertThat(peca.getNome()).isEqualTo("Filtro Novo");
        assertThat(peca.getDescricao()).isEqualTo("Nova Descrição");
        assertThat(peca.getEstoqueMinimo()).isEqualTo(5);

        assertThatThrownBy(() -> peca.atualizarDados(null, "Desc", 5))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> peca.atualizarDados("Nome", "Desc", -1))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Deve verificar igualdade e hash code de Peca")
    void deveVerificarIgualdade() {
        UUID id = UUID.randomUUID();
        Peca p1 = new Peca(id, "COD-1", "Nome 1", null, Dinheiro.de(10.00), 5, 2, null, null);
        Peca p2 = new Peca(id, "COD-2", "Nome 2", null, Dinheiro.de(20.00), 10, 3, null, null);
        Peca p3 = Peca.criarNova("COD-3", "Nome 3", null, Dinheiro.de(10.00), 5, 2);

        assertThat(p1).isEqualTo(p1);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo("outro");

        assertThat(p1.getId()).isEqualTo(id);
        assertThat(p1.getCodigo()).isEqualTo("COD-1");
        assertThat(p1.getCriadoEm()).isNotNull();
    }
}

