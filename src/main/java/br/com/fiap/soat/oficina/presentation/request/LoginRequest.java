package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação de usuário")
public record LoginRequest(
        @Schema(description = "Nome de usuário (ex: admin ou mecanico)", example = "admin")
        @NotBlank(message = "O nome de usuário é obrigatório")
        String username,

        @Schema(description = "Senha de acesso (ex: admin123 ou mecanico123)", example = "admin123")
        @NotBlank(message = "A senha de acesso é obrigatória")
        String password
) {
}

