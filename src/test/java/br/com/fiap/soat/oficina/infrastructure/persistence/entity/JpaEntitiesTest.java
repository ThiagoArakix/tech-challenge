package br.com.fiap.soat.oficina.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Unidade - Entidades JPA (Infraestrutura)")
class JpaEntitiesTest {

    @Test
    @DisplayName("Deve testar getters, setters e equals de ClienteJpaEntity")
    void deveTestarClienteJpaEntity() {
        UUID id = UUID.randomUUID();
        Instant agora = Instant.now();
        ClienteJpaEntity c1 = new ClienteJpaEntity(id, "Nome", "11144477735", "email@test.com", "11988887777", agora, agora);
        ClienteJpaEntity c2 = new ClienteJpaEntity();
        c2.setId(id);
        c2.setNome("Nome");
        c2.setDocumento("11144477735");
        c2.setEmail("email@test.com");
        c2.setTelefone("11988887777");
        c2.setCriadoEm(agora);
        c2.setAtualizadoEm(agora);

        assertThat(c1.getId()).isEqualTo(id);
        assertThat(c1.getNome()).isEqualTo("Nome");
        assertThat(c1.getDocumento()).isEqualTo("11144477735");
        assertThat(c1.getEmail()).isEqualTo("email@test.com");
        assertThat(c1.getTelefone()).isEqualTo("11988887777");
        assertThat(c1.getCriadoEm()).isEqualTo(agora);
        assertThat(c1.getAtualizadoEm()).isEqualTo(agora);

        assertThat(c1).isEqualTo(c1);
        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1).isNotEqualTo(new ClienteJpaEntity(UUID.randomUUID(), "Outro", "123", "a@a.com", "123", agora, agora));
        assertThat(c1).isNotEqualTo(null);
        assertThat(c1).isNotEqualTo("outro");
    }

    @Test
    @DisplayName("Deve testar getters, setters e equals de VeiculoJpaEntity")
    void deveTestarVeiculoJpaEntity() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        Instant agora = Instant.now();
        VeiculoJpaEntity v1 = new VeiculoJpaEntity(id, clienteId, "ABC1D23", "Honda", "Civic", 2021, 2022, "Prata", 10000, agora, agora);
        VeiculoJpaEntity v2 = new VeiculoJpaEntity();
        v2.setId(id);
        v2.setClienteId(clienteId);
        v2.setPlaca("ABC1D23");
        v2.setMarca("Honda");
        v2.setModelo("Civic");
        v2.setAnoFabricacao(2021);
        v2.setAnoModelo(2022);
        v2.setCor("Prata");
        v2.setQuilometragem(10000);
        v2.setCriadoEm(agora);
        v2.setAtualizadoEm(agora);

        assertThat(v1.getId()).isEqualTo(id);
        assertThat(v1.getClienteId()).isEqualTo(clienteId);
        assertThat(v1.getPlaca()).isEqualTo("ABC1D23");
        assertThat(v1.getMarca()).isEqualTo("Honda");
        assertThat(v1.getModelo()).isEqualTo("Civic");
        assertThat(v1.getAnoFabricacao()).isEqualTo(2021);
        assertThat(v1.getAnoModelo()).isEqualTo(2022);
        assertThat(v1.getCor()).isEqualTo("Prata");
        assertThat(v1.getQuilometragem()).isEqualTo(10000);
        assertThat(v1.getCriadoEm()).isEqualTo(agora);
        assertThat(v1.getAtualizadoEm()).isEqualTo(agora);

        assertThat(v1).isEqualTo(v1);
        assertThat(v1).isEqualTo(v2);
        assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
        assertThat(v1).isNotEqualTo(new VeiculoJpaEntity());
        assertThat(v1).isNotEqualTo(null);
        assertThat(v1).isNotEqualTo("outro");
    }

    @Test
    @DisplayName("Deve testar getters, setters e equals de PecaJpaEntity")
    void deveTestarPecaJpaEntity() {
        UUID id = UUID.randomUUID();
        Instant agora = Instant.now();
        PecaJpaEntity p1 = new PecaJpaEntity(id, "COD-1", "Vela", "Vela ignicao", BigDecimal.valueOf(30.0), 10, 2, agora, agora);
        PecaJpaEntity p2 = new PecaJpaEntity();
        p2.setId(id);
        p2.setCodigo("COD-1");
        p2.setNome("Vela");
        p2.setDescricao("Vela ignicao");
        p2.setValorUnitario(BigDecimal.valueOf(30.0));
        p2.setSaldoEstoque(10);
        p2.setEstoqueMinimo(2);
        p2.setCriadoEm(agora);
        p2.setAtualizadoEm(agora);

        assertThat(p1.getId()).isEqualTo(id);
        assertThat(p1.getCodigo()).isEqualTo("COD-1");
        assertThat(p1.getNome()).isEqualTo("Vela");
        assertThat(p1.getDescricao()).isEqualTo("Vela ignicao");
        assertThat(p1.getValorUnitario()).isEqualTo(BigDecimal.valueOf(30.0));
        assertThat(p1.getSaldoEstoque()).isEqualTo(10);
        assertThat(p1.getEstoqueMinimo()).isEqualTo(2);
        assertThat(p1.getCriadoEm()).isEqualTo(agora);
        assertThat(p1.getAtualizadoEm()).isEqualTo(agora);

        assertThat(p1).isEqualTo(p1);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(new PecaJpaEntity());
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo("outro");
    }

    @Test
    @DisplayName("Deve testar getters, setters e equals de OrdemServicoJpaEntity")
    void deveTestarOrdemServicoJpaEntity() {
        UUID id = UUID.randomUUID();
        UUID cId = UUID.randomUUID();
        UUID vId = UUID.randomUUID();
        Instant agora = Instant.now();

        OrdemServicoJpaEntity os1 = new OrdemServicoJpaEntity(id, cId, vId, "RECEBIDA", "Barulho", "Pastilha",
                BigDecimal.valueOf(200.0), agora, agora, agora, agora, agora, agora);
        OrdemServicoJpaEntity os2 = new OrdemServicoJpaEntity();
        os2.setId(id);
        os2.setClienteId(cId);
        os2.setVeiculoId(vId);
        os2.setStatus("RECEBIDA");
        os2.setDescricaoProblema("Barulho");
        os2.setDiagnostico("Pastilha");
        os2.setValorTotal(BigDecimal.valueOf(200.0));
        os2.setDataAbertura(agora);
        os2.setDataAprovacao(agora);
        os2.setDataFinalizacao(agora);
        os2.setDataEntrega(agora);
        os2.setCriadoEm(agora);
        os2.setAtualizadoEm(agora);
        os2.setItensServico(new ArrayList<>());
        os2.setItensPeca(new ArrayList<>());

        assertThat(os1.getId()).isEqualTo(id);
        assertThat(os1.getClienteId()).isEqualTo(cId);
        assertThat(os1.getVeiculoId()).isEqualTo(vId);
        assertThat(os1.getStatus()).isEqualTo("RECEBIDA");
        assertThat(os1.getDescricaoProblema()).isEqualTo("Barulho");
        assertThat(os1.getDiagnostico()).isEqualTo("Pastilha");
        assertThat(os1.getValorTotal()).isEqualTo(BigDecimal.valueOf(200.0));
        assertThat(os1.getDataAbertura()).isEqualTo(agora);
        assertThat(os1.getDataAprovacao()).isEqualTo(agora);
        assertThat(os1.getDataFinalizacao()).isEqualTo(agora);
        assertThat(os1.getDataEntrega()).isEqualTo(agora);
        assertThat(os1.getCriadoEm()).isEqualTo(agora);
        assertThat(os1.getAtualizadoEm()).isEqualTo(agora);
        assertThat(os1.getItensServico()).isNotNull();
        assertThat(os1.getItensPeca()).isNotNull();

        assertThat(os1).isEqualTo(os1);
        assertThat(os1).isEqualTo(os2);
        assertThat(os1.hashCode()).isEqualTo(os2.hashCode());
        assertThat(os1).isNotEqualTo(new OrdemServicoJpaEntity());
        assertThat(os1).isNotEqualTo(null);
        assertThat(os1).isNotEqualTo("outro");
    }

    @Test
    @DisplayName("Deve testar getters, setters e equals de ItemServicoJpaEntity")
    void deveTestarItemServicoJpaEntity() {
        UUID id = UUID.randomUUID();
        OrdemServicoJpaEntity os = new OrdemServicoJpaEntity();
        os.setId(UUID.randomUUID());
        Instant agora = Instant.now();

        ItemServicoJpaEntity is1 = new ItemServicoJpaEntity(id, os, "Troca", BigDecimal.ONE, BigDecimal.valueOf(50.0), BigDecimal.valueOf(50.0), agora);
        ItemServicoJpaEntity is2 = new ItemServicoJpaEntity();
        is2.setId(id);
        is2.setOrdemServico(os);
        is2.setDescricao("Troca");
        is2.setQuantidade(BigDecimal.ONE);
        is2.setValorUnitario(BigDecimal.valueOf(50.0));
        is2.setValorTotal(BigDecimal.valueOf(50.0));
        is2.setCriadoEm(agora);

        assertThat(is1.getId()).isEqualTo(id);
        assertThat(is1.getOrdemServico()).isEqualTo(os);
        assertThat(is1.getDescricao()).isEqualTo("Troca");
        assertThat(is1.getQuantidade()).isEqualTo(BigDecimal.ONE);
        assertThat(is1.getValorUnitario()).isEqualTo(BigDecimal.valueOf(50.0));
        assertThat(is1.getValorTotal()).isEqualTo(BigDecimal.valueOf(50.0));
        assertThat(is1.getCriadoEm()).isEqualTo(agora);

        assertThat(is1).isEqualTo(is1);
        assertThat(is1).isEqualTo(is2);
        assertThat(is1.hashCode()).isEqualTo(is2.hashCode());
        assertThat(is1).isNotEqualTo(new ItemServicoJpaEntity());
        assertThat(is1).isNotEqualTo(null);
        assertThat(is1).isNotEqualTo("outro");
    }

    @Test
    @DisplayName("Deve testar getters, setters e equals de ItemPecaJpaEntity")
    void deveTestarItemPecaJpaEntity() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();
        OrdemServicoJpaEntity os = new OrdemServicoJpaEntity();
        os.setId(UUID.randomUUID());
        Instant agora = Instant.now();

        ItemPecaJpaEntity ip1 = new ItemPecaJpaEntity(id, os, pecaId, 2, BigDecimal.valueOf(40.0), BigDecimal.valueOf(80.0), agora);
        ItemPecaJpaEntity ip2 = new ItemPecaJpaEntity();
        ip2.setId(id);
        ip2.setOrdemServico(os);
        ip2.setPecaId(pecaId);
        ip2.setQuantidade(2);
        ip2.setValorUnitario(BigDecimal.valueOf(40.0));
        ip2.setValorTotal(BigDecimal.valueOf(80.0));
        ip2.setCriadoEm(agora);

        assertThat(ip1.getId()).isEqualTo(id);
        assertThat(ip1.getOrdemServico()).isEqualTo(os);
        assertThat(ip1.getPecaId()).isEqualTo(pecaId);
        assertThat(ip1.getQuantidade()).isEqualTo(2);
        assertThat(ip1.getValorUnitario()).isEqualTo(BigDecimal.valueOf(40.0));
        assertThat(ip1.getValorTotal()).isEqualTo(BigDecimal.valueOf(80.0));
        assertThat(ip1.getCriadoEm()).isEqualTo(agora);

        assertThat(ip1).isEqualTo(ip1);
        assertThat(ip1).isEqualTo(ip2);
        assertThat(ip1.hashCode()).isEqualTo(ip2.hashCode());
        assertThat(ip1).isNotEqualTo(new ItemPecaJpaEntity());
        assertThat(ip1).isNotEqualTo(null);
        assertThat(ip1).isNotEqualTo("outro");
    }
}

