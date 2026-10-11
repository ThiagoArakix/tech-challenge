package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Peca {

    private final UUID id;
    private final String codigo;
    private String nome;
    private String descricao;
    private Dinheiro valorUnitario;
    private int saldoEstoque;
    private int estoqueMinimo;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Peca(UUID id, String codigo, String nome, String descricao, Dinheiro valorUnitario,
                int saldoEstoque, int estoqueMinimo, Instant criadoEm, Instant atualizadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm;

        if (codigo == null || codigo.isBlank()) {
            throw new DomainException("O código identificador da peça é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new DomainException("O nome da peça é obrigatório");
        }
        if (saldoEstoque < 0) {
            throw new DomainException("O saldo de estoque não pode ser negativo: " + saldoEstoque);
        }
        if (estoqueMinimo < 0) {
            throw new DomainException("O estoque mínimo não pode ser negativo: " + estoqueMinimo);
        }

        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.descricao = (descricao != null && !descricao.isBlank()) ? descricao.trim() : null;
        this.valorUnitario = Objects.requireNonNull(valorUnitario, "O valor unitário da peça é obrigatório");
        this.saldoEstoque = saldoEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }

    public static Peca criarNova(String codigo, String nome, String descricao, Dinheiro valorUnitario,
                                 int saldoEstoque, int estoqueMinimo) {
        return new Peca(UUID.randomUUID(), codigo, nome, descricao, valorUnitario, saldoEstoque, estoqueMinimo, Instant.now(), null);
    }

    public void debitarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new DomainException("A quantidade a debitar do estoque deve ser maior que zero: " + quantidade);
        }
        if (quantidade > this.saldoEstoque) {
            throw new SaldoEstoqueInsuficienteException(this.saldoEstoque, quantidade);
        }
        this.saldoEstoque -= quantidade;
        this.atualizadoEm = Instant.now();
    }

    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new DomainException("A quantidade a repor no estoque deve ser maior que zero: " + quantidade);
        }
        this.saldoEstoque += quantidade;
        this.atualizadoEm = Instant.now();
    }

    public void atualizarPreco(Dinheiro novoValor) {
        this.valorUnitario = Objects.requireNonNull(novoValor, "O novo valor unitário não pode ser nulo");
        this.atualizadoEm = Instant.now();
    }

    public void atualizarDados(String novoNome, String novaDescricao, int novoEstoqueMinimo) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new DomainException("O nome da peça não pode ser nulo ou vazio");
        }
        if (novoEstoqueMinimo < 0) {
            throw new DomainException("O estoque mínimo não pode ser negativo");
        }
        this.nome = novoNome.trim();
        this.descricao = (novaDescricao != null && !novaDescricao.isBlank()) ? novaDescricao.trim() : null;
        this.estoqueMinimo = novoEstoqueMinimo;
        this.atualizadoEm = Instant.now();
    }

    public boolean isEstoqueAbaixoDoMinimo() {
        return this.saldoEstoque < this.estoqueMinimo;
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Dinheiro getValorUnitario() {
        return valorUnitario;
    }

    public int getSaldoEstoque() {
        return saldoEstoque;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
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
        if (!(o instanceof Peca peca)) return false;
        return Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

