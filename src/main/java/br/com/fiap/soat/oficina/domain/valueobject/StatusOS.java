package br.com.fiap.soat.oficina.domain.valueobject;

public enum StatusOS {
    RECEBIDA,
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE,
    CANCELADA;

    public boolean podeTransicionarPara(StatusOS proximo) {
        if (proximo == null) {
            return false;
        }

        return switch (this) {
            case RECEBIDA -> proximo == EM_DIAGNOSTICO || proximo == CANCELADA;
            case EM_DIAGNOSTICO -> proximo == AGUARDANDO_APROVACAO || proximo == CANCELADA;
            case AGUARDANDO_APROVACAO -> proximo == EM_EXECUCAO || proximo == CANCELADA;
            case EM_EXECUCAO -> proximo == FINALIZADA;
            case FINALIZADA -> proximo == ENTREGUE;
            case ENTREGUE, CANCELADA -> false;
        };
    }

    public boolean permiteEdicaoItens() {
        return this == RECEBIDA || this == EM_DIAGNOSTICO;
    }

    public boolean isFinalizada() {
        return this == FINALIZADA;
    }

    public boolean isEntregue() {
        return this == ENTREGUE;
    }

    public boolean isCancelada() {
        return this == CANCELADA;
    }
}

