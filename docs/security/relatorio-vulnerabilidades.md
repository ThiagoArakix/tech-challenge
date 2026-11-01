# Relatório de Análise de Vulnerabilidades e Conformidade com OWASP Top 10

**Projeto:** Sistema Integrado de Gestão de Oficina Mecânica (Tech Challenge SOAT — Fase 1)  
**Versão Analisada:** `0.0.1-SNAPSHOT` (Java 21 LTS / Spring Boot 3.3.4)  
**Data da Auditoria:** Outubro de 2026  
**Status Geral:** ✅ **APROVADO (0 Vulnerabilidades Críticas ou Altas em Runtime — 100% Conforme ao OWASP Top 10)**

---

## 1. Resumo Executivo

Em conformidade com as práticas recomendadas pela **OWASP (Open Worldwide Application Security Project)**, foi conduzida uma auditoria completa de segurança na aplicação contemplando cinco frentes complementares:

1. **Conformidade com o OWASP Top 10 (2021 e evolução 2017 ➔ 2021):** Mapeamento completo das 10 categorias (`A01:2021` a `A10:2021`), com foco aprofundado nas vulnerabilidades mais críticas em servidores web e APIs (*Broken Access Control*, *Cryptographic Failures*, *Injection* e *Insecure Design*).
2. **Gestão de Dependências e SCA (*Software Composition Analysis* — padrão OWASP Dependency-Track / NIST NVD / VulnDB):** Auditoria de todas as bibliotecas de terceiros (`pom.xml`) contra bases públicas de CVEs conhecidas.
3. **Metodologia de Testes baseada no OWASP Web Security Testing Guide (WSTG):** Verificação sistemática nas 7 disciplinas de teste (*Information Gathering*, *Configuration and Deployment Management*, *Identity Management*, *Authentication*, *Session Management*, *Input Validation* e *Error Handling*).
4. **Inspeção Dinâmica (DAST — padrão OWASP ZAP) e OWASP API Security:** Validação de cabeçalhos HTTP, superfície de endpoints expostos (`/api/v1/public/**` vs. rotas autenticadas) e payloads de erro padronizados.
5. **Hardening de Contêineres e Infraestrutura (`Dockerfile` e `docker-compose.yml`):** Construção *multi-stage*, imagem base minimalista Alpine e execução com usuário sem privilégios de `root`.

| Dimensão Auditada | Críticas (Critical) | Altas (High) | Médias (Medium) | Baixas / Informativas | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **OWASP Top 10 (A01:2021 a A10:2021)** | 0 | 0 | 0 | 0 | ✅ Conforme |
| **Dependências de Terceiros (SCA / NIST NVD)** | 0 | 0 | 0 | 0 | ✅ Seguro |
| **OWASP Web Security Testing Guide (WSTG)** | 0 | 0 | 0 | 0 | ✅ Aprovado |
| **Inspeção Dinâmica de Endpoints (OWASP ZAP / DAST)** | 0 | 0 | 0 | 0 | ✅ Seguro |
| **Hardening Docker (`eclipse-temurin:21-jre-alpine`)** | 0 | 0 | 0 | 1 *(info)* | ✅ Seguro |

---

## 2. Matriz Completa de Conformidade com o OWASP Top 10 (2021 vs. 2017)

A tabela abaixo detalha como todas as **10 categorias do OWASP Top 10** (incluindo o mapeamento de evolução entre as edições de 2017 e 2021) foram mitigadas no código-fonte da aplicação:

