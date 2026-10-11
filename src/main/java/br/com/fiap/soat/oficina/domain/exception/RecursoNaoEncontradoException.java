package br.com.fiap.soat.oficina.domain.exception;

public class RecursoNaoEncontradoException extends DomainException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }

    public static RecursoNaoEncontradoException de(String entidade, Object id) {
        return new RecursoNaoEncontradoException(String.format("%s com identificador '%s' não foi encontrado(a)", entidade, id));
    }
}

