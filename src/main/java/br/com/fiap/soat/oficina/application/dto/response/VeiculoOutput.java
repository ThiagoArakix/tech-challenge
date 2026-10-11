package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.Veiculo;

import java.time.Instant;
import java.util.UUID;

public record VeiculoOutput(
        UUID id,
        UUID clienteId,
        String placa,
        String placaFormatada,
        String marca,
        String modelo,
        int anoFabricacao,
        int anoModelo,
        String cor,
        int quilometragem,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static VeiculoOutput deDominio(Veiculo veiculo) {
        if (veiculo == null) return null;
        return new VeiculoOutput(
                veiculo.getId(),
                veiculo.getClienteId(),
                veiculo.getPlaca().valor(),
                veiculo.getPlaca().formatada(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAnoFabricacao(),
                veiculo.getAnoModelo(),
                veiculo.getCor(),
                veiculo.getQuilometragem(),
                veiculo.getCriadoEm(),
                veiculo.getAtualizadoEm()
        );
    }
}

