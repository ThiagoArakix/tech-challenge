package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Placa(String valor, PadraoPlaca padrao) {

    public enum PadraoPlaca {
        TRADICIONAL,
        MERCOSUL
    }

    private static final Pattern REGEX_TRADICIONAL = Pattern.compile("^[A-Z]{3}-?\\d{4}$");
    private static final Pattern REGEX_MERCOSUL = Pattern.compile("^[A-Z]{3}\\d[A-Z]\\d{2}$");

    public Placa {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("A placa do veículo não pode ser nula ou vazia");
        }

        String normalizada = valor.trim().toUpperCase();

        if (REGEX_MERCOSUL.matcher(normalizada).matches()) {
            padrao = PadraoPlaca.MERCOSUL;
            valor = normalizada;
        } else if (REGEX_TRADICIONAL.matcher(normalizada).matches()) {
            padrao = PadraoPlaca.TRADICIONAL;
            valor = normalizada.replace("-", "");
        } else {
            throw new DomainException(String.format(
                    "Placa '%s' é inválida. Deve seguir o padrão tradicional (ex: ABC-1234) ou Mercosul (ex: ABC1D23)", valor));
        }
    }

    public static Placa de(String valor) {
        return new Placa(valor, null);
    }

    public boolean isMercosul() {
        return this.padrao == PadraoPlaca.MERCOSUL;
    }

    public boolean isTradicional() {
        return this.padrao == PadraoPlaca.TRADICIONAL;
    }

    public String formatada() {
        if (isTradicional() && valor.length() == 7) {
            return valor.substring(0, 3) + "-" + valor.substring(3);
        }
        return valor;
    }

    @Override
    public String toString() {
        return formatada();
    }
}

