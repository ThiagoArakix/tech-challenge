package br.com.fiap.soat.oficina.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para atualização cadastral de veículo")
public record AtualizarVeiculoRequest(
        @Schema(description = "Marca do veículo", example = "Honda")
        @NotBlank(message = "A marca do veículo é obrigatória")
        @Size(max = 50, message = "A marca deve conter no máximo 50 caracteres")
        String marca,

        @Schema(description = "Modelo do veículo", example = "Civic Touring")
        @NotBlank(message = "O modelo do veículo é obrigatório")
        @Size(max = 50, message = "O modelo deve conter no máximo 50 caracteres")
        String modelo,

        @Schema(description = "Cor do veículo", example = "Preto")
        @Size(max = 30, message = "A cor deve conter no máximo 30 caracteres")
        String cor,

        @Schema(description = "Quilometragem atualizada (não pode ser menor que a anterior)", example = "42000")
        @Min(value = 0, message = "A quilometragem não pode ser negativa")
        Integer quilometragem
) {
}