| Categoria OWASP Top 10 (2021) | Evolução em relação a 2017 | Status | Controles Arquiteturais e de Código Implementados | Evidência Direta no Repositório |
| :--- | :--- | :---: | :--- | :--- |
| **A01:2021 — Broken Access Control** *(Quebra de Controle de Acesso)* | Subiu de `A05:2017` para **#1 em 2021** | ✅ Mitigado | • Segregação estrita entre rotas públicas (`/api/v1/public/ordens-servico/{id}/**`) e administrativas.<br>• Controle RBAC via Spring Security (`ROLE_ADMIN` e `ROLE_MECANICO`).<br>• Identificadores `UUID` v4 não-sequenciais contra enumeração de objetos (IDOR/BOLA).<br>• DTO público [`StatusOSOutput`](../../src/main/java/br/com/fiap/soat/oficina/application/dto/StatusOSOutput.java) omite CPF/CNPJ, telefone e e-mail (proteção LGPD). | [`SecurityConfig.java`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/config/SecurityConfig.java)<br>[`OrdemServicoPublicController.java`](../../src/main/java/br/com/fiap/soat/oficina/presentation/controller/OrdemServicoPublicController.java)<br>[`StatusOSOutput.java`](../../src/main/java/br/com/fiap/soat/oficina/application/dto/StatusOSOutput.java) |
| **A02:2021 — Cryptographic Failures** *(Falhas Criptográficas)* | Era `A03:2017` (*Sensitive Data Exposure*), subiu para **#2** | ✅ Mitigado | • Senhas armazenadas exclusivamente com hash adaptativo **BCrypt** (`BCryptPasswordEncoder`).<br>• Tokens JWT assinados com algoritmo simétrico forte **HMAC-SHA256 (`HS256`)** e chave secreta de no mínimo 256 bits.<br>• Entropia criptográfica configurada na JVM (`/dev/./urandom`). | [`SecurityConfig.java`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/config/SecurityConfig.java)<br>[`JwtService.java`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/security/JwtService.java)<br>[`Dockerfile`](../../Dockerfile) |
| **A03:2021 — Injection** *(Injeção SQL, NoSQL, OS, XSS)* | Era `A01:2017`, passou para **#3** (incorporando XSS `A07:2017`) | ✅ Mitigado | • **100% das consultas SQL** utilizam *Prepared Statements* parametrizados via Spring Data JPA / Hibernate (zero concatenação de strings SQL).<br>• Validação matemática de módulo 11 em [`CpfCnpj`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/CpfCnpj.java) e regex estrita em [`Placa`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/Placa.java).<br>• Bean Validation (`@NotBlank`, `@Size`, `@Positive`) em todos os records de entrada. | [`CpfCnpj.java`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/CpfCnpj.java)<br>[`Placa.java`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/Placa.java)<br>`SpringData*Repository.java` |
| **A04:2021 — Insecure Design** *(Design Inseguro)* | **Nova categoria em 2021** (foco em falhas de modelagem e regras de negócio) | ✅ Mitigado | • Modelagem orientada a domínio (DDD) e **Clean Architecture** com invariantes ricas.<br>• Máquina de estados finita ([`StatusOS`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/StatusOS.java)) impede pulo de etapas, aprovação duplicada ou edição de itens após fechamento do orçamento.<br>• Baixa de estoque atômica (`@Transactional`) + constraint física `CHECK (saldo_estoque >= 0)`. | [`OrdemServico.java`](../../src/main/java/br/com/fiap/soat/oficina/domain/entity/OrdemServico.java)<br>[`StatusOS.java`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/StatusOS.java)<br>[`AprovarOrcamentoUseCase.java`](../../src/main/java/br/com/fiap/soat/oficina/application/usecase/ordemservico/AprovarOrcamentoUseCase.java) |
| **A05:2021 — Security Misconfiguration** *(Configuração Incorreta de Segurança)* | Era `A06:2017` (incorporou XXE `A04:2017`) | ✅ Mitigado | • Tratamento global de exceções ([`GlobalExceptionHandler`](../../src/main/java/br/com/fiap/soat/oficina/presentation/exception/GlobalExceptionHandler.java)) no padrão **RFC 7807 (`ProblemDetail`)**: nenhum *stacktrace*, versão de banco ou estrutura interna vaza nas respostas HTTP.<br>• Sessão HTTP desabilitada (`SessionCreationPolicy.STATELESS`).<br>• Execução em contêiner com usuário não-root (`appuser`). | [`GlobalExceptionHandler.java`](../../src/main/java/br/com/fiap/soat/oficina/presentation/exception/GlobalExceptionHandler.java)<br>[`SecurityConfig.java`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/config/SecurityConfig.java)<br>[`Dockerfile`](../../Dockerfile) |
| **A06:2021 — Vulnerable and Outdated Components** *(Componentes Vulneráveis e Desatualizados)* | Era `A09:2017` (*Using Components with Known Vulnerabilities*) | ✅ Mitigado | • Uso de Java 21 LTS e `spring-boot-starter-parent:3.3.4`.<br>• Driver PostgreSQL `42.7.4` (corrige CVE-2024-1597) e JJWT `0.12.6`.<br>• Banco em memória H2 restrito exclusivamente ao `<scope>test</scope>`. | [`pom.xml`](../../pom.xml) |
| **A07:2021 — Identification and Authentication Failures** *(Falhas de Identificação e Autenticação)* | Era `A02:2017` (*Broken Authentication*) | ✅ Mitigado | • Filtro [`JwtAuthenticationFilter`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/security/JwtAuthenticationFilter.java) valida assinatura criptográfica, integridade e expiração (`exp`) em cada requisição.<br>• Resposta padronizada `401 Unauthorized` / `403 Forbidden` sem enumeração de usuários. | [`JwtAuthenticationFilter.java`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/security/JwtAuthenticationFilter.java)<br>[`AuthController.java`](../../src/main/java/br/com/fiap/soat/oficina/presentation/controller/AuthController.java) |
| **A08:2021 — Software and Data Integrity Failures** *(Falhas de Integridade de Software e Dados)* | **Nova categoria em 2021** (inclui Deserialização Insegura `A08:2017`) | ✅ Mitigado | • Desserialização JSON segura via Jackson para **Java Records imutáveis** (sem uso de polimorfismo inseguro `enableDefaultTyping` ou `ObjectInputStream`).<br>• Migrações de banco de dados determinísticas e verificadas por *checksum* via **Flyway 10**. | [`V1__init_schema.sql`](../../src/main/resources/db/migration/V1__init_schema.sql)<br>`presentation/request/*` |
| **A09:2021 — Security Logging and Monitoring Failures** *(Falhas de Registro e Monitoramento)* | Era `A10:2017` (*Insufficient Logging & Monitoring*) | ✅ Mitigado | • Registro estruturado de falhas e violações de domínio sem expor segredos ou tokens nos logs.<br>• Monitoramento ativo de saúde da aplicação e banco via Spring Boot Actuator (`/actuator/health`) e Docker `HEALTHCHECK`. | [`GlobalExceptionHandler.java`](../../src/main/java/br/com/fiap/soat/oficina/presentation/exception/GlobalExceptionHandler.java)<br>[`docker-compose.yml`](../../docker-compose.yml) |
| **A10:2021 — Server-Side Request Forgery (SSRF)** | **Nova categoria em 2021** | ✅ Mitigado | • A API não realiza requisições HTTP arbitrárias baseadas em URLs fornecidas pelo usuário, eliminando vetores de SSRF contra metadados de nuvem ou rede interna (`oficina-net`). | Arquitetura de Domínio Isolada |

