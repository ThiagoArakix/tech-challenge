# ADR-003: Autenticação Stateless baseada em JSON Web Tokens (JWT) e RBAC

* **Data:** 10 de Outubro de 2026
* **Status:** Aprovado
* **Autor:** Thiago Costa Araki

---

## 1. Contexto

A API RESTful da **Oficina Mecânica** atende a dois perfis distintos de interação:
1. **Público Externo (Clientes):** Precisam consultar o status do reparo de seu veículo e aprovar orçamentos técnicos de maneira simplificada, sem atrito de registro de senhas ou logins pesados.
2. **Operadores Internos (Administradores e Mecânicos):** Precisam acessar endpoints administrativos, executar movimentações de estoque, emitir relatórios de métricas e realizar intervenções nas Ordens de Serviço.

Era necessário definir uma estratégia de segurança escalável, independente de sessões em memória no servidor (*stateless*) e protegida contra falsificação.

---

## 2. Decisão

Decidiu-se pela implementação de autenticação e autorização **Stateless baseada em Bearer JWT** utilizando **Spring Security 6** e a biblioteca **JJWT 0.12.6 (Java JWT)** com algoritmo de assinatura digital criptográfica **HMAC-SHA256**:

### Principais Aspectos da Implementação:
1. **Arquitetura Stateless:** A política de criação de sessão do Spring Security foi configurada como `SessionCreationPolicy.STATELESS`. O servidor não armazena sessões HTTP em memória, tornando a API horizontalmente escalável.
2. **Assinatura e Validação Criptográfica:** Tokens são assinados com chave simétrica secreta de 256 bits (`security.jwt.secret`) e possuem tempo de expiração configurável (padrão de 1 hora no ambiente de produção).
3. **Filtro de Interceptação Customizado:** O [JwtAuthenticationFilter](file://wsl.localhost/Ubuntu-24.04/home/thiago/projetos/oficina-mecanica/src/main/java/br/com/fiap/soat/oficina/infrastructure/security/JwtAuthenticationFilter.java) herda de `OncePerRequestFilter`, extrai o cabeçalho `Authorization: Bearer <token>`, valida a integridade/expiração do token e popula o `SecurityContextHolder`.
4. **Controle de Acesso Baseado em Papéis (RBAC):**
   - `ROLE_ADMIN`: Acesso completo ao sistema, relatórios de tempo médio (`/api/v1/ordens-servico/metricas/**`) e gestão de clientes, veículos e catálogo de peças.
   - `ROLE_MECANICO`: Acesso às rotas operacionais de diagnóstico e execução técnica de serviços.
5. **Endpoints Públicos Permissivos (`permitAll()`):**
   - `/api/v1/auth/**`: Endpoint de login para geração do token.
   - `/api/v1/public/**`: Consulta pública do status da OS e aprovação do orçamento pelo cliente via código identificador único (UUID seguro da OS).
   - `/swagger-ui/**`, `/v3/api-docs/**`: Documentação interativa OpenAPI 3 / Swagger.
6. **Contas de Demonstração para Avaliação:** Provisão de credenciais canônicas criptografadas via `BCryptPasswordEncoder` no serviço [CustomUserDetailsService](file://wsl.localhost/Ubuntu-24.04/home/thiago/projetos/oficina-mecanica/src/main/java/br/com/fiap/soat/oficina/infrastructure/security/CustomUserDetailsService.java):
   - `admin` / `admin123` (`ROLE_ADMIN`)
   - `mecanico` / `mecanico123` (`ROLE_MECANICO`)

---

## 3. Consequências

### Positivas:
- **Desacoplamento e Escalabilidade:** Sem estado em servidor, novos nós da API podem ser instanciados sem necessidade de sincronização de sessões (*sticky sessions* ou Redis de sessão).
- **Padronização de Mercado:** JWT é o padrão de fato da indústria para APIs RESTful modernas, compatível com qualquer cliente front-end (web, mobile ou Postman).
- **Autonomia para Clientes Públicos:** Clientes consultam e aprovam orçamentos sem atritos burocráticos de criação prévia de login e senha, aumentando a taxa de aprovação de serviços na oficina.
- **Rastreabilidade de Auditoria:** O token carrega *claims* de identificação (`sub` e `roles`) diretamente no payload, facilitando auditoria e *logs*.

### Negativas / Trade-offs:
- **Invalidação Prévia de Tokens:** Em implementações JWT puras sem blacklist centralizada, um token emitido permanece válido até sua expiração natural. Mitigado através de tempo de vida curto do token (1 hora).

---

## 4. Alternativas Consideradas

1. **Sessões HTTP Tradicionais via Cookies (Stateful Session):**
   - *Motivo da rejeição:* Gera acoplamento com o servidor, exige gerenciador de sessões distribuído e apresenta vulnerabilidades a ataques CSRF em clientes não-navegador.
2. **HTTP Basic Authentication em Cada Requisição:**
   - *Motivo da rejeição:* Exige tráfego repetido de usuário e senha na rede a cada chamada, aumentando superfície de ataque e sem suporte a expiração transitória de credencial.
3. **Servidor Externo de Identidade OAuth2 / OpenID Connect (ex.: Keycloak ou Auth0):**
   - *Motivo da rejeição:* Introduziria complexidade operacional excessiva e dependência de infraestrutura externa desnecessária para o escopo atual do sistema.

