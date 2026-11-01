package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.ServicoOutput;
import br.com.fiap.soat.oficina.application.usecase.servico.GerenciarServicoUseCase;
import br.com.fiap.soat.oficina.presentation.request.AtualizarServicoRequest;
import br.com.fiap.soat.oficina.presentation.request.CriarServicoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/servicos")
@Tag(name = "Serviços", description = "Endpoints para catálogo e gestão de serviços (mão de obra)")
public class ServicoController {

    private final GerenciarServicoUseCase gerenciarServicoUseCase;

    public ServicoController(GerenciarServicoUseCase gerenciarServicoUseCase) {
        this.gerenciarServicoUseCase = gerenciarServicoUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo serviço no catálogo", description = "Registra serviço padronizado com código único e valor tabelado.")
    public ResponseEntity<ServicoOutput> cadastrar(@RequestBody @Valid CriarServicoRequest request) {
        ServicoOutput output = gerenciarServicoUseCase.cadastrar(request.toInput());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id())
                .toUri();

        return ResponseEntity.created(location).body(output);
    }

    @GetMapping
    @Operation(summary = "Lista todos os serviços cadastrados no catálogo")
    public ResponseEntity<List<ServicoOutput>> listarTodos() {
        return ResponseEntity.ok(gerenciarServicoUseCase.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca serviço do catálogo por ID (UUID)")
    public ResponseEntity<ServicoOutput> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(gerenciarServicoUseCase.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Busca serviço do catálogo pelo código identificador")
    public ResponseEntity<ServicoOutput> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(gerenciarServicoUseCase.buscarPorCodigo(codigo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados e valor unitário de um serviço do catálogo")
    public ResponseEntity<ServicoOutput> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarServicoRequest request) {
        ServicoOutput output = gerenciarServicoUseCase.atualizar(
                id, request.nome(), request.descricao(), request.valorUnitario());
        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um serviço do catálogo")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        gerenciarServicoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

