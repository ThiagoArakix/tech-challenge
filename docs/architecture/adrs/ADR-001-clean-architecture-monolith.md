# ADR-001: Adoção de Clean Architecture em Estrutura Monolítica Modular

* **Data:** 10 de Outubro de 2026
* **Status:** Aprovado
* **Autores:** Equipe de Arquitetura de Software SOAT (Fase 1)

---

## 1. Contexto

Para o desenvolvimento do sistema de gestão da **Oficina Mecânica** (Tech Challenge SOAT - Fase 1), era necessário definir o estilo arquitetural e o padrão de organização interna do código-fonte. O sistema precisa suportar regras de negócio automotivas complexas (máquina de estados finita da Ordem de Serviço, cálculo de orçamentos, baixa física de inventário e métricas operacionais), mantendo alta testabilidade, baixo acoplamento e conformidade estrita com os princípios de **Domain-Driven Design (DDD)**.

O edital da Fase 1 estabelece critérios eliminatórios que exigem no mínimo 80% de cobertura de código em testes automatizados e isolamento do domínio de negócio em relação a tecnologias e frameworks.

---

## 2. Decisão

Decidiu-se pela construção de um **Monólito Modular** estruturado segundo os princípios da **Clean Architecture** (Arquitetura Limpa / Onion Architecture / Hexagonal Ports and Adapters) em **Java 21**, organizado em quatro camadas bem delimitadas:

1. **Camada de Domínio Puro (`domain`):** Totalmente desacoplada de frameworks (sem anotações `@Entity`, `@Table`, Spring ou Jakarta). Contém as entidades de negócio, agregados, Value Objects imutáveis e interfaces de repositórios (Portas).
2. **Camada de Aplicação (`application`):** Use Cases implementados como classes Java puras que dependem exclusivamente das portas de domínio e DTOs, sem acoplamento a `@Service` ou anotações externas.
3. **Camada de Apresentação (`presentation`):** Controllers Spring MVC expondo endpoints RESTful documentados via OpenAPI 3 e tratamento uniforme de exceções baseado na RFC 7807 (*ProblemDetail*).
4. **Camada de Infraestrutura (`infrastructure`):** Adaptadores de persistência Spring Data JPA, mapeadores bidirecionais (*Mappers*), configuração de injeção de dependência via `@Configuration` e segurança JWT com Spring Security 6.

---

## 3. Consequências

### Positivas:
- **Testabilidade Absoluta:** O domínio puro e os casos de uso podem ser exaustivamente testados por testes de unidade ultrarrápidos com Mockito puro, sem necessidade de carregar contextos do Spring ou bancos em memória. A suíte atingiu mais de 95% de cobertura de linhas e 85% de branches.
- **Independência de Frameworks:** Facilidade de migração de tecnologia ou de banco de dados no futuro sem impacto sobre as regras de negócio centrais.
- **Simplicidade Operacional (Monólito Modular):** Em comparação com microsserviços, elimina problemas de latência de rede, transações distribuídas (Saga), orquestração de contêineres prematura e complexidade de observabilidade distribuída.
- **Prontidão para Futura Decomposição:** Como os agregados (Ordem de Serviço, Cliente, Veículo, Peça) foram modelados respeitando limites transacionais de DDD, o monólito pode ser facilmente dividido em microsserviços em fases futuras (ex.: Fase 2/Fase 3), se houver demanda de escalabilidade independente.

### Negativas / Trade-offs:
- **Maior Quantidade de Classes:** Necessidade de manter DTOs de entrada/saída, JPA Entities e Entidades de Domínio, exigindo conversores/mappers bidirecionais.
- **Curva de Disciplina:** Engenheiros devem manter estrita vigilância para nunca importar pacotes de infraestrutura ou Spring na camada de domínio.

---

## 4. Alternativas Consideradas

1. **Arquitetura em Camadas Tradicional Anêmica (Controller-Service-Repository):**
   - *Motivo da rejeição:* Gera entidades anêmicas fortemente acopladas a anotações `@Entity` do JPA/Hibernate, violando o princípio de domínio puro do DDD e dificultando testes unitários isolados.
2. **Microsserviços Prematuros desde a Fase 1:**
   - *Motivo da rejeição:* O volume transacional da Fase 1 não justifica a sobrecarga operacional de gerenciar múltiplos serviços, bancos distribuídos e consistência eventual para o fluxo crítico de baixa de estoque e aprovação de orçamento.

