package br.com.fiap.soat.oficina.domain.exception;

import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

public class TransicaoStatusInvalidaException extends DomainException {

    private final StatusOS statusAtual;
    private final StatusOS statusDestino;

    public TransicaoStatusInvalidaException(StatusOS statusAtual, StatusOS statusDestino) {
        super(String.format("Transição de status inválida na Ordem de Serviço: de '%s' para '%s'", statusAtual, statusDestino));
        this.statusAtual = statusAtual;
        this.statusDestino = statusDestino;
    }

    public StatusOS getStatusAtual() {
        return statusAtual;
    }

    public StatusOS getStatusDestino() {
        return statusDestino;
    }
}

