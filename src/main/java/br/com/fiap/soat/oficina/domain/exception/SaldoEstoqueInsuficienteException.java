package br.com.fiap.soat.oficina.domain.exception;

public class SaldoEstoqueInsuficienteException extends DomainException {

    private final int saldoAtual;
    private final int quantidadeSolicitada;

    public SaldoEstoqueInsuficienteException(int saldoAtual, int quantidadeSolicitada) {
        super(String.format("Saldo insuficiente em estoque: saldo atual é %d, mas foi solicitada a baixa de %d item(ns)",
                saldoAtual, quantidadeSolicitada));
        this.saldoAtual = saldoAtual;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public int getSaldoAtual() {
        return saldoAtual;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }
}