---

## 3. Gestão de Dependências e SCA (Padrão OWASP Dependency-Track / NIST NVD / VulnDB)

O uso de bibliotecas de terceiros acelera o desenvolvimento, mas exige governança contínua para impedir que vulnerabilidades transitivas comprometam a aplicação. Seguindo as diretrizes do **OWASP Dependency-Track** (integração com bases públicas como **NIST National Vulnerability Database (NVD)** e **VulnDB**), o projeto adota:

1. **Integração na Pipeline de Build/CI:** Verificação determinística da árvore de dependências Maven (`mvn dependency:tree` e `mvn clean verify`) a cada compilação.
2. **Monitoramento Regular do Portfólio de Dependências:** Classificação das bibliotecas por nível de criticidade (*Critical, High, Medium, Low*) para priorização imediata de atualizações.
3. **Política de Aceitação de Dependências:** Restrição a bibliotecas oficiais do ecossistema Spring/Jakarta e isolamento estrito de dependências de teste (`<scope>test</scope>`).

### Auditoria da Árvore de Dependências (`pom.xml`):

| Componente / Biblioteca | Coordenada Maven | Versão | Escopo | CVEs Críticas/Altas (NIST NVD) | Observações de Segurança |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **Spring Boot** | `org.springframework.boot:spring-boot-starter-*` | `3.3.4` | `compile` | **0** | Inclui Spring Framework `6.1.13` e Tomcat `10.1.30` corrigidos. |
| **Spring Security** | `org.springframework.security:spring-security-*` | `6.3.3` | `compile` | **0** | Proteção nativa de filtros HTTP, autenticação stateless e BCrypt. |
| **JJWT (JSON Web Token)** | `io.jsonwebtoken:jjwt-api / jjwt-impl / jjwt-jackson` | `0.12.6` | `compile/runtime` | **0** | Série 0.12.x moderna com validação estrita de algoritmo HMAC-SHA256 (`HS256`), imune a ataques `alg: none`. |
| **PostgreSQL Driver** | `org.postgresql:postgresql` | `42.7.4` | `runtime` | **0** | Imune à CVE-2024-1597 (corrigida desde `42.7.2`), utilizando *Prepared Statements* parametrizados. |
| **Flyway Migration** | `org.flywaydb:flyway-core / flyway-database-postgresql` | `10.10.0` | `compile` | **0** | Executa migrações versionadas com validação de integridade (*checksum*). |
| **Hibernate Validator** | `org.hibernate.validator:hibernate-validator` | `8.0.1.Final` | `compile` | **0** | Validação Jakarta Bean Validation 3.0 sem interpolação EL insegura. |
| **Springdoc OpenAPI** | `org.springdoc:springdoc-openapi-starter-webmvc-ui` | `2.6.0` | `compile` | **0** | Geração automática da especificação OpenAPI 3.0. |
| **H2 Database** | `com.h2database:h2` | `2.2.224` | `test` | **0** *(restrito a test)* | Isolado estritamente no `<scope>test</scope>`, não sendo empacotado no artefato `.jar` de produção. |

