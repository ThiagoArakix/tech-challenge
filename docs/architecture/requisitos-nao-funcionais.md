# Requisitos Não Funcionais (RNF) - Oficina Mecânica

Classificação dos requisitos não funcionais baseada na taxonomia de Sommerville:

| Grupo | Mnemônico | Categorias |
| :--- | :--- | :--- |
| **Requisitos de Produto** | **DEUS** | **D**esempenho, **E**ficiência (espaço/recursos), **U**sabilidade, **S**egurança/Confiabilidade (dependabilidade) |
| **Requisitos Organizacionais** | **DOA** | **D**esenvolvimento, **O**peracional, **A**mbiental |
| **Requisitos Externos** | **LER** | **L**egal, **É**tico, **R**egulatório |

Cada requisito traz a evidência de onde é atendido no projeto.

---

## 1. Requisitos de Produto (DEUS)

| ID | Categoria | Requisito | Evidência / Atendimento |
| :--- | :--- | :--- | :--- |
| RNF-01 | **D**esempenho | Consultas por documento, placa, código de peça e status da OS devem usar índices. | `V1__init_schema.sql`: `idx_clientes_documento`, `idx_veiculos_placa`, `idx_pecas_codigo`, `idx_ordens_servico_status`. |
| RNF-02 | **D**esempenho | A API deve ser *stateless* para permitir escala horizontal. | `SecurityConfig`: `SessionCreationPolicy.STATELESS`. |
| RNF-03 | **E**ficiência | A imagem de execução deve ser enxuta e respeitar limites de memória do contêiner. | `Dockerfile`: runtime `eclipse-temurin:21-jre-alpine`, `-XX:MaxRAMPercentage=75.0`. |
| RNF-04 | **U**sabilidade | A API deve ser autodocumentada e testável pelo navegador. | Springdoc OpenAPI 3 em `/swagger-ui/index.html` com esquema Bearer. |
| RNF-05 | **U**sabilidade | Erros devem retornar formato padronizado e legível. | `GlobalExceptionHandler` com `ProblemDetail` (RFC 7807). |
| RNF-06 | **U**sabilidade | O cliente deve acompanhar e aprovar a OS sem criar conta. | Rotas `/api/v1/public/**` com `permitAll()`. |
| RNF-07 | **S**egurança | Rotas administrativas exigem autenticação JWT e papel adequado (RBAC). | `SecurityConfig` + `JwtAuthenticationFilter`; métricas restritas a `ROLE_ADMIN`. |
| RNF-08 | **S**egurança | Senhas nunca armazenadas em texto puro. | `BCryptPasswordEncoder`. |
| RNF-09 | **S**egurança | Identificadores expostos não podem ser sequenciais/previsíveis. | Chaves primárias `UUID`. |
| RNF-10 | **S**egurança / Confiabilidade | A aprovação do orçamento (baixa de estoque + mudança de status) deve ser atômica. | `@Transactional` nos endpoints de aprovação; PostgreSQL ACID (ADR-002). |
| RNF-11 | Confiabilidade | O estoque nunca pode ficar negativo, mesmo diante de bug de aplicação. | Invariante em `Peca.debitarEstoque` + `CHECK (saldo_estoque >= 0)`. |

---

## 2. Requisitos Organizacionais (DOA)

| ID | Categoria | Requisito | Evidência / Atendimento |
| :--- | :--- | :--- | :--- |
| RNF-12 | **D**esenvolvimento | Linguagem e plataforma: Java 21 LTS e Spring Boot 3. | `pom.xml`. |
| RNF-13 | **D**esenvolvimento | Domínio isolado de frameworks (Clean Architecture). | Pacote `domain` sem imports Spring/JPA/Jakarta (ADR-001). |
| RNF-14 | **D**esenvolvimento | Cobertura mínima de 80% (linhas e branches) como barreira de build. | `jacoco-maven-plugin` `check` na fase `verify`, `minimum 0.80`. |
| RNF-15 | **D**esenvolvimento | Esquema de banco versionado e reprodutível. | Flyway `db/migration`. |
| RNF-16 | **O**peracional | Execução local com um único comando. | `docker compose up -d --build`. |
| RNF-17 | **O**peracional | A API só sobe após o banco estar saudável. | `healthcheck pg_isready` + `depends_on: condition: service_healthy`. |
| RNF-18 | **A**mbiental | Execução multiplataforma (Linux, WSL2, macOS, Windows) via contêineres. | Docker multi-stage; testes também executáveis via imagem `maven:3.9.8-eclipse-temurin-21`. |
| RNF-19 | **A**mbiental | Processo da aplicação executa sem privilégios de root. | `Dockerfile`: `USER appuser`. |

---

## 3. Requisitos Externos (LER)

| ID | Categoria | Requisito | Evidência / Atendimento |
| :--- | :--- | :--- | :--- |
| RNF-20 | **L**egal (LGPD) | Dados pessoais (CPF/CNPJ, e-mail, telefone) só podem ser acessados por usuários autenticados. | Rotas `/api/v1/clientes/**` protegidas por JWT; a consulta pública da OS retorna apenas o status (`StatusOSOutput`), sem dados pessoais. |
| RNF-21 | **L**egal | Documentos fiscais de identificação devem ser válidos. | `CpfCnpj` com validação matemática dos dígitos verificadores. |
| RNF-22 | **É**tico | A execução de serviços e a cobrança de peças só ocorrem após o aceite explícito do cliente. | Máquina de estados: `AGUARDANDO_APROVACAO → EM_EXECUCAO` apenas via aprovação; baixa de estoque somente nesse momento. |
| RNF-23 | **É**tico | O orçamento submetido não pode ser alterado sem o conhecimento do cliente. | `StatusOS.permiteEdicaoItens()` bloqueia edição após o diagnóstico. |
| RNF-24 | **R**egulatório | Placas devem seguir os padrões do CONTRAN (tradicional e Mercosul). | `Placa` com regex `ABC-1234` e `ABC1D23`. |
| RNF-25 | **R**egulatório | Valores monetários com precisão de centavos e arredondamento comercial. | `Dinheiro` com `BigDecimal`, escala 2, `HALF_UP`; colunas `NUMERIC(12,2)`. |

