package br.com.fiap.soat.oficina.application.dto.request;

import java.util.UUID;

public record AdicionarItemPecaInput(
        UUID pecaId,
        int quantidade
) {
}