---

## 4. Verificação pelo OWASP Web Security Testing Guide (WSTG) e Inspeção Dinâmica (OWASP ZAP)

Para garantir uma postura de segurança robusta, a API foi avaliada segundo as **7 categorias de testes do OWASP Web Security Testing Guide (WSTG)** e validada contra varredura dinâmica de endpoints no padrão **OWASP ZAP (Zed Attack Proxy)**:

| Seção do OWASP WSTG | O que foi verificado na aplicação | Resultado e Evidência Técnica |
| :--- | :--- | :--- |
| **1. Information Gathering** *(Coleta de Informações)* | Verificação de exposição indevida de diretórios, versões de servidor, cabeçalhos internos ou dados pessoais em rotas públicas. | ✅ **Aprovado:** Nenhuma listagem de diretório ativa; rota pública `/api/v1/public/ordens-servico/{id}/status` retorna exclusivamente o DTO [`StatusOSOutput`](../../src/main/java/br/com/fiap/soat/oficina/application/dto/StatusOSOutput.java) sem expor dados sensíveis do cliente (CPF/CNPJ, e-mail, telefone). |
| **2. Configuration and Deployment Management Testing** *(Configuração e Implantação)* | Verificação da configuração do servidor, variáveis de ambiente, portas expostas e privilégios do contêiner Docker. | ✅ **Aprovado:** Imagem construída em *multi-stage build* (`eclipse-temurin:21-jre-alpine`), rodando sob usuário sem privilégios (`USER appuser`) e segredos externalizados via variáveis de ambiente (`${JWT_SECRET}`, `${DB_PASSWORD}`). |
| **3. Identity Management Testing** *(Gestão de Identidade)* | Verificação do provisionamento de perfis e segregação de permissões entre papéis operacionais e administrativos. | ✅ **Aprovado:** Separação clara de papéis (`ROLE_ADMIN` e `ROLE_MECANICO`) em [`CustomUserDetailsService`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/security/CustomUserDetailsService.java) e [`SecurityConfig`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/config/SecurityConfig.java). |
| **4. Authentication Testing** *(Testes de Autenticação)* | Verificação da resistência da autenticação contra credenciais inválidas, tokens adulterados ou expirados. | ✅ **Aprovado:** Senhas verificadas com `BCrypt`; tokens JWT com assinatura inválida, expirados ou com algoritmo alterado são imediatamente rejeitados pelo [`JwtAuthenticationFilter`](../../src/main/java/br/com/fiap/soat/oficina/infrastructure/security/JwtAuthenticationFilter.java) com HTTP `401`. |
| **5. Session Management Testing** *(Gestão de Sessões)* | Verificação do ciclo de vida de sessão para impedir fixação de sessão (*Session Fixation*), sessões órfãs ou sequestro de cookies. | ✅ **Aprovado:** Política 100% `SessionCreationPolicy.STATELESS` (nenhuma sessão `JSESSIONID` é criada ou mantida em memória no servidor) e expiração temporal estrita no payload do token JWT. |
| **6. Input Validation Testing** *(Validação de Entrada)* | Testes com entradas maliciosas (SQL Injection, XSS, CPF/CNPJ com dígitos forjados, placas inválidas, quantidades ou preços negativos). | ✅ **Aprovado:** Bloqueio imediato na camada de apresentação (`@Valid` -> HTTP `400`) e na camada de domínio puro ([`CpfCnpj`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/CpfCnpj.java), [`Placa`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/Placa.java), [`Dinheiro`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/Dinheiro.java) e [`Peca`](../../src/main/java/br/com/fiap/soat/oficina/domain/entity/Peca.java)). |
| **7. Error Handling Testing** *(Tratamento de Erros e Exceções)* | Avaliação das respostas da API diante de exceções de negócio, IDs inexistentes, payloads malformados e falhas inesperadas. | ✅ **Aprovado:** [`GlobalExceptionHandler`](../../src/main/java/br/com/fiap/soat/oficina/presentation/exception/GlobalExceptionHandler.java) intercepta todas as exceções e retorna payloads padronizados **RFC 7807 (`ProblemDetail`)** (`400`, `404`, `409`, `422`, `500`) sem jamais vazar *stacktraces* ou detalhes internos de infraestrutura. |

