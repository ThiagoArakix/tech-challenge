package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização cadastral do cliente")
public record AtualizarClienteRequest(
        @Schema(description = "Nome atualizado do cliente", example = "Mariana Costa Silva")
        @NotBlank(message = "O nome do cliente é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve conter entre 2 e 150 caracteres")
        String nome,

        @Schema(description = "Novo e-mail de contato", example = "mariana.silva@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        String email,

        @Schema(description = "Novo telefone de contato", example = "11988889999")
        @NotBlank(message = "O telefone é obrigatório")
        String telefone
) {
}

