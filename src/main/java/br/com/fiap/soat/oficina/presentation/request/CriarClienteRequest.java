package br.com.fiap.soat.oficina.presentation.request;

import br.com.fiap.soat.oficina.application.dto.request.CadastrarClienteInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de novo cliente")
public record CriarClienteRequest(
        @Schema(description = "Nome completo do cliente", example = "Mariana Costa")
        @NotBlank(message = "O nome do cliente é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve conter entre 2 e 150 caracteres")
        String nome,

        @Schema(description = "Documento de identificação (CPF ou CNPJ)", example = "529.982.247-25")
        @NotBlank(message = "O documento (CPF/CNPJ) é obrigatório")
        String documento,

        @Schema(description = "E-mail de contato", example = "mariana.costa@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail informado é inválido")
        String email,

        @Schema(description = "Telefone para contato", example = "11987654321")
        @NotBlank(message = "O telefone é obrigatório")
        String telefone
) {
    public CadastrarClienteInput toInput() {
        return new CadastrarClienteInput(nome, documento, email, telefone);
    }
}

