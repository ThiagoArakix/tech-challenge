package br.com.fiap.soat.oficina.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração E2E - Ciclo Completo de Atendimento da Oficina")
class OrdemServicoFluxoCompletoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Teste 1: Fluxo E2E Completo da Ordem de Serviço com Autenticação, Diagnóstico, Aprovação Pública e Baixa de Estoque")
    void deveExecutarFluxoCompletoE2E() throws Exception {
        // ---------------------------------------------------------------------
        // 1. Autenticação via /api/v1/auth/login
        // ---------------------------------------------------------------------
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
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = "Bearer " + loginJson.get("token").asText();

        // ---------------------------------------------------------------------
        // 2. Cadastrar Cliente com CPF válido
        // ---------------------------------------------------------------------
        String clientePayload = """
                {
                    "nome": "João da Silva",
                    "documento": "52998224725",
                    "email": "joao.silva@email.com",
                    "telefone": "11988887777"
                }
                """;

        MvcResult clienteResult = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID clienteId = UUID.fromString(objectMapper.readTree(clienteResult.getResponse().getContentAsString()).get("id").asText());

        // ---------------------------------------------------------------------
        // 3. Cadastrar Veículo com Placa Mercosul
        // ---------------------------------------------------------------------
        String veiculoPayload = String.format("""
                {
                    "clienteId": "%s",
                    "placa": "ABC1D23",
                    "marca": "Honda",
                    "modelo": "Civic",
                    "anoFabricacao": 2021,
                    "anoModelo": 2022,
                    "cor": "Prata",
                    "quilometragem": 35000
                }
                """, clienteId);

        MvcResult veiculoResult = mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veiculoPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID veiculoId = UUID.fromString(objectMapper.readTree(veiculoResult.getResponse().getContentAsString()).get("id").asText());

        // ---------------------------------------------------------------------
        // 4. Cadastrar Peça com 10 unidades a R$ 150,00 cada
        // ---------------------------------------------------------------------
        String pecaPayload = """
                {
                    "codigo": "DISCO-FREIO-01",
                    "nome": "Disco de Freio Ventilado",
                    "descricao": "Disco dianteiro original",
                    "valorUnitario": 150.00,
                    "saldoEstoque": 10,
                    "estoqueMinimo": 2
                }
                """;

        MvcResult pecaResult = mockMvc.perform(post("/api/v1/pecas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pecaPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        UUID pecaId = UUID.fromString(objectMapper.readTree(pecaResult.getResponse().getContentAsString()).get("id").asText());

        // ---------------------------------------------------------------------
        // 5. Abrir Ordem de Serviço (Status inicial RECEBIDA)
        // ---------------------------------------------------------------------
        String osPayload = String.format("""
                {
                    "clienteId": "%s",
                    "veiculoId": "%s",
                    "descricaoProblema": "Vibração excessiva no volante ao frear"
                }
                """, clienteId, veiculoId);

        MvcResult osResult = mockMvc.perform(post("/api/v1/ordens-servico")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(osPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("RECEBIDA"))
                .andReturn();

        UUID osId = UUID.fromString(objectMapper.readTree(osResult.getResponse().getContentAsString()).get("id").asText());

        // ---------------------------------------------------------------------
        // 6. Iniciar Diagnóstico e Registrar Laudo Técnico
        // ---------------------------------------------------------------------
        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/iniciar-diagnostico")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"));

        String diagnosticoPayload = """
                {
                    "diagnostico": "Discos de freio dianteiros empenados necessitando troca imediata"
                }
                """;

        mockMvc.perform(patch("/api/v1/ordens-servico/" + osId + "/diagnostico")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(diagnosticoPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Discos de freio dianteiros empenados necessitando troca imediata"));

        // ---------------------------------------------------------------------
        // 7. Adicionar Serviço (R$ 200,00) e 2 Peças (2x R$ 150,00 = R$ 300,00)
        // ---------------------------------------------------------------------
        String servicoPayload = """
                {
                    "descricao": "Mão de obra substituição dos discos de freio",
                    "quantidade": 1.0,
                    "valorUnitario": 200.00
                }
                """;

        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/itens/servicos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(servicoPayload))
                .andExpect(status().isOk());

        String itemPecaPayload = String.format("""
                {
                    "pecaId": "%s",
                    "quantidade": 2
                }
                """, pecaId);

        MvcResult osComItensResult = mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/itens/pecas")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemPecaPayload))
                .andExpect(status().isOk())
                .andReturn();

        // ---------------------------------------------------------------------
        // 8. Validar Orçamento Total de R$ 500,00 (R$ 200 serviços + R$ 300 peças)
        // ---------------------------------------------------------------------
        JsonNode osComItensJson = objectMapper.readTree(osComItensResult.getResponse().getContentAsString());
        assertThat(osComItensJson.get("valorTotalServicos").asDouble()).isEqualTo(200.00);
        assertThat(osComItensJson.get("valorTotalPecas").asDouble()).isEqualTo(300.00);
        assertThat(osComItensJson.get("valorTotal").asDouble()).isEqualTo(500.00);

        // ---------------------------------------------------------------------
        // 9. Submeter para Aprovação (Status AGUARDANDO_APROVACAO)
        // ---------------------------------------------------------------------
        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/submeter-aprovacao")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"));

        // ---------------------------------------------------------------------
        // 10. Consultar no Endpoint Público sem Token
        // ---------------------------------------------------------------------
        mockMvc.perform(get("/api/v1/public/ordens-servico/" + osId + "/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordemServicoId").value(osId.toString()))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_APROVACAO"))
                .andExpect(jsonPath("$.valorTotal").value(500.00));

        // ---------------------------------------------------------------------
        // 11. Aprovar via Endpoint Público pelo Cliente
        // ---------------------------------------------------------------------
        mockMvc.perform(post("/api/v1/public/ordens-servico/" + osId + "/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_EXECUCAO"));

        // ---------------------------------------------------------------------
        // 12. Validar Baixa Física no Estoque da Peça (10 - 2 = 8 unidades)
        // ---------------------------------------------------------------------
        MvcResult pecaAtualizadaResult = mockMvc.perform(get("/api/v1/pecas")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode pecasJson = objectMapper.readTree(pecaAtualizadaResult.getResponse().getContentAsString());
        JsonNode pecaConsultada = null;
        for (JsonNode p : pecasJson) {
            if (p.get("id").asText().equals(pecaId.toString())) {
                pecaConsultada = p;
                break;
            }
        }
        assertThat(pecaConsultada).isNotNull();
        assertThat(pecaConsultada.get("saldoEstoque").asInt()).isEqualTo(8);

        // ---------------------------------------------------------------------
        // 13. Finalizar Execução da OS (Status FINALIZADA)
        // ---------------------------------------------------------------------
        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/finalizar")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"))
                .andExpect(jsonPath("$.dataFinalizacao").isNotEmpty());

        // ---------------------------------------------------------------------
        // 14. Registrar Entrega do Veículo ao Cliente (Status ENTREGUE)
        // ---------------------------------------------------------------------
        mockMvc.perform(post("/api/v1/ordens-servico/" + osId + "/entregar")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"))
                .andExpect(jsonPath("$.dataEntrega").isNotEmpty());

        // ---------------------------------------------------------------------
        // 15. Consultar Métricas de Tempo Médio de Execução
        // ---------------------------------------------------------------------
        mockMvc.perform(get("/api/v1/ordens-servico/metricas/tempo-medio")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrdensFinalizadas").value(1))
                .andExpect(jsonPath("$.tempoMedioFormatado").isNotEmpty());
    }

    @Test
    @DisplayName("Teste 2: Validação de Invariante de Negócio - Tentativa de abrir OS com veículo de outro cliente deve retornar HTTP 400 e RFC 7807")
    void deveRejeitarAberturaDeOsComVeiculoDeOutroCliente() throws Exception {
        // 1. Autenticação
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

        String token = "Bearer " + objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        // 2. Cadastrar Cliente A
        String clienteAPayload = """
                {
                    "nome": "Cliente A",
                    "documento": "52998224725",
                    "email": "clienteA@email.com",
                    "telefone": "11988880001"
                }
                """;
        MvcResult resClienteA = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteAPayload))
                .andExpect(status().isCreated())
                .andReturn();
        UUID clienteAId = UUID.fromString(objectMapper.readTree(resClienteA.getResponse().getContentAsString()).get("id").asText());

        // 3. Cadastrar Cliente B
        String clienteBPayload = """
                {
                    "nome": "Cliente B",
                    "documento": "11144477735",
                    "email": "clienteB@email.com",
                    "telefone": "11988880002"
                }
                """;
        MvcResult resClienteB = mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteBPayload))
                .andExpect(status().isCreated())
                .andReturn();
        UUID clienteBId = UUID.fromString(objectMapper.readTree(resClienteB.getResponse().getContentAsString()).get("id").asText());

        // 4. Cadastrar Veículo associado ao Cliente B
        String veiculoBPayload = String.format("""
                {
                    "clienteId": "%s",
                    "placa": "XYZ9Z99",
                    "marca": "Ford",
                    "modelo": "Ka",
                    "anoFabricacao": 2019,
                    "anoModelo": 2020,
                    "cor": "Vermelho",
                    "quilometragem": 60000
                }
                """, clienteBId);

        MvcResult resVeiculoB = mockMvc.perform(post("/api/v1/veiculos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(veiculoBPayload))
                .andExpect(status().isCreated())
                .andReturn();
        UUID veiculoBId = UUID.fromString(objectMapper.readTree(resVeiculoB.getResponse().getContentAsString()).get("id").asText());

        // 5. Tentar abrir OS informando Cliente A e Veículo B (invariante violada)
        String osInvalidaPayload = String.format("""
                {
                    "clienteId": "%s",
                    "veiculoId": "%s",
                    "descricaoProblema": "Tentativa de serviço em carro alheio"
                }
                """, clienteAId, veiculoBId);

        mockMvc.perform(post("/api/v1/ordens-servico")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(osInvalidaPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Violação de Regra de Negócio"))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("não pertence ao cliente informado")))
                .andExpect(jsonPath("$.type").value("https://api.oficina.com.br/erros/regra-de-negocio"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}

