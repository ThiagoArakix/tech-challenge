package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.StatusOSOutput;
import br.com.fiap.soat.oficina.application.usecase.os.AprovarOrcamentoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.AtualizarStatusOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.ConsultarStatusOSUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/ordens-servico")
@Tag(name = "Acompanhamento Público (Cliente)", description = "Endpoints públicos para consulta de status, orçamento e aprovação/rejeição pelo próprio cliente")
public class OrdemServicoPublicController {

    private final ConsultarStatusOSUseCase consultarStatusOSUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final AtualizarStatusOSUseCase atualizarStatusOSUseCase;

    public OrdemServicoPublicController(ConsultarStatusOSUseCase consultarStatusOSUseCase,
                                        AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
                                        AtualizarStatusOSUseCase atualizarStatusOSUseCase) {
        this.consultarStatusOSUseCase = consultarStatusOSUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.atualizarStatusOSUseCase = atualizarStatusOSUseCase;
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Consulta pública de status e detalhamento do orçamento da Ordem de Serviço",
            description = "Acesso público para que o cliente acompanhe em tempo real a evolução do serviço e os itens do orçamento.")
    public ResponseEntity<StatusOSOutput> consultarStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(consultarStatusOSUseCase.executar(id));
    }

    @PostMapping("/{id}/aprovar")
    @Transactional
    @Operation(summary = "Aprovação direta de orçamento pelo cliente",
            description = "Permite que o cliente aprove o orçamento disponibilizado, debitando peças em estoque e iniciando a execução.")
    public ResponseEntity<StatusOSOutput> aprovarPeloCliente(@PathVariable UUID id) {
        aprovarOrcamentoUseCase.executar(id);
        return ResponseEntity.ok(consultarStatusOSUseCase.executar(id));
    }

    @PostMapping("/{id}/rejeitar")
    @Transactional
    @Operation(summary = "Rejeição direta de orçamento pelo cliente",
            description = "Permite que o cliente rejeite o orçamento disponibilizado, cancelando a Ordem de Serviço sem baixa de estoque.")
    public ResponseEntity<StatusOSOutput> rejeitarPeloCliente(@PathVariable UUID id) {
        atualizarStatusOSUseCase.rejeitarOrcamento(id);
        return ResponseEntity.ok(consultarStatusOSUseCase.executar(id));
    }
}

