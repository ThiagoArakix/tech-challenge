package br.com.fiap.soat.oficina.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ordens_servico")
public class OrdemServicoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private UUID veiculoId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "descricao_problema", nullable = false, length = 500)
    private String descricaoProblema;

    @Column(name = "diagnostico", length = 500)
    private String diagnostico;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    private Instant dataAbertura;

    @Column(name = "data_aprovacao")
    private Instant dataAprovacao;

    @Column(name = "data_finalizacao")
    private Instant dataFinalizacao;

    @Column(name = "data_entrega")
    private Instant dataEntrega;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemServicoJpaEntity> itensServico = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPecaJpaEntity> itensPeca = new ArrayList<>();

    public OrdemServicoJpaEntity() {
    }

    public OrdemServicoJpaEntity(UUID id, UUID clienteId, UUID veiculoId, String status,
                                String descricaoProblema, String diagnostico, BigDecimal valorTotal,
                                Instant dataAbertura, Instant dataAprovacao, Instant dataFinalizacao,
                                Instant dataEntrega, Instant criadoEm, Instant atualizadoEm) {
        this.id = id;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.status = status;
        this.descricaoProblema = descricaoProblema;
        this.diagnostico = diagnostico;
        this.valorTotal = valorTotal;
        this.dataAbertura = dataAbertura;
        this.dataAprovacao = dataAprovacao;
        this.dataFinalizacao = dataFinalizacao;
        this.dataEntrega = dataEntrega;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public void adicionarItemServico(ItemServicoJpaEntity item) {
        this.itensServico.add(item);
        item.setOrdemServico(this);
    }

    public void adicionarItemPeca(ItemPecaJpaEntity item) {
        this.itensPeca.add(item);
        item.setOrdemServico(this);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(UUID veiculoId) {
        this.veiculoId = veiculoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Instant getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Instant dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Instant getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Instant dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Instant getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(Instant dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public Instant getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Instant dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<ItemServicoJpaEntity> getItensServico() {
        return itensServico;
    }

    public void setItensServico(List<ItemServicoJpaEntity> itensServico) {
        this.itensServico = (itensServico != null) ? itensServico : new ArrayList<>();
    }

    public List<ItemPecaJpaEntity> getItensPeca() {
        return itensPeca;
    }

    public void setItensPeca(List<ItemPecaJpaEntity> itensPeca) {
        this.itensPeca = (itensPeca != null) ? itensPeca : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServicoJpaEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

