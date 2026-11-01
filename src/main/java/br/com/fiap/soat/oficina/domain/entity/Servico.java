package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Servico {

    private final UUID id;
    private final String codigo;
    private String nome;
    private String descricao;
    private Dinheiro valorUnitario;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Servico(UUID id, String codigo, String nome, String descricao,
                   Dinheiro valorUnitario, Instant criadoEm, Instant atualizadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm;

        if (codigo == null || codigo.isBlank()) {
            throw new DomainException("O código identificador do serviço é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new DomainException("O nome do serviço é obrigatório");
        }

        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.descricao = (descricao != null && !descricao.isBlank()) ? descricao.trim() : null;
        this.valorUnitario = Objects.requireNonNull(valorUnitario, "O valor unitário do serviço é obrigatório");
    }

    public static Servico criarNovo(String codigo, String nome, String descricao, Dinheiro valorUnitario) {
        return new Servico(UUID.randomUUID(), codigo, nome, descricao, valorUnitario, Instant.now(), null);
    }

    public void atualizarDados(String novoNome, String novaDescricao, Dinheiro novoValorUnitario) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new DomainException("O nome do serviço não pode ser nulo ou vazio");
        }
        this.nome = novoNome.trim();
        this.descricao = (novaDescricao != null && !novaDescricao.isBlank()) ? novaDescricao.trim() : null;
        this.valorUnitario = Objects.requireNonNull(novoValorUnitario, "O valor unitário do serviço não pode ser nulo");
        this.atualizadoEm = Instant.now();
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

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servico servico)) return false;
        return Objects.equals(id, servico.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