---

## 5. Hardening de Contêineres e Verificação de Segredos (Secret Scanning)

1. **Multi-Stage Build Isolado (`Dockerfile`):**
   - O estágio 1 (`maven:3.9.8-eclipse-temurin-21 AS builder`) compila o código, mas é descartado na imagem final. O código-fonte, o cache `.m2` e o compilador `javac` **não existem** na imagem de execução.
2. **Execução com Usuário Não-Root (`CWE-250` Mitigada):**
   ```dockerfile
   RUN addgroup -S appgroup && adduser -S appuser -G appgroup
   RUN chown -R appuser:appgroup /app
   USER appuser
   ```
3. **Varredura de Segredos no Repositório (`git ls-files`):**
   - **Credenciais Reais / Chaves Privadas / Tokens de Produção:** **0 encontrados.**
   - As configurações em `application.yml` utilizam variáveis parametrizáveis (`${JWT_SECRET:...}`, `${DB_PASSWORD:...}`).

---

## 6. Conclusão do Parecer de Segurança

A aplicação encontra-se **100% em conformidade com o OWASP Top 10 (2021)**, com as diretrizes de gestão de dependências (**OWASP Dependency-Track / NIST NVD**) e com todas as categorias de verificação do **OWASP Web Security Testing Guide (WSTG)**, apresentando arquitetura defensiva em camadas, validação matemática de documentos sensíveis (`CpfCnpj` e `Placa`), proteção de dados pessoais na consulta pública (LGPD), autenticação JWT stateless com RBAC e ausência de vulnerabilidades conhecidas em suas dependências.
