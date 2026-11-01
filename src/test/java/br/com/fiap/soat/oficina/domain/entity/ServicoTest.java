package br.com.fiap.soat.oficina.domain.entity;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.valueobject.Dinheiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes de Unidade - Entidade Servico (Catálogo)")
class ServicoTest {

    private Servico servico;

    @BeforeEach
    void setUp() {
        servico = Servico.criarNovo("SRV-001", "Alinhamento 3D", "Alinhamento completo das 4 rodas", Dinheiro.de(120.00));
    }

    @Test
    @DisplayName("Deve criar serviço do catálogo com sucesso")
    void deveCriarServicoComSucesso() {
        assertThat(servico.getId()).isNotNull();
        assertThat(servico.getCodigo()).isEqualTo("SRV-001");
        assertThat(servico.getNome()).isEqualTo("Alinhamento 3D");
        assertThat(servico.getDescricao()).isEqualTo("Alinhamento completo das 4 rodas");
        assertThat(servico.getValorUnitario()).isEqualTo(Dinheiro.de(120.00));
        assertThat(servico.getCriadoEm()).isNotNull();
        assertThat(servico.getAtualizadoEm()).isNull();
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no construtor de Servico")
    void deveValidarCamposNoConstrutor() {
        assertThatThrownBy(() -> Servico.criarNovo(null, "Nome", "Desc", Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Servico.criarNovo("   ", "Nome", "Desc", Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Servico.criarNovo("SRV-1", null, "Desc", Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Servico.criarNovo("SRV-1", "   ", "Desc", Dinheiro.de(50.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> Servico.criarNovo("SRV-1", "Nome", "Desc", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve atualizar dados e valor unitário do serviço")
    void deveAtualizarDadosDoServico() {
        servico.atualizarDados("Alinhamento e Balanceamento", "Revisão completa", Dinheiro.de(150.00));

        assertThat(servico.getNome()).isEqualTo("Alinhamento e Balanceamento");
        assertThat(servico.getDescricao()).isEqualTo("Revisão completa");
        assertThat(servico.getValorUnitario()).isEqualTo(Dinheiro.de(150.00));
        assertThat(servico.getAtualizadoEm()).isNotNull();

        servico.atualizarDados("Alinhamento Simples", "  ", Dinheiro.de(100.00));
        assertThat(servico.getDescricao()).isNull();

        assertThatThrownBy(() -> servico.atualizarDados(null, "Desc", Dinheiro.de(100.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> servico.atualizarDados("  ", "Desc", Dinheiro.de(100.00)))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> servico.atualizarDados("Nome", "Desc", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve verificar igualdade e hash code de Servico")
    void deveVerificarIgualdade() {
        UUID id = UUID.randomUUID();
        Servico s1 = new Servico(id, "SRV-1", "Nome 1", null, Dinheiro.de(80.00), null, null);
        Servico s2 = new Servico(id, "SRV-2", "Nome 2", "   ", Dinheiro.de(90.00), null, null);
        Servico s3 = Servico.criarNovo("SRV-3", "Nome 3", null, Dinheiro.de(100.00));

        assertThat(s1).isEqualTo(s1);
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        assertThat(s1).isNotEqualTo(s3);
        assertThat(s1).isNotEqualTo(null);
        assertThat(s1).isNotEqualTo("outro");
    }
}

