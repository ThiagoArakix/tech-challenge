package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrdemServico {

    private final UUID id;
    private final UUID clienteId;
    private final UUID veiculoId;
    private StatusOS status;
    private String descricaoProblema;
    private String diagnostico;
    private final List<ItemServicoOrdem> itensServico;
    private final List<ItemPecaOrdem> itensPeca;
    private final Instant dataAbertura;
    private Instant dataAprovacao;
    private Instant dataFinalizacao;
    private Instant dataEntrega;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public OrdemServico(UUID id, UUID clienteId, UUID veiculoId, StatusOS status,
                        String descricaoProblema, String diagnostico,
                        List<ItemServicoOrdem> itensServico, List<ItemPecaOrdem> itensPeca,
                        Instant dataAbertura, Instant dataAprovacao, Instant dataFinalizacao,
                        Instant dataEntrega, Instant criadoEm, Instant atualizadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.clienteId = Objects.requireNonNull(clienteId, "O ID do cliente associado à OS é obrigatório");
        this.veiculoId = Objects.requireNonNull(veiculoId, "O ID do veículo associado à OS é obrigatório");
        this.status = (status != null) ? status : StatusOS.RECEBIDA;
        this.dataAbertura = (dataAbertura != null) ? dataAbertura : Instant.now();
        this.dataAprovacao = dataAprovacao;
        this.dataFinalizacao = dataFinalizacao;
        this.dataEntrega = dataEntrega;
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm;

        if (descricaoProblema == null || descricaoProblema.isBlank()) {
            throw new DomainException("A descrição do problema informado pelo cliente é obrigatória");
        }
        this.descricaoProblema = descricaoProblema.trim();
        this.diagnostico = (diagnostico != null && !diagnostico.isBlank()) ? diagnostico.trim() : null;

        this.itensServico = (itensServico != null) ? new ArrayList<>(itensServico) : new ArrayList<>();
        this.itensPeca = (itensPeca != null) ? new ArrayList<>(itensPeca) : new ArrayList<>();
    }

    public static OrdemServico abrirNova(UUID clienteId, UUID veiculoId, String descricaoProblema) {
        return new OrdemServico(UUID.randomUUID(), clienteId, veiculoId, StatusOS.RECEBIDA,
                descricaoProblema, null, new ArrayList<>(), new ArrayList<>(),
                Instant.now(), null, null, null, Instant.now(), null);
    }

    // Gestão de Itens (bloqueado após sair da fase de diagnóstico)

    public void adicionarItemServico(ItemServicoOrdem item) {
        validarPermissaoEdicaoItens();
        Objects.requireNonNull(item, "O item de serviço não pode ser nulo");
        this.itensServico.add(item);
        this.atualizadoEm = Instant.now();
    }

    public void removerItemServico(UUID itemId) {
        validarPermissaoEdicaoItens();
        Objects.requireNonNull(itemId, "O ID do item de serviço a remover é obrigatório");
        boolean removido = this.itensServico.removeIf(i -> i.getId().equals(itemId));
        if (!removido) {
            throw new DomainException("Item de serviço não encontrado na Ordem de Serviço: " + itemId);
        }
        this.atualizadoEm = Instant.now();
    }

    public void adicionarItemPeca(ItemPecaOrdem item) {
        validarPermissaoEdicaoItens();
        Objects.requireNonNull(item, "O item de peça não pode ser nulo");
        this.itensPeca.add(item);
        this.atualizadoEm = Instant.now();
    }

    public void removerItemPeca(UUID itemId) {
        validarPermissaoEdicaoItens();
        Objects.requireNonNull(itemId, "O ID da peça a remover é obrigatório");
        boolean removido = this.itensPeca.removeIf(p -> p.getId().equals(itemId));
        if (!removido) {
            throw new DomainException("Item de peça não encontrado na Ordem de Serviço: " + itemId);
        }
        this.atualizadoEm = Instant.now();
    }

    private void validarPermissaoEdicaoItens() {
        if (!this.status.permiteEdicaoItens()) {
            throw new DomainException(String.format(
                    "Não é permitido alterar itens da Ordem de Serviço após o diagnóstico (status atual: %s)", this.status));
        }
    }

    // Cálculo Automático de Orçamento

    public Dinheiro getValorTotalServicos() {
        return this.itensServico.stream()
                .map(ItemServicoOrdem::getValorTotal)
                .reduce(Dinheiro.ZERO, Dinheiro::somar);
    }

    public Dinheiro getValorTotalPecas() {
        return this.itensPeca.stream()
                .map(ItemPecaOrdem::getValorTotal)
                .reduce(Dinheiro.ZERO, Dinheiro::somar);
    }

    public Dinheiro getValorTotal() {
        return getValorTotalServicos().somar(getValorTotalPecas());
    }

    // Máquina de Estados da Ordem de Serviço

    public void transicionarPara(StatusOS novoStatus) {
        if (!this.status.podeTransicionarPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(this.status, novoStatus);
        }
        this.status = novoStatus;
        this.atualizadoEm = Instant.now();
    }

    public void iniciarDiagnostico() {
        transicionarPara(StatusOS.EM_DIAGNOSTICO);
    }

    public void registrarDiagnostico(String diagnostico) {
        if (this.status != StatusOS.EM_DIAGNOSTICO && this.status != StatusOS.RECEBIDA) {
            throw new DomainException("Só é possível registrar diagnóstico nas fases RECEBIDA ou EM_DIAGNOSTICO");
        }
        if (diagnostico == null || diagnostico.isBlank()) {
            throw new DomainException("O laudo de diagnóstico técnico não pode ser vazio");
        }
        this.diagnostico = diagnostico.trim();
        this.atualizadoEm = Instant.now();
    }

    public void enviarParaAprovacao() {
        if (this.diagnostico == null || this.diagnostico.isBlank()) {
            throw new DomainException("Não é possível enviar para aprovação sem registrar o diagnóstico técnico");
        }
        if (this.itensServico.isEmpty() && this.itensPeca.isEmpty()) {
            throw new DomainException("Não é possível enviar orçamento para aprovação sem ao menos um serviço ou peça");
        }
        transicionarPara(StatusOS.AGUARDANDO_APROVACAO);
    }

    public void aprovar() {
        transicionarPara(StatusOS.EM_EXECUCAO);
        this.dataAprovacao = Instant.now();
    }

    public void rejeitar() {
        if (this.status != StatusOS.AGUARDANDO_APROVACAO) {
            throw new TransicaoStatusInvalidaException(this.status, StatusOS.CANCELADA);
        }
        transicionarPara(StatusOS.CANCELADA);
    }

    public void finalizar() {
        transicionarPara(StatusOS.FINALIZADA);
        this.dataFinalizacao = Instant.now();
    }

    public void entregar() {
        transicionarPara(StatusOS.ENTREGUE);
        this.dataEntrega = Instant.now();
    }

    public void cancelar() {
        transicionarPara(StatusOS.CANCELADA);
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public StatusOS getStatus() {
        return status;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public List<ItemServicoOrdem> getItensServico() {
        return Collections.unmodifiableList(itensServico);
    }

    public List<ItemPecaOrdem> getItensPeca() {
        return Collections.unmodifiableList(itensPeca);
    }

    public Instant getDataAbertura() {
        return dataAbertura;
    }

    public Instant getDataAprovacao() {
        return dataAprovacao;
    }

    public Instant getDataFinalizacao() {
        return dataFinalizacao;
    }

    public Instant getDataEntrega() {
        return dataEntrega;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServico that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

