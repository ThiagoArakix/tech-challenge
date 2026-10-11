package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para registro do laudo de diagnóstico técnico")
public record RegistrarDiagnosticoRequest(
        @Schema(description = "Laudo técnico detalhado do diagnóstico realizado", example = "Amortecedor dianteiro direito com vazamento de fluido. Necessária substituição do par.")
        @NotBlank(message = "O laudo de diagnóstico é obrigatório")
        @Size(max = 500, message = "O laudo deve conter no máximo 500 caracteres")
        String diagnostico
) {
}

