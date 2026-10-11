package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarVeiculoInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Dados para cadastro de veículo vinculado a um cliente")
public record CriarVeiculoRequest(
        @Schema(description = "ID do cliente proprietário", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @NotNull(message = "O ID do cliente proprietário é obrigatório")
        UUID clienteId,

        @Schema(description = "Placa do veículo (padrão tradicional ou Mercosul)", example = "ABC1D23")
        @NotBlank(message = "A placa do veículo é obrigatória")
        String placa,

        @Schema(description = "Marca / Fabricante", example = "Toyota")
        @NotBlank(message = "A marca do veículo é obrigatória")
        String marca,

        @Schema(description = "Modelo", example = "Corolla")
        @NotBlank(message = "O modelo do veículo é obrigatório")
        String modelo,

        @Schema(description = "Ano de fabricação", example = "2021")
        @NotNull(message = "O ano de fabricação é obrigatório")
        @Min(value = 1901, message = "O ano de fabricação deve ser posterior a 1900")
        Integer anoFabricacao,

        @Schema(description = "Ano do modelo", example = "2022")
        @NotNull(message = "O ano do modelo é obrigatório")
        Integer anoModelo,

        @Schema(description = "Cor predominante", example = "Preto")
        String cor,

        @Schema(description = "Quilometragem atual", example = "35000")
        @NotNull(message = "A quilometragem é obrigatória")
        @Min(value = 0, message = "A quilometragem não pode ser negativa")
        Integer quilometragem
) {
    public CadastrarVeiculoInput toInput() {
        return new CadastrarVeiculoInput(clienteId, placa, marca, modelo, anoFabricacao, anoModelo, cor, quilometragem);
    }
}

