package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.CpfCnpj;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Cliente {

    private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final UUID id;
    private String nome;
    private final CpfCnpj documento;
    private String email;
    private String telefone;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Cliente(UUID id, String nome, CpfCnpj documento, String email, String telefone, Instant criadoEm, Instant atualizadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.documento = Objects.requireNonNull(documento, "O documento (CPF/CNPJ) do cliente é obrigatório");
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm;

        validarEAtribuirCampos(nome, email, telefone);
    }

    public static Cliente criarNovo(String nome, CpfCnpj documento, String email, String telefone) {
        return new Cliente(UUID.randomUUID(), nome, documento, email, telefone, Instant.now(), null);
    }

    public void atualizar(String novoNome, String novoEmail, String novoTelefone) {
        validarEAtribuirCampos(novoNome, novoEmail, novoTelefone);
        this.atualizadoEm = Instant.now();
    }

    private void validarEAtribuirCampos(String nome, String email, String telefone) {
        if (nome == null || nome.isBlank()) {
            throw new DomainException("O nome do cliente não pode ser vazio ou nulo");
        }
        if (nome.trim().length() < 2) {
            throw new DomainException("O nome do cliente deve conter ao menos 2 caracteres");
        }
        if (email == null || email.isBlank() || !REGEX_EMAIL.matcher(email.trim()).matches()) {
            throw new DomainException("E-mail inválido informado para o cliente: " + email);
        }
        if (telefone == null || telefone.isBlank()) {
            throw new DomainException("O telefone do cliente não pode ser vazio ou nulo");
        }

        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
        this.telefone = telefone.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CpfCnpj getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
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
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

