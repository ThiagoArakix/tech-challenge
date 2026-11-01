package br.com.fiap.soat.oficina.presentation.controller;

import br.com.fiap.soat.oficina.application.dto.response.MetricasTempoMedioOutput;
import br.com.fiap.soat.oficina.application.dto.response.OrdemServicoOutput;
import br.com.fiap.soat.oficina.application.usecase.os.AdicionarItensOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.AprovarOrcamentoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.AtualizarStatusOSUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.CalcularTempoMedioExecucaoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.CriarOrdemServicoUseCase;
import br.com.fiap.soat.oficina.application.usecase.os.ListarOrdensServicoUseCase;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import br.com.fiap.soat.oficina.presentation.request.AdicionarItemPecaRequest;
import br.com.fiap.soat.oficina.presentation.request.AdicionarItemServicoRequest;
import br.com.fiap.soat.oficina.presentation.request.CriarOrdemServicoRequest;
import br.com.fiap.soat.oficina.presentation.request.RegistrarDiagnosticoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ordens-servico")
@Tag(name = "Ordens de Serviço (Administrativo)", description = "Fluxo operacional completo de atendimento e gestão das ordens de serviço")
public class OrdemServicoAdminController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final AdicionarItensOSUseCase adicionarItensOSUseCase;
    private final AprovarOrcamentoUseCase aprovarOrcamentoUseCase;
    private final AtualizarStatusOSUseCase atualizarStatusOSUseCase;
    private final CalcularTempoMedioExecucaoUseCase calcularTempoMedioExecucaoUseCase;
    private final ListarOrdensServicoUseCase listarOrdensServicoUseCase;

    public OrdemServicoAdminController(CriarOrdemServicoUseCase criarOrdemServicoUseCase,
                                      AdicionarItensOSUseCase adicionarItensOSUseCase,
                                      AprovarOrcamentoUseCase aprovarOrcamentoUseCase,
                                      AtualizarStatusOSUseCase atualizarStatusOSUseCase,
                                      CalcularTempoMedioExecucaoUseCase calcularTempoMedioExecucaoUseCase,
                                      ListarOrdensServicoUseCase listarOrdensServicoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.adicionarItensOSUseCase = adicionarItensOSUseCase;
        this.aprovarOrcamentoUseCase = aprovarOrcamentoUseCase;
        this.atualizarStatusOSUseCase = atualizarStatusOSUseCase;
        this.calcularTempoMedioExecucaoUseCase = calcularTempoMedioExecucaoUseCase;
        this.listarOrdensServicoUseCase = listarOrdensServicoUseCase;
    }

    @PostMapping
    @Operation(summary = "Passo 1 - Abertura da OS: Abre uma nova Ordem de Serviço (Status: RECEBIDA)",
            description = "Valida a posse do veículo pelo cliente e inicia a Ordem de Serviço em status RECEBIDA.")
    public ResponseEntity<OrdemServicoOutput> criar(@RequestBody @Valid CriarOrdemServicoRequest request) {
        OrdemServicoOutput output = criarOrdemServicoUseCase.executar(request.toInput());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(output.id())
                .toUri();

        return ResponseEntity.created(location).body(output);
    }

    @PostMapping("/{id}/iniciar-diagnostico")
    @Operation(summary = "Passo 2 - Início do Diagnóstico: Inicia a análise técnica (Status: EM_DIAGNOSTICO)",
            description = "Avança a OS para EM_DIAGNOSTICO para início dos testes e inspeção do veículo.")
    public ResponseEntity<OrdemServicoOutput> iniciarDiagnostico(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.iniciarDiagnostico(id));
    }

    @PatchMapping("/{id}/diagnostico")
    @Operation(summary = "Passo 3 - Laudo Técnico: Registra o parecer técnico do diagnóstico realizado",
            description = "Descreve os defeitos encontrados e laudo técnico para respaldar as peças e serviços necessários.")
    public ResponseEntity<OrdemServicoOutput> registrarDiagnostico(
            @PathVariable UUID id,
            @RequestBody @Valid RegistrarDiagnosticoRequest request) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.registrarDiagnostico(id, request.diagnostico()));
    }

    @PostMapping("/{id}/itens/servicos")
    @Operation(summary = "Passo 4 - Adicionar Serviços: Inclui serviços e mão de obra no orçamento",
            description = "Adiciona um serviço (via servicoId do catálogo ou descrição/valor avulsos) à Ordem de Serviço.")
    public ResponseEntity<OrdemServicoOutput> adicionarServico(
            @PathVariable UUID id,
            @RequestBody @Valid AdicionarItemServicoRequest request) {
        return ResponseEntity.ok(adicionarItensOSUseCase.adicionarServico(id, request.toInput()));
    }

    @DeleteMapping("/{id}/itens/servicos/{itemId}")
    @Operation(summary = "Passo 4.1 - Remover Serviço: Remove um item de serviço durante o diagnóstico",
            description = "Remove um serviço previamente incluído na OS enquanto ela estiver em fase de diagnóstico.")
    public ResponseEntity<OrdemServicoOutput> removerServico(
            @PathVariable UUID id,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(adicionarItensOSUseCase.removerServico(id, itemId));
    }

    @PostMapping("/{id}/itens/pecas")
    @Operation(summary = "Passo 5 - Adicionar Peças: Inclui peças no orçamento (com validação de estoque)",
            description = "Adiciona peças do inventário à Ordem de Serviço, validando prévia disponibilidade de saldo.")
    public ResponseEntity<OrdemServicoOutput> adicionarPeca(
            @PathVariable UUID id,
            @RequestBody @Valid AdicionarItemPecaRequest request) {
        return ResponseEntity.ok(adicionarItensOSUseCase.adicionarPeca(id, request.toInput()));
    }

    @DeleteMapping("/{id}/itens/pecas/{itemId}")
    @Operation(summary = "Passo 5.1 - Remover Peça: Remove um item de peça durante o diagnóstico",
            description = "Remove uma peça previamente incluída na OS enquanto ela estiver em fase de diagnóstico.")
    public ResponseEntity<OrdemServicoOutput> removerPeca(
            @PathVariable UUID id,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(adicionarItensOSUseCase.removerPeca(id, itemId));
    }

    @PostMapping("/{id}/submeter-aprovacao")
    @Operation(summary = "Passo 6 - Submeter para Aprovação: Congela o orçamento e avança para AGUARDANDO_APROVACAO",
            description = "Avança o status da OS para AGUARDANDO_APROVACAO e bloqueia edições posteriores de peças e serviços.")
    public ResponseEntity<OrdemServicoOutput> submeterParaAprovacao(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.submeterParaAprovacao(id));
    }

    @PostMapping("/{id}/aprovar")
    @Transactional
    @Operation(summary = "Passo 7 - Aprovar Orçamento: Baixa definitiva do estoque e início dos trabalhos (Status: EM_EXECUCAO)",
            description = "Aprova orçamento da OS, realiza baixa definitiva de estoque de todas as peças e avança para EM_EXECUCAO.")
    public ResponseEntity<OrdemServicoOutput> aprovarOrcamento(@PathVariable UUID id) {
        return ResponseEntity.ok(aprovarOrcamentoUseCase.executar(id));
    }

    @PostMapping("/{id}/rejeitar")
    @Operation(summary = "Passo 7.1 - Rejeitar Orçamento: Registra a rejeição do orçamento (Status: CANCELADA)",
            description = "Registra a rejeição do orçamento que estava AGUARDANDO_APROVACAO, cancelando a OS sem debitar estoque.")
    public ResponseEntity<OrdemServicoOutput> rejeitarOrcamento(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.rejeitarOrcamento(id));
    }

    @PostMapping("/{id}/finalizar")
    @Operation(summary = "Passo 8 - Finalizar Execução: Conclui os serviços executados (Status: FINALIZADA)",
            description = "Finaliza a execução dos serviços da OS e registra a data/hora de encerramento técnico.")
    public ResponseEntity<OrdemServicoOutput> finalizarExecucao(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.finalizarExecucao(id));
    }

    @PostMapping("/{id}/entregar")
    @Operation(summary = "Passo 9 - Registrar Entrega: Registra a entrega do veículo ao cliente (Status: ENTREGUE)",
            description = "Registra a entrega física do veículo finalizado ao cliente, encerrando o ciclo de atendimento.")
    public ResponseEntity<OrdemServicoOutput> registrarEntrega(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.registrarEntrega(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta - Buscar por ID: Consulta detalhes completos de uma Ordem de Serviço",
            description = "Retorna todos os dados da OS, incluindo cliente, veículo, itens de serviço, itens de peça e histórico de datas.")
    public ResponseEntity<OrdemServicoOutput> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(listarOrdensServicoUseCase.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Consulta - Listar todas as OS: Lista Ordens de Serviço (com filtro opcional por status)",
            description = "Permite filtrar opcionalmente por status (RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO, EM_EXECUCAO, FINALIZADA, ENTREGUE, CANCELADA).")
    public ResponseEntity<List<OrdemServicoOutput>> listar(
            @RequestParam(required = false) StatusOS status) {
        return ResponseEntity.ok(listarOrdensServicoUseCase.listar(status));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Gestão - Cancelar OS: Cancela a Ordem de Serviço (Status: CANCELADA)",
            description = "Cancela a OS em qualquer etapa permitida pelas regras de transição de status.")
    public ResponseEntity<OrdemServicoOutput> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(atualizarStatusOSUseCase.cancelar(id));
    }

    @GetMapping("/metricas/tempo-medio")
    @Operation(summary = "Métricas - Tempo Médio: Calcula o tempo médio de execução das OS finalizadas",
            description = "Calcula tempo médio entre a aprovação e finalização de todas as OS com status FINALIZADA ou ENTREGUE (exclusivo ADMIN).")
    public ResponseEntity<MetricasTempoMedioOutput> calcularTempoMedio() {
        return ResponseEntity.ok(calcularTempoMedioExecucaoUseCase.executar());
    }
}

