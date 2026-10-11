package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Dinheiro(BigDecimal valor) implements Comparable<Dinheiro> {

    private static final int ESCALA = 2;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_UP;
    public static final Dinheiro ZERO = new Dinheiro(BigDecimal.ZERO);

    public Dinheiro {
        if (valor == null) {
            throw new DomainException("O valor monetário não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("O valor monetário não pode ser negativo: " + valor);
        }
        valor = valor.setScale(ESCALA, ARREDONDAMENTO);
    }

    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public static Dinheiro de(double valor) {
        return new Dinheiro(BigDecimal.valueOf(valor));
    }

    public static Dinheiro de(long valor) {
        return new Dinheiro(BigDecimal.valueOf(valor));
    }

    public static Dinheiro de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("O valor monetário não pode ser vazio ou nulo");
        }
        try {
            return new Dinheiro(new BigDecimal(valor.trim().replace(",", ".")));
        } catch (NumberFormatException e) {
            throw new DomainException("Formato monetário inválido: " + valor, e);
        }
    }

    public Dinheiro somar(Dinheiro outro) {
        Objects.requireNonNull(outro, "O valor monetário para soma não pode ser nulo");
        return new Dinheiro(this.valor.add(outro.valor));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        Objects.requireNonNull(outro, "O valor monetário para subtração não pode ser nulo");
        if (this.valor.compareTo(outro.valor) < 0) {
            throw new DomainException("Subtração resultaria em valor negativo. Saldo atual: " + this.valor + ", solicitado: " + outro.valor);
        }
        return new Dinheiro(this.valor.subtract(outro.valor));
    }

    public Dinheiro multiplicar(int quantidade) {
        if (quantidade < 0) {
            throw new DomainException("O fator multiplicador não pode ser negativo: " + quantidade);
        }
        return new Dinheiro(this.valor.multiply(BigDecimal.valueOf(quantidade)));
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        if (fator == null || fator.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("O fator multiplicador não pode ser nulo ou negativo: " + fator);
        }
        return new Dinheiro(this.valor.multiply(fator));
    }

    public boolean isMaiorQue(Dinheiro outro) {
        return compareTo(outro) > 0;
    }

    public boolean isMaiorOuIgualA(Dinheiro outro) {
        return compareTo(outro) >= 0;
    }

    public boolean isMenorQue(Dinheiro outro) {
        return compareTo(outro) < 0;
    }

    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public int compareTo(Dinheiro outro) {
        Objects.requireNonNull(outro, "Não é possível comparar com valor nulo");
        return this.valor.compareTo(outro.valor);
    }

    @Override
    public String toString() {
        return "R$ " + valor.toString();
    }
}

