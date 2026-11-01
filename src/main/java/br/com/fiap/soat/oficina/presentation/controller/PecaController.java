package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.PecaOutput;
import br.com.fiap.soat.oficina.application.usecase.peca.CadastrarPecaUseCase;
import br.com.fiap.soat.oficina.application.usecase.peca.GerenciarEstoquePecaUseCase;
import br.com.fiap.soat.oficina.application.usecase.peca.ListarPecasUseCase;
import br.com.fiap.soat.oficina.presentation.request.AjusteEstoqueRequest;
import br.com.fiap.soat.oficina.presentation.request.AtualizarPecaRequest;
import br.com.fiap.soat.oficina.presentation.request.CriarPecaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/pecas")
@Tag(name = "Peças", description = "Endpoints para catálogo e controle de estoque de peças e insumos")
public class PecaController {

    private final CadastrarPecaUseCase cadastrarPecaUseCase;
    private final GerenciarEstoquePecaUseCase gerenciarEstoquePecaUseCase;
    private final ListarPecasUseCase listarPecasUseCase;

    public PecaController(CadastrarPecaUseCase cadastrarPecaUseCase,
                          GerenciarEstoquePecaUseCase gerenciarEstoquePecaUseCase,
                          ListarPecasUseCase listarPecasUseCase) {
        this.cadastrarPecaUseCase = cadastrarPecaUseCase;
        this.gerenciarEstoquePecaUseCase = gerenciarEstoquePecaUseCase;
        this.listarPecasUseCase = listarPecasUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova peça no catálogo", description = "Registra item com código identificador único e estoque inicial.")
    public ResponseEntity<PecaOutput> cadastrar(@RequestBody @Valid CriarPecaRequest request) {
        PecaOutput output = cadastrarPecaUseCase.executar(request.toInput());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id())
                .toUri();

        return ResponseEntity.created(location).body(output);
    }

    @GetMapping
    @Operation(summary = "Lista todas as peças do catálogo")
    public ResponseEntity<List<PecaOutput>> listarTodas() {
        return ResponseEntity.ok(listarPecasUseCase.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca peça do catálogo por ID (UUID)")
    public ResponseEntity<PecaOutput> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(listarPecasUseCase.buscarPorId(id));
    }

    @GetMapping("/abaixo-minimo")
    @Operation(summary = "Lista peças com saldo de estoque abaixo do mínimo configurado")
    public ResponseEntity<List<PecaOutput>> listarAbaixoDoMinimo() {
        return ResponseEntity.ok(listarPecasUseCase.listarAbaixoDoMinimo());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados cadastrais, valor unitário e estoque mínimo de uma peça")
    public ResponseEntity<PecaOutput> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarPecaRequest request) {
        PecaOutput output = gerenciarEstoquePecaUseCase.atualizar(
                id, request.nome(), request.descricao(), request.valorUnitario(), request.estoqueMinimo());
        return ResponseEntity.ok(output);
    }

    @PatchMapping("/{id}/repor-estoque")
    @Operation(summary = "Repõe saldo de estoque de uma peça")
    public ResponseEntity<PecaOutput> reporEstoque(
            @PathVariable UUID id,
            @RequestBody @Valid AjusteEstoqueRequest request) {
        PecaOutput output = gerenciarEstoquePecaUseCase.reporEstoque(id, request.quantidade());
        return ResponseEntity.ok(output);
    }

    @PatchMapping("/{id}/debitar-estoque")
    @Operation(summary = "Debita saldo de estoque de uma peça (baixa avulsa)")
    public ResponseEntity<PecaOutput> debitarEstoque(
            @PathVariable UUID id,
            @RequestBody @Valid AjusteEstoqueRequest request) {
        PecaOutput output = gerenciarEstoquePecaUseCase.debitarEstoque(id, request.quantidade());
        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma peça do catálogo pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        gerenciarEstoquePecaUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

