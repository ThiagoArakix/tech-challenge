package br.com.fiap.soat.oficina.application.dto.request;

public record CadastrarClienteInput(
        String nome,
        String documento,
        String email,
        String telefone
) {
}

