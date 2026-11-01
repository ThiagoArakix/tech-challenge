package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.VeiculoOutput;
import br.com.fiap.soat.oficina.application.usecase.veiculo.BuscarVeiculoUseCase;
import br.com.fiap.soat.oficina.application.usecase.veiculo.CadastrarVeiculoUseCase;
import br.com.fiap.soat.oficina.presentation.request.AtualizarVeiculoRequest;
import br.com.fiap.soat.oficina.presentation.request.CriarVeiculoRequest;
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
@RequestMapping("/api/v1/veiculos")
@Tag(name = "Veículos", description = "Endpoints para cadastro e gestão de veículos")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private final BuscarVeiculoUseCase buscarVeiculoUseCase;

    public VeiculoController(CadastrarVeiculoUseCase cadastrarVeiculoUseCase,
                             BuscarVeiculoUseCase buscarVeiculoUseCase) {
        this.cadastrarVeiculoUseCase = cadastrarVeiculoUseCase;
        this.buscarVeiculoUseCase = buscarVeiculoUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um veículo vinculado a um cliente", description = "Valida placa Mercosul/Tradicional e cliente proprietário.")
    public ResponseEntity<VeiculoOutput> cadastrar(@RequestBody @Valid CriarVeiculoRequest request) {
        VeiculoOutput output = cadastrarVeiculoUseCase.executar(request.toInput());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id())
                .toUri();

        return ResponseEntity.created(location).body(output);
    }

    @GetMapping
    @Operation(summary = "Lista todos os veículos cadastrados na oficina")
    public ResponseEntity<List<VeiculoOutput>> listarTodos() {
        return ResponseEntity.ok(buscarVeiculoUseCase.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca veículo por ID (UUID)")
    public ResponseEntity<VeiculoOutput> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(buscarVeiculoUseCase.buscarPorId(id));
    }

    @GetMapping("/placa/{placa}")
    @Operation(summary = "Busca veículo por placa (Tradicional ou Mercosul)")
    public ResponseEntity<VeiculoOutput> buscarPorPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(buscarVeiculoUseCase.buscarPorPlaca(placa));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Lista todos os veículos de um cliente")
    public ResponseEntity<List<VeiculoOutput>> listarPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(buscarVeiculoUseCase.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados cadastrais e quilometragem de um veículo")
    public ResponseEntity<VeiculoOutput> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarVeiculoRequest request) {
        VeiculoOutput output = buscarVeiculoUseCase.atualizar(
                id, request.marca(), request.modelo(), request.cor(), request.quilometragem());
        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um veículo pelo ID")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        buscarVeiculoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

