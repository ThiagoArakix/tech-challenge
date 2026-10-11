package br.com.fiap.soat.oficina.domain.valueobject;

import br.com.fiap.soat.oficina.domain.exception.DomainException;

public record CpfCnpj(String valor, TipoDocumento tipo) {

    public enum TipoDocumento {
        CPF,
        CNPJ
    }

    public CpfCnpj {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("Documento de identificação (CPF/CNPJ) não pode ser nulo ou vazio");
        }

        String apenasNumeros = valor.replaceAll("\\D", "");

        if (apenasNumeros.length() == 11) {
            if (!validarCpf(apenasNumeros)) {
                throw new DomainException("Número de CPF inválido: " + valor);
            }
            tipo = TipoDocumento.CPF;
            valor = apenasNumeros;
        } else if (apenasNumeros.length() == 14) {
            if (!validarCnpj(apenasNumeros)) {
                throw new DomainException("Número de CNPJ inválido: " + valor);
            }
            tipo = TipoDocumento.CNPJ;
            valor = apenasNumeros;
        } else {
            throw new DomainException("Documento deve conter 11 dígitos para CPF ou 14 dígitos para CNPJ. Valor informado: " + valor);
        }
    }

    public static CpfCnpj de(String valor) {
        return new CpfCnpj(valor, null);
    }

    public boolean isCpf() {
        return this.tipo == TipoDocumento.CPF;
    }

    public boolean isCnpj() {
        return this.tipo == TipoDocumento.CNPJ;
    }

    public String formatado() {
        if (isCpf()) {
            return String.format("%s.%s.%s-%s",
                    valor.substring(0, 3),
                    valor.substring(3, 6),
                    valor.substring(6, 9),
                    valor.substring(9, 11));
        }
        return String.format("%s.%s.%s/%s-%s",
                valor.substring(0, 2),
                valor.substring(2, 5),
                valor.substring(5, 8),
                valor.substring(8, 12),
                valor.substring(12, 14));
    }

    private static boolean validarCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int somaPrimeiroDigito = 0;
            for (int i = 0; i < 9; i++) {
                somaPrimeiroDigito += (cpf.charAt(i) - '0') * (10 - i);
            }
            int restoPrimeiro = 11 - (somaPrimeiroDigito % 11);
            int primeiroDigitoVerificador = (restoPrimeiro >= 10) ? 0 : restoPrimeiro;

            if (primeiroDigitoVerificador != (cpf.charAt(9) - '0')) {
                return false;
            }

            int somaSegundoDigito = 0;
            for (int i = 0; i < 10; i++) {
                somaSegundoDigito += (cpf.charAt(i) - '0') * (11 - i);
            }
            int restoSegundo = 11 - (somaSegundoDigito % 11);
            int segundoDigitoVerificador = (restoSegundo >= 10) ? 0 : restoSegundo;

            return segundoDigitoVerificador == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean validarCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int somaPrimeiroDigito = 0;
            for (int i = 0; i < 12; i++) {
                somaPrimeiroDigito += (cnpj.charAt(i) - '0') * pesosPrimeiro[i];
            }
            int restoPrimeiro = somaPrimeiroDigito % 11;
            int primeiroDigitoVerificador = (restoPrimeiro < 2) ? 0 : (11 - restoPrimeiro);

            if (primeiroDigitoVerificador != (cnpj.charAt(12) - '0')) {
                return false;
            }

            int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int somaSegundoDigito = 0;
            for (int i = 0; i < 13; i++) {
                somaSegundoDigito += (cnpj.charAt(i) - '0') * pesosSegundo[i];
            }
            int restoSegundo = somaSegundoDigito % 11;
            int segundoDigitoVerificador = (restoSegundo < 2) ? 0 : (11 - restoSegundo);

            return segundoDigitoVerificador == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}

