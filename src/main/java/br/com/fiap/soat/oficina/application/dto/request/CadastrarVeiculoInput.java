package br.com.fiap.soat.oficina.application.dto.request;

import java.util.UUID;

public record CadastrarVeiculoInput(
        UUID clienteId,
        String placa,
        String marca,
        String modelo,
        int anoFabricacao,
        int anoModelo,
        String cor,
        int quilometragem
) {
}

