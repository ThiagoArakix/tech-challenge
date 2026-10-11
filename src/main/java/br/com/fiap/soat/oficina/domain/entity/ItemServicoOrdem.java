package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ItemServicoOrdem {

    private final UUID id;
    private final String descricao;
    private final BigDecimal quantidade;
    private final Dinheiro valorUnitario;
    private final Instant criadoEm;

    public ItemServicoOrdem(UUID id, String descricao, BigDecimal quantidade, Dinheiro valorUnitario, Instant criadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();

        if (descricao == null || descricao.isBlank()) {
            throw new DomainException("A descrição do serviço não pode ser vazia ou nula");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("A quantidade do serviço deve ser estritamente maior que zero: " + quantidade);
        }

        this.descricao = descricao.trim();
        this.quantidade = quantidade;
        this.valorUnitario = Objects.requireNonNull(valorUnitario, "O valor unitário do serviço é obrigatório");
    }

    public static ItemServicoOrdem criarNovo(String descricao, BigDecimal quantidade, Dinheiro valorUnitario) {
        return new ItemServicoOrdem(UUID.randomUUID(), descricao, quantidade, valorUnitario, Instant.now());
    }

    public static ItemServicoOrdem criarNovo(String descricao, int quantidade, Dinheiro valorUnitario) {
        return new ItemServicoOrdem(UUID.randomUUID(), descricao, BigDecimal.valueOf(quantidade), valorUnitario, Instant.now());
    }

    public Dinheiro getValorTotal() {
        return this.valorUnitario.multiplicar(this.quantidade);
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public Dinheiro getValorUnitario() {
        return valorUnitario;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemServicoOrdem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

