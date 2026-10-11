package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.ClienteOutput;
import br.com.fiap.soat.oficina.application.usecase.cliente.AtualizarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.BuscarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.CadastrarClienteUseCase;
import br.com.fiap.soat.oficina.application.usecase.cliente.ListarClientesUseCase;
import br.com.fiap.soat.oficina.presentation.request.AtualizarClienteRequest;
import br.com.fiap.soat.oficina.presentation.request.CriarClienteRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Endpoints para cadastro e gestão de clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;

    public ClienteController(CadastrarClienteUseCase cadastrarClienteUseCase,
                             BuscarClienteUseCase buscarClienteUseCase,
                             AtualizarClienteUseCase atualizarClienteUseCase,
                             ListarClientesUseCase listarClientesUseCase) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente", description = "Registra cliente com validação matemática de CPF ou CNPJ.")
    public ResponseEntity<ClienteOutput> cadastrar(@RequestBody @Valid CriarClienteRequest request) {
        ClienteOutput output = cadastrarClienteUseCase.executar(request.toInput());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id())
                .toUri();

        return ResponseEntity.created(location).body(output);
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes cadastrados")
    public ResponseEntity<List<ClienteOutput>> listarTodos() {
        return ResponseEntity.ok(listarClientesUseCase.executar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca cliente por ID (UUID)")
    public ResponseEntity<ClienteOutput> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(buscarClienteUseCase.buscarPorId(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Busca cliente por CPF ou CNPJ")
    public ResponseEntity<ClienteOutput> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(buscarClienteUseCase.buscarPorDocumento(documento));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados cadastrais do cliente")
    public ResponseEntity<ClienteOutput> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarClienteRequest request) {
        ClienteOutput output = atualizarClienteUseCase.executar(id, request.nome(), request.email(), request.telefone());
        return ResponseEntity.ok(output);
    }
}

