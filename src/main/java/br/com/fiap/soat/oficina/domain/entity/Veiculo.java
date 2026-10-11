package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Placa;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Veiculo {

    private final UUID id;
    private final UUID clienteId;
    private final Placa placa;
    private String marca;
    private String modelo;
    private final int anoFabricacao;
    private final int anoModelo;
    private String cor;
    private int quilometragem;
    private final Instant criadoEm;
    private Instant atualizadoEm;

    public Veiculo(UUID id, UUID clienteId, Placa placa, String marca, String modelo,
                   int anoFabricacao, int anoModelo, String cor, int quilometragem,
                   Instant criadoEm, Instant atualizadoEm) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.clienteId = Objects.requireNonNull(clienteId, "O ID do cliente proprietário do veículo é obrigatório");
        this.placa = Objects.requireNonNull(placa, "A placa do veículo é obrigatória");
        this.criadoEm = (criadoEm != null) ? criadoEm : Instant.now();
        this.atualizadoEm = atualizadoEm;

        if (anoFabricacao <= 1900) {
            throw new DomainException("Ano de fabricação deve ser posterior a 1900: " + anoFabricacao);
        }
        if (anoModelo < anoFabricacao) {
            throw new DomainException(String.format("Ano do modelo (%d) não pode ser inferior ao ano de fabricação (%d)",
                    anoModelo, anoFabricacao));
        }
        if (quilometragem < 0) {
            throw new DomainException("A quilometragem não pode ser negativa: " + quilometragem);
        }

        this.anoFabricacao = anoFabricacao;
        this.anoModelo = anoModelo;
        this.quilometragem = quilometragem;

        validarEAtribuirDados(marca, modelo, cor);
    }

    public static Veiculo criarNovo(UUID clienteId, Placa placa, String marca, String modelo,
                                    int anoFabricacao, int anoModelo, String cor, int quilometragem) {
        return new Veiculo(UUID.randomUUID(), clienteId, placa, marca, modelo, anoFabricacao, anoModelo, cor, quilometragem, Instant.now(), null);
    }

    public void atualizarQuilometragem(int novaQuilometragem) {
        if (novaQuilometragem < this.quilometragem) {
            throw new DomainException(String.format(
                    "Quilometragem informada (%d km) não pode ser inferior à registrada anteriormente (%d km)",
                    novaQuilometragem, this.quilometragem));
        }
        this.quilometragem = novaQuilometragem;
        this.atualizadoEm = Instant.now();
    }

    public void atualizarDados(String novaMarca, String novoModelo, String novaCor) {
        validarEAtribuirDados(novaMarca, novoModelo, novaCor);
        this.atualizadoEm = Instant.now();
    }

    private void validarEAtribuirDados(String marca, String modelo, String cor) {
        if (marca == null || marca.isBlank()) {
            throw new DomainException("A marca do veículo não pode ser vazia ou nula");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new DomainException("O modelo do veículo não pode ser vazio ou nulo");
        }

        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.cor = (cor != null && !cor.isBlank()) ? cor.trim() : null;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public Placa getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnoFabricacao() {
        return anoFabricacao;
    }

    public int getAnoModelo() {
        return anoModelo;
    }

    public String getCor() {
        return cor;
    }

    public int getQuilometragem() {
        return quilometragem;
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
        if (!(o instanceof Veiculo veiculo)) return false;
        return Objects.equals(id, veiculo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

