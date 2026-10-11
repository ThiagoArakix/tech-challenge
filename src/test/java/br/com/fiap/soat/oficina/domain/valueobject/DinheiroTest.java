package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Value Object Dinheiro")
class DinheiroTest {

    @Test
    @DisplayName("Deve criar Dinheiro com arredondamento HALF_UP e escala de 2 casas decimais")
    void deveCriarDinheiroComEscalaCorreta() {
        Dinheiro d1 = Dinheiro.de(10.555);
        assertThat(d1.valor()).isEqualTo(new BigDecimal("10.56"));

        Dinheiro d2 = Dinheiro.de("100,50");
        assertThat(d2.valor()).isEqualTo(new BigDecimal("100.50"));

        Dinheiro d3 = Dinheiro.de(50L);
        assertThat(d3.valor()).isEqualTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Deve rejeitar valor monetário negativo")
    void deveRejeitarNegativo() {
        assertThatThrownBy(() -> Dinheiro.de(-1.0))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("não pode ser negativo");

        assertThatThrownBy(() -> Dinheiro.de(new BigDecimal("-0.01")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("não pode ser negativo");
    }

    @Test
    @DisplayName("Deve rejeitar valor nulo ou vazio")
    void deveRejeitarNuloOuVazio() {
        assertThatThrownBy(() -> new Dinheiro(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("não pode ser nulo");

        assertThatThrownBy(() -> Dinheiro.de(""))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Deve somar valores monetários corretamente")
    void deveSomarValores() {
        Dinheiro d1 = Dinheiro.de(50.25);
        Dinheiro d2 = Dinheiro.de(49.75);

        Dinheiro resultado = d1.somar(d2);

        assertThat(resultado.valor()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Deve subtrair valores e falhar se resultado for negativo")
    void deveSubtrairValores() {
        Dinheiro d1 = Dinheiro.de(100.00);
        Dinheiro d2 = Dinheiro.de(40.00);

        Dinheiro resultado = d1.subtrair(d2);
        assertThat(resultado.valor()).isEqualTo(new BigDecimal("60.00"));

        assertThatThrownBy(() -> d2.subtrair(d1))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Subtração resultaria em valor negativo");
    }

    @Test
    @DisplayName("Deve multiplicar por inteiro e por BigDecimal")
    void deveMultiplicarValores() {
        Dinheiro preco = Dinheiro.de(25.50);

        Dinheiro totalInt = preco.multiplicar(3);
        assertThat(totalInt.valor()).isEqualTo(new BigDecimal("76.50"));

        Dinheiro totalDec = preco.multiplicar(new BigDecimal("1.5"));
        assertThat(totalDec.valor()).isEqualTo(new BigDecimal("38.25"));

        assertThatThrownBy(() -> preco.multiplicar(-1))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> preco.multiplicar((BigDecimal) null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Deve comparar grandezas monetárias corretamente")
    void deveCompararValores() {
        Dinheiro menor = Dinheiro.de(10.00);
        Dinheiro maior = Dinheiro.de(20.00);
        Dinheiro zero = Dinheiro.ZERO;

        assertThat(maior.isMaiorQue(menor)).isTrue();
        assertThat(maior.isMaiorOuIgualA(menor)).isTrue();
        assertThat(menor.isMenorQue(maior)).isTrue();
        assertThat(zero.isZero()).isTrue();
        assertThat(maior.isZero()).isFalse();
    }
}

