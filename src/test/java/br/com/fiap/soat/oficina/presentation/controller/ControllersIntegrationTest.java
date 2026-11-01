package br.com.fiap.soat.oficina.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - Controllers REST")
class ControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        String loginPayload = """
                {
                    "username": "admin",
                    "password": "admin123"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        token = "Bearer " + objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    @DisplayName("Deve listar clientes e buscar por documento via Controller")
    void deveTestarEndpointsCliente() throws Exception {
        String clientePayload = """
                {
                    "nome": "Fernanda Lima",
                    "documento": "52998224725",
                    "email": "fernanda@email.com",
                    "telefone": "11988887766"
                }
                """;

        MvcResult res = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientePayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID clienteId = UUID.fromString(objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/api/v1/clientes")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/clientes/documento/52998224725")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fernanda Lima"));

        String updatePayload = """
                {
                    "nome": "Fernanda Lima Silva",
                    "email": "fernandasilva@email.com",
                    "telefone": "11999998888"
                }
                """;

        mockMvc.perform(put("/api/v1/clientes/" + clienteId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fernanda Lima Silva"));

        mockMvc.perform(get("/api/v1/clientes/" + clienteId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId.toString()));

        mockMvc.perform(delete("/api/v1/clientes/" + clienteId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve testar CRUD completo de Peças e controle de estoque via Controller")
    void deveTestarEndpointsPeca() throws Exception {
        String pecaPayload = """
                {
                    "codigo": "VELA-NGK-01",
                    "nome": "Vela de Ignição NGK",
                    "descricao": "Vela para motor flex",
                    "valorUnitario": 35.00,
                    "saldoEstoque": 2,
                    "estoqueMinimo": 5
                }
                """;

        MvcResult resPeca = mockMvc.perform(post("/api/v1/pecas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pecaPayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID pecaId = UUID.fromString(objectMapper.readTree(resPeca.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/api/v1/pecas")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/pecas/" + pecaId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("VELA-NGK-01"));

        String updatePecaPayload = """
                {
                    "nome": "Vela de Ignição NGK Iridium",
                    "descricao": "Vela Iridium de alta performance",
                    "valorUnitario": 45.00,
                    "estoqueMinimo": 6
                }
                """;

        mockMvc.perform(put("/api/v1/pecas/" + pecaId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePecaPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Vela de Ignição NGK Iridium"))
                .andExpect(jsonPath("$.valorUnitario").value(45.00));

        mockMvc.perform(patch("/api/v1/pecas/" + pecaId + "/repor-estoque")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoEstoque").value(7));

        mockMvc.perform(patch("/api/v1/pecas/" + pecaId + "/debitar-estoque")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoEstoque").value(5));

        mockMvc.perform(get("/api/v1/pecas/abaixo-minimo")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(delete("/api/v1/pecas/" + pecaId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve testar CRUD completo de Serviços do catálogo via Controller")
    void deveTestarEndpointsServico() throws Exception {
        String servicoPayload = """
                {
                    "codigo": "SRV-FREIO-01",
                    "nome": "Revisão de Freios",
                    "descricao": "Inspeção e limpeza do sistema de freios",
                    "valorUnitario": 150.00
                }
                """;

        MvcResult resServico = mockMvc.perform(post("/api/v1/servicos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(servicoPayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID servicoId = UUID.fromString(objectMapper.readTree(resServico.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/api/v1/servicos")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/servicos/" + servicoId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("SRV-FREIO-01"));

        mockMvc.perform(get("/api/v1/servicos/codigo/SRV-FREIO-01")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Revisão de Freios"));

        String updateServicoPayload = """
                {
                    "nome": "Revisão Completa de Freios ABS",
                    "descricao": "Inspeção, sangria e limpeza do sistema ABS",
                    "valorUnitario": 220.00
                }
                """;

        mockMvc.perform(put("/api/v1/servicos/" + servicoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateServicoPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Revisão Completa de Freios ABS"))
                .andExpect(jsonPath("$.valorUnitario").value(220.00));

        mockMvc.perform(delete("/api/v1/servicos/" + servicoId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve testar CRUD completo de Veículos via Controller")
    void deveTestarEndpointsVeiculo() throws Exception {
        String clientePayload = """
                {
                    "nome": "Paulo Santos",
                    "documento": "11144477735",
                    "email": "paulo@email.com",
                    "telefone": "11977771111"
                }
                """;

        MvcResult res = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientePayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID clienteId = UUID.fromString(objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asText());

        String veiculoPayload = String.format("""
                {
                    "clienteId": "%s",
                    "placa": "BRA2E19",
                    "marca": "Chevrolet",
                    "modelo": "Cruze",
                    "anoFabricacao": 2020,
                    "anoModelo": 2021,
                    "cor": "Branco",
                    "quilometragem": 45000
                }
                """, clienteId);

        MvcResult resVeiculo = mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veiculoPayload))
                .andExpect(status().isCreated())
                .andReturn();

        UUID veiculoId = UUID.fromString(objectMapper.readTree(resVeiculo.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(get("/api/v1/veiculos")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/veiculos/" + veiculoId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("BRA2E19"));

        mockMvc.perform(get("/api/v1/veiculos/placa/BRA2E19")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Cruze"));

        mockMvc.perform(get("/api/v1/veiculos/cliente/" + clienteId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        String updateVeiculoPayload = """
                {
                    "marca": "Chevrolet",
                    "modelo": "Cruze Premier",
                    "cor": "Preto",
                    "quilometragem": 48000
                }
                """;

        mockMvc.perform(put("/api/v1/veiculos/" + veiculoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateVeiculoPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Cruze Premier"))
                .andExpect(jsonPath("$.quilometragem").value(48000));

        mockMvc.perform(delete("/api/v1/veiculos/" + veiculoId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve testar remoção de itens da OS e rejeição pública do orçamento pelo cliente")
    void deveTestarRemocaoItensERejeicaoOrcamento() throws Exception {
        MvcResult resCliente = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Carlos Eduardo",
                                    "documento": "85387431041",
                                    "email": "carlos@email.com",
                                    "telefone": "11966665555"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        UUID clienteId = UUID.fromString(objectMapper.readTree(resCliente.getResponse().getContentAsString()).get("id").asText());

        MvcResult resVeiculo = mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "clienteId": "%s",
                                    "placa": "ABC-1234",
                                    "marca": "Volkswagen",
                                    "modelo": "Golf",
                                    "anoFabricacao": 2019,
                                    "anoModelo": 2020,
                                    "cor": "Cinza",
                                    "quilometragem": 50000
                                }
                                """, clienteId)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID veiculoId = UUID.fromString(objectMapper.readTree(resVeiculo.getResponse().getContentAsString()).get("id").asText());

        MvcResult resPeca = mockMvc.perform(post("/api/v1/pecas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "codigo": "PAST-01",
                                    "nome": "Pastilha Dianteira",
                                    "descricao": "Jogo de pastilhas",
                                    "valorUnitario": 180.00,
                                    "saldoEstoque": 10,
                                    "estoqueMinimo": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        UUID pecaId = UUID.fromString(objectMapper.readTree(resPeca.getResponse().getContentAsString()).get("id").asText());

        MvcResult resOS = mockMvc.perform(post("/api/v1/ordens-servico")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                    "clienteId": "%s",
                                    "veiculoId": "%s",
                                    "descricaoProblema": "Ruído ao frear"
                                }
                                """, clienteId, veiculoId)))
                .andExpect(status().isCreated())
                .andReturn();
        UUID osId = UUID.fromString(objectMapper.readTree(resOS.getResponse().getContentAsString()).get("id").asText());

        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/iniciar-diagnostico")
                        .header("Authorization", token))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/ordens-servico/" + osId + "/diagnostico")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"diagnostico\": \"Troca de pastilhas\"}"))
                .andExpect(status().isOk());

        // Adiciona 2 serviços e remove 1; adiciona 1 peça e remove 1
        MvcResult resAddServ = mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/itens/servicos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\": \"Serviço Extra\", \"quantidade\": 1.0, \"valorUnitario\": 90.00}"))
                .andExpect(status().isOk())
                .andReturn();
        UUID itemServicoId = UUID.fromString(objectMapper.readTree(resAddServ.getResponse().getContentAsString())
                .get("itensServico").get(0).get("id").asText());

        mockMvc.perform(delete("/api/v1/ordens-servico/" + osId + "/itens/servicos/" + itemServicoId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itensServico").isEmpty());

        MvcResult resAddPeca = mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/itens/pecas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"pecaId\": \"%s\", \"quantidade\": 1}", pecaId)))
                .andExpect(status().isOk())
                .andReturn();
        UUID itemPecaId = UUID.fromString(objectMapper.readTree(resAddPeca.getResponse().getContentAsString())
                .get("itensPeca").get(0).get("id").asText());

        mockMvc.perform(delete("/api/v1/ordens-servico/" + osId + "/itens/pecas/" + itemPecaId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itensPeca").isEmpty());

        // Adiciona serviço definitivo e submete para aprovação
        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/itens/servicos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\": \"Mão de obra freio\", \"quantidade\": 1.0, \"valorUnitario\": 150.00}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/submeter-aprovacao")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"));

        // Cliente rejeita o orçamento publicamente
        mockMvc.perform(post("/api/v1/public/ordens-servico/" + osId + "/rejeitar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        // Consulta administrativa de listagem e por ID
        mockMvc.perform(get("/api/v1/ordens-servico/" + osId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        mockMvc.perform(get("/api/v1/ordens-servico?status=CANCELADA")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

