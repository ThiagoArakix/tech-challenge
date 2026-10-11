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
    }

    @Test
    @DisplayName("Deve listar peças e consultar alerta de estoque baixo via Controller")
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
    }

    @Test
    @DisplayName("Deve consultar veículos por placa e cliente via Controller")
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

        mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veiculoPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/veiculos/placa/BRA2E19")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Cruze"));

        mockMvc.perform(get("/api/v1/veiculos/cliente/" + clienteId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

