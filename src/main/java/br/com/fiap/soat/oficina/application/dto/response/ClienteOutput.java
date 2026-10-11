package br.com.fiap.soat.oficina.application.dto.response;

import br.com.fiap.soat.oficina.domain.entity.Cliente;

import java.time.Instant;
import java.util.UUID;

public record ClienteOutput(
        UUID id,
        String nome,
        String documento,
        String documentoFormatado,
        String email,
        String telefone,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ClienteOutput deDominio(Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteOutput(
                cliente.getId(),
                cliente.getNome(),
                cliente.getDocumento().valor(),
                cliente.getDocumento().formatado(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getCriadoEm(),
                cliente.getAtualizadoEm()
        );
    }
}

