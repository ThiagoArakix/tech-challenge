package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ItemPecaOrdem {

    private final UUID id;
    private final UUID pecaId;
    private final int quantidade;
    private final Dinheiro valorUnitario;
    private final Instant criadoEm;

    public ItemPecaOrdem(UUID id, UUID pecaId, int quantidade, Dinheiro valorUnitario, Instant criadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.pecaId = Objects.requireNonNull(pecaId, "O ID da peça é obrigatório");
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();

        if (quantidade <= 0) {
            throw new DomainException("A quantidade de peças na ordem deve ser estritamente maior que zero: " + quantidade);
        }

        this.quantidade = quantidade;
        this.valorUnitario = Objects.requireNonNull(valorUnitario, "O valor unitário da peça é obrigatório");
    }

    public static ItemPecaOrdem criarNovo(UUID pecaId, int quantidade, Dinheiro valorUnitario) {
        return new ItemPecaOrdem(UUID.randomUUID(), pecaId, quantidade, valorUnitario, Instant.now());
    }

    public Dinheiro getValorTotal() {
        return this.valorUnitario.multiplicar(this.quantidade);
    }

    public UUID getId() {
        return id;
    }

    public UUID getPecaId() {
        return pecaId;
    }

    public int getQuantidade() {
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
        if (!(o instanceof ItemPecaOrdem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

