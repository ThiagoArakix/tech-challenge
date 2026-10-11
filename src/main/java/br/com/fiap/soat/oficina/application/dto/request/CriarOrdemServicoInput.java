package br.com.fiap.soat.oficina.application.dto.request;

import java.util.UUID;

public record CriarOrdemServicoInput(
        UUID clienteId,
        UUID veiculoId,
        String descricaoProblema
) {
}

