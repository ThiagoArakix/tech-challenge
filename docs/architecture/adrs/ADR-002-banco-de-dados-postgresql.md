# ADR-002: Escolha do Banco de Dados Relacional PostgreSQL 16 com Transações ACID

* **Data:** 10 de Outubro de 2026
* **Status:** Aprovado
* **Autores:** Equipe de Arquitetura de Software SOAT (Fase 1)

---

## 1. Contexto

O sistema da **Oficina Mecânica** opera fluxos de missão crítica que envolvem impacto financeiro e integridade física de inventário:
- O orçamento de uma Ordem de Serviço deve calcular valores exatos em moeda corrente (sem imprecisões de ponto flutuante).
- A aprovação de um orçamento pelo cliente deve debitar o estoque físico de múltiplas peças simultaneamente e transicionar a Ordem de Serviço para o estado `EM_EXECUCAO`.
- Se qualquer inconsistência ocorrer durante a baixa de uma peça (ex.: saldo insuficiente concorrente), todas as alterações devem ser abortadas atomicamente.
- O relacionamento entre Veículo e Cliente deve garantir que nenhum veículo fique órfão ou seja cadastrado para clientes inexistentes (`FOREIGN KEY ... ON DELETE RESTRICT`).

Era fundamental escolher um mecanismo de persistência robusto, com conformidade rigorosa aos princípios **ACID** (Atomicidade, Consistência, Isolamento e Durabilidade).

---

## 2. Decisão

Decidiu-se pela adoção do **PostgreSQL 16** (imagem oficial `postgres:16-alpine`), orquestrado via **Docker Compose**, utilizando **Flyway** para controle de versão do esquema (`V1__init_schema.sql`) e **HikariCP** como pool de conexões gerenciado pelo Spring Boot.

Principais fundamentos da escolha:
1. **Garantias ACID Rigorosas:** Transações atômicas garantem que a atualização do status da OS e as baixas de saldo de estoque ocorram dentro do mesmo *commit* relacional ou sofram *rollback* completo em caso de falha.
2. **Integridade Referencial e Constraints Nativas:**
   - `CHECK (saldo_estoque >= 0)` e `CHECK (estoque_minimo >= 0)` em nível de tabela, impedindo fisicamente que qualquer bug de software gere estoque negativo.
   - `CHECK (status IN ('RECEBIDA', 'EM_DIAGNOSTICO', ...))` como barreira secundária à máquina de estados.
   - `FOREIGN KEY` garantindo integridade de vínculos entre clientes, veículos, ordens de serviço e itens associados.
3. **Tipo Nativo `UUID` e Geração Criptográfica:** Uso de chaves primárias do tipo `UUID DEFAULT gen_random_uuid() PRIMARY KEY`, garantindo identificadores universais sem colisões e sem expor sequências previsíveis na API pública.
4. **Precisão Numérica Financeira:** Tipos `NUMERIC(12, 2)` garantem precisão decimal exata para cálculos orçamentários, evitando erros de arredondamento inerentes a tipos de ponto flutuante (*float/double*).
5. **Compatibilidade de Testes:** O esquema SQL criado com tipos ANSI e convenções padronizadas é 100% executável tanto no PostgreSQL 16 de produção/Docker quanto no H2 Database em modo de compatibilidade PostgreSQL (`MODE=PostgreSQL`) durante a suíte de testes de integração rápida.

---

## 3. Consequências

### Positivas:
- **Consistência Absoluta:** Impossibilidade de venda ou débito de peças além do saldo existente no estoque.
- **Rastreabilidade e Evolutividade:** O Flyway garante que qualquer ambiente (desenvolvimento, homologação, produção) aplique exatamente as mesmas migrações de forma idempotente e versionada.
- **Ecossistema Maduro:** Amplo suporte da comunidade, alta performance com índices b-tree configurados nos campos de maior busca (`documento`, `placa`, `codigo`, `status`).

### Negativas / Trade-offs:
- **Esquema Rígido:** Modificações estruturais exigem criação de novos arquivos de migração Flyway (`V2__...sql`), ao contrário de bancos orientados a documentos *schemaless*. No entanto, para o domínio financeiro e operacional de oficinas mecânicas, rigidez de esquema é uma virtude de engenharia, não um defeito.

---

## 4. Alternativas Consideradas

1. **Bancos NoSQL Orientados a Documentos (ex.: MongoDB):**
   - *Motivo da rejeição:* Documentos desnormalizados tornam complexa a garantia de transações atômicas distribuídas entre diferentes coleções (`pecas` e `ordens_servico`), introduzindo risco inaceitável de estoque inconsistente sob concorrência e exigindo validações manuais de integridade referencial na aplicação.
2. **Bancos NoSQL Chave-Valor ou Colunares (ex.: Redis, Cassandra):**
   - *Motivo da rejeição:* Inadequados para modelos relacionais com consultas complexas por filtros combinados (ex.: busca por status da OS, histórico de veículos por cliente, alerta de estoque baixo).
3. **MySQL / MariaDB:**
   - *Motivo da rejeição:* O PostgreSQL oferece suporte superior a tipos estruturados nativos (como `UUID`), índices parciais e conformidade estrita aos padrões SQL e tipos temporais com fuso horário (`TIMESTAMP WITH TIME ZONE`).

