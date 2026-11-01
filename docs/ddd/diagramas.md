# Diagramas de Domain-Driven Design (DDD) e Arquitetura - Oficina Mecânica

Este documento consolida os diagramas estratégicos, táticos, comportamentais e estruturais de **Domain-Driven Design (DDD)** e **Arquitetura de Software**, mantendo correspondência direta (1:1) com o código-fonte implementado no repositório.

> 🎨 **Quadro Público no Miro e Documentos Exportados:**
> - **Quadro Público no Miro (Visualização Direta):** [https://miro.com/app/board/uXjVETBYsng=/?share_link_id=296220385966](https://miro.com/app/board/uXjVETBYsng=/?share_link_id=296220385966)
> - [diagramas.pdf](diagramas.pdf) — Versão consolidada em PDF dos diagramas estratégicos, táticos e C4.
> - [event-storming.pdf](event-storming.pdf) — Painéis completos de Event Storming em PDF.
> - [linguagem-ubiqua.md](linguagem-ubiqua.md) — Glossário da Linguagem Ubíqua e Rastreabilidade com o Código.

---

## 1. Diagrama Estratégico: Mapa de Contextos Delimitados (Bounded Contexts / Context Map)

Embora a aplicação seja implantada fisicamente como um **Monólito Modular em Clean Architecture**, a modelagem estratégica seguiu a separação lógica em **4 Contextos Delimitados (Bounded Contexts)** com fronteiras bem definidas e referências por identidade (`UUID`):

![Mapa de Contextos Delimitados (Context Map)](miro/ddd-01-context-map.svg)

```mermaid
flowchart TB
    subgraph CoreDomain["🎯 Core Domain: Contexto de Atendimento e Ordem de Serviço"]
        AggOS["Aggregate Root: OrdemServico<br/>Entidades Internas: ItemServicoOrdem, ItemPecaOrdem<br/>Value Objects: StatusOS, Dinheiro"]
        UseCasesOS["Casos de Uso:<br/>• CriarOrdemServicoUseCase<br/>• AdicionarItensOSUseCase<br/>• AprovarOrcamentoUseCase<br/>• AtualizarStatusOSUseCase<br/>• ConsultarStatusOSUseCase<br/>• ListarOrdensServicoUseCase<br/>• CalcularTempoMedioExecucaoUseCase"]
    end

    subgraph SupportingCadastros["📋 Supporting Domain: Contexto de Clientes e Frota"]
        AggCliente["Aggregate Root: Cliente<br/>Value Object: CpfCnpj"]
        AggVeiculo["Aggregate Root: Veiculo<br/>Value Object: Placa"]
        UseCasesCad["Casos de Uso:<br/>• CadastrarClienteUseCase / BuscarClienteUseCase / AtualizarClienteUseCase<br/>• CadastrarVeiculoUseCase / BuscarVeiculoUseCase"]
    end

    subgraph SupportingCatalogo["📦 Supporting Domain: Contexto de Catálogo e Estoque"]
        AggPeca["Aggregate Root: Peca<br/>Value Object: Dinheiro"]
        AggServico["Aggregate Root: Servico<br/>Value Object: Dinheiro"]
        UseCasesCat["Casos de Uso:<br/>• CadastrarPecaUseCase / ListarPecasUseCase / GerenciarEstoquePecaUseCase<br/>• GerenciarServicoUseCase"]
    end

    subgraph GenericIdentidade["🔐 Generic Subdomain: Identidade e Controle de Acesso (IAM)"]
        SecComponents["Componentes:<br/>• AuthController / JwtService<br/>• JwtAuthenticationFilter<br/>• CustomUserDetailsService (RBAC: ADMIN, MECANICO)"]
    end

    AggVeiculo -->|"pertence a (clienteId)"| AggCliente
    AggOS -->|"referencia por ID (clienteId, veiculoId)"| SupportingCadastros
    AggOS -->|"compõe itens e debita saldo (pecaId, servicoId)"| SupportingCatalogo
    GenericIdentidade -.->|"protege endpoints administrativos via JWT"| CoreDomain
    GenericIdentidade -.->|"protege endpoints administrativos via JWT"| SupportingCadastros
    GenericIdentidade -.->|"protege endpoints administrativos via JWT"| SupportingCatalogo
```

---

## 2. Diagrama Tático de Domínio: Agregados, Entidades e Objetos de Valor (DDD Tactical Design)

O diagrama de classes abaixo reflete exatamente o pacote `br.com.fiap.soat.oficina.domain` implementado em Java 21 puro:

![Diagrama Tático DDD — Agregados, Entidades e Objetos de Valor](miro/ddd-02-tactical-aggregates.svg)

```mermaid
classDiagram
    class OrdemServico {
        <<Aggregate Root>>
        -UUID id
        -UUID clienteId
        -UUID veiculoId
        -StatusOS status
        -String descricaoProblema
        -String diagnostico
        -List~ItemServicoOrdem~ itensServico
        -List~ItemPecaOrdem~ itensPeca
        -LocalDateTime dataAbertura
        -LocalDateTime dataAprovacao
        -LocalDateTime dataFinalizacao
        -LocalDateTime dataEntrega
        +abrir(UUID clienteId, UUID veiculoId, String descricaoProblema)$ OrdemServico
        +iniciarDiagnostico() void
        +registrarDiagnostico(String diagnostico) void
        +adicionarItemServico(ItemServicoOrdem item) void
        +removerItemServico(UUID itemId) void
        +adicionarItemPeca(ItemPecaOrdem item) void
        +removerItemPeca(UUID itemId) void
        +aguardarAprovacao() void
        +aprovar() void
        +rejeitar() void
        +finalizar() void
        +entregar() void
        +cancelar() void
        +calcularValorTotal() Dinheiro
        +calcularTotalServicos() Dinheiro
        +calcularTotalPecas() Dinheiro
        +calcularTempoExecucaoEmMinutos() OptionalLong
    }

    class ItemServicoOrdem {
        <<Internal Entity>>
        -UUID id
        -String descricao
        -Integer quantidade
        -Dinheiro valorUnitario
        +criar(String descricao, Integer qtd, Dinheiro valor)$ ItemServicoOrdem
        +calcularSubtotal() Dinheiro
    }

    class ItemPecaOrdem {
        <<Internal Entity>>
        -UUID id
        -UUID pecaId
        -String nomePeca
        -Integer quantidade
        -Dinheiro valorUnitario
        +criar(UUID pecaId, String nome, Integer qtd, Dinheiro valor)$ ItemPecaOrdem
        +calcularSubtotal() Dinheiro
    }

    class Cliente {
        <<Aggregate Root>>
        -UUID id
        -String nome
        -CpfCnpj cpfCnpj
        -String telefone
        -String email
        +criar(String nome, CpfCnpj doc, String tel, String email)$ Cliente
        +atualizar(String nome, String tel, String email) void
    }

    class Veiculo {
        <<Aggregate Root>>
        -UUID id
        -UUID clienteId
        -Placa placa
        -String marca
        -String modelo
        -Integer anoFabricacao
        -Integer anoModelo
        -String cor
        -Integer quilometragemAtual
        +criar(...)$ Veiculo
        +atualizarCadastro(String marca, String modelo, Integer anoFab, Integer anoMod, String cor) void
        +atualizarQuilometragem(Integer novaKm) void
    }

    class Peca {
        <<Aggregate Root>>
        -UUID id
        -String codigo
        -String nome
        -String descricao
        -Dinheiro precoUnitario
        -Integer saldoEstoque
        -Integer estoqueMinimo
        +criar(...)$ Peca
        +creditarEstoque(int quantidade) void
        +debitarEstoque(int quantidade) void
        +abaixoDoEstoqueMinimo() boolean
        +atualizarPreco(Dinheiro novoPreco) void
        +atualizarDadosCadastrais(String nome, String descricao, Integer estoqueMinimo) void
    }

    class Servico {
        <<Aggregate Root>>
        -UUID id
        -String codigo
        -String nome
        -String descricao
        -Dinheiro precoPadrao
        -Integer tempoEstimadoMinutos
        +criar(...)$ Servico
        +atualizar(String nome, String descricao, Dinheiro preco, Integer tempo) void
    }

    class StatusOS {
        <<Value Object / Enum>>
        RECEBIDA
        EM_DIAGNOSTICO
        AGUARDANDO_APROVACAO
        EM_EXECUCAO
        FINALIZADA
        ENTREGUE
        CANCELADA
        +podeTransicionarPara(StatusOS destino) boolean
    }

    class Dinheiro {
        <<Value Object>>
        +BigDecimal valor
        +somar(Dinheiro outro) Dinheiro
        +multiplicar(int fator) Dinheiro
    }

    class CpfCnpj {
        <<Value Object>>
        +String valor
        +formatado() String
        +isCpf() boolean
        +isCnpj() boolean
    }

    class Placa {
        <<Value Object>>
        +String valor
        +isMercosul() boolean
    }

    OrdemServico *-- "0..*" ItemServicoOrdem : compõe
    OrdemServico *-- "0..*" ItemPecaOrdem : compõe
    OrdemServico *-- "1" StatusOS : estado atual
    OrdemServico ..> Dinheiro : calcula orçamento
    Cliente *-- "1" CpfCnpj : possui
    Veiculo *-- "1" Placa : possui
    Peca *-- "1" Dinheiro : preço unitário
    Servico *-- "1" Dinheiro : preço padrão
```

---

## 3. Diagrama de Máquina de Estados da Ordem de Serviço (`StatusOS`)

A máquina de estados finita implementada no enum [`StatusOS`](../../src/main/java/br/com/fiap/soat/oficina/domain/valueobject/StatusOS.java) e protegida pela raiz de agregação [`OrdemServico`](../../src/main/java/br/com/fiap/soat/oficina/domain/entity/OrdemServico.java) impede transições inválidas ou regressões de status:

![Máquina de Estados da Ordem de Serviço (StatusOS)](miro/ddd-03-state-machine-os.svg)

```mermaid
stateDiagram-v2
    direction LR
    [*] --> RECEBIDA : abrir()
    RECEBIDA --> EM_DIAGNOSTICO : iniciarDiagnostico()
    EM_DIAGNOSTICO --> AGUARDANDO_APROVACAO : aguardarAprovacao()<br/>[Exige diagnóstico e ≥ 1 item]
    AGUARDANDO_APROVACAO --> EM_EXECUCAO : aprovar()<br/>[Debita estoque + grava dataAprovacao]
    AGUARDANDO_APROVACAO --> CANCELADA : rejeitar() / cancelar()<br/>[Sem baixa de estoque]
    EM_EXECUCAO --> FINALIZADA : finalizar()<br/>[Grava dataFinalizacao]
    FINALIZADA --> ENTREGUE : entregar()<br/>[Grava dataEntrega]
    ENTREGUE --> [*]

    RECEBIDA --> CANCELADA : cancelar()
    EM_DIAGNOSTICO --> CANCELADA : cancelar()
    CANCELADA --> [*]
```

---

## 4. Diagramas de Sequência dos Fluxos Principais

### 4.1 Fluxo de Criação da Ordem de Serviço, Diagnóstico e Geração Automática de Orçamento

![Diagrama de Sequência 4.1 — Criação da OS e Orçamento Automático](miro/ddd-04-seq-criacao-os.svg)

```mermaid
sequenceDiagram
    autonumber
    actor Mecanico as Mecânico / Atendente
    participant Ctrl as OrdemServicoAdminController
    participant UC_Criar as CriarOrdemServicoUseCase
    participant UC_Itens as AdicionarItensOSUseCase
    participant UC_Status as AtualizarStatusOSUseCase
    participant OS as Aggregate: OrdemServico
    participant DB as PostgreSQL (Repositórios)

    Mecanico->>Ctrl: POST /api/v1/ordens-servico (clienteId, veiculoId, descricaoProblema)
    Ctrl->>UC_Criar: executar(CriarOrdemServicoInput)
    UC_Criar->>DB: buscarPorId(clienteId) & buscarPorId(veiculoId)
    Note over UC_Criar: Valida invariante: veiculo.pertenceAoCliente(clienteId)
    UC_Criar->>OS: OrdemServico.abrir(clienteId, veiculoId, descricaoProblema)
    UC_Criar->>DB: salvar(OrdemServico [RECEBIDA])
    Ctrl-->>Mecanico: 201 Created (OrdemServicoOutput)

    Mecanico->>Ctrl: POST /api/v1/ordens-servico/{id}/iniciar-diagnostico
    Ctrl->>UC_Status: iniciarDiagnostico(id)
    UC_Status->>OS: os.iniciarDiagnostico() [EM_DIAGNOSTICO]
    UC_Status->>DB: salvar(os)
    Ctrl-->>Mecanico: 200 OK

    Mecanico->>Ctrl: PATCH /api/v1/ordens-servico/{id}/diagnostico (diagnostico)
    Ctrl->>UC_Status: registrarDiagnostico(id, diagnostico)
    UC_Status->>OS: os.registrarDiagnostico(diagnostico)
    UC_Status->>DB: salvar(os)
    Ctrl-->>Mecanico: 200 OK

    Mecanico->>Ctrl: POST /api/v1/ordens-servico/{id}/itens/servicos & /itens/pecas
    Ctrl->>UC_Itens: adicionarServico(id, input) / adicionarPeca(id, input)
    UC_Itens->>DB: buscarPorId(servicoId) / buscarPorId(pecaId)
    Note over UC_Itens: Valida saldo disponível no estoque antes de incluir peça
    UC_Itens->>OS: adicionarItemServico() + adicionarItemPeca()
    Note over OS: Orçamento recalculado automaticamente (valorTotal = serviços + peças)
    UC_Itens->>DB: salvar(os)
    Ctrl-->>Mecanico: 200 OK (OrdemServicoOutput com Orçamento Calculado)

    Mecanico->>Ctrl: POST /api/v1/ordens-servico/{id}/submeter-aprovacao
    Ctrl->>UC_Status: submeterParaAprovacao(id)
    UC_Status->>OS: os.enviarParaAprovacao() [AGUARDANDO_APROVACAO]
    UC_Status->>DB: salvar(os)
    Ctrl-->>Mecanico: 200 OK (Orçamento disponível para aprovação do cliente)
```

### 4.2 Fluxo de Acompanhamento Público pelo Cliente, Aprovação/Rejeição e Execução até Entrega

![Diagrama de Sequência 4.2 — Acompanhamento Público, Aprovação ACID e Entrega](miro/ddd-05-seq-acompanhamento-os.svg)

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente
    actor Mecanico as Mecânico / Atendente
    participant PubCtrl as OrdemServicoPublicController
    participant AdmCtrl as OrdemServicoAdminController
    participant UC_Cons as ConsultarStatusOSUseCase
    participant UC_Apr as AprovarOrcamentoUseCase
    participant UC_Stat as AtualizarStatusOSUseCase
    participant OS as Aggregate: OrdemServico
    participant Peca as Aggregate: Peca
    participant DB as PostgreSQL

    Cliente->>PubCtrl: GET /api/v1/public/ordens-servico/{id}/status
    PubCtrl->>UC_Cons: executar(id)
    UC_Cons->>DB: buscarPorId(id)
    PubCtrl-->>Cliente: 200 OK (StatusOSOutput: status, itens, valorTotal, datas - sem dados sensíveis)

    alt Cliente Aprova o Orçamento
        Cliente->>PubCtrl: POST /api/v1/public/ordens-servico/{id}/aprovar
        PubCtrl->>UC_Apr: executar(id)
        Note over UC_Apr,DB: Transação Atômica (@Transactional)
        loop Para cada ItemPecaOrdem da OS
            UC_Apr->>Peca: peca.debitarEstoque(quantidade)
            UC_Apr->>DB: salvar(peca)
        end
        UC_Apr->>OS: os.aprovar() [Status -> EM_EXECUCAO, grava dataAprovacao]
        UC_Apr->>DB: salvar(os)
        PubCtrl-->>Cliente: 200 OK (Orçamento aprovado e reparo iniciado)

        Mecanico->>AdmCtrl: POST /api/v1/ordens-servico/{id}/finalizar
        AdmCtrl->>UC_Stat: finalizar(id)
        UC_Stat->>OS: os.finalizar() [Status -> FINALIZADA, grava dataFinalizacao]
        UC_Stat->>DB: salvar(os)
        AdmCtrl-->>Mecanico: 200 OK

        Mecanico->>AdmCtrl: POST /api/v1/ordens-servico/{id}/entregar
        AdmCtrl->>UC_Stat: entregar(id)
        UC_Stat->>OS: os.entregar() [Status -> ENTREGUE, grava dataEntrega]
        UC_Stat->>DB: salvar(os)
        AdmCtrl-->>Mecanico: 200 OK
    else Cliente Rejeita o Orçamento
        Cliente->>PubCtrl: POST /api/v1/public/ordens-servico/{id}/rejeitar
        PubCtrl->>UC_Stat: rejeitar(id)
        UC_Stat->>OS: os.rejeitar() [Status -> CANCELADA, sem débito de estoque]
        UC_Stat->>DB: salvar(os)
        PubCtrl-->>Cliente: 200 OK (Orçamento rejeitado / OS cancelada)
    end
```

---

## 5. Diagrama Entidade-Relacionamento (DER - PostgreSQL 16 / Flyway `V1__init_schema.sql`)

![Diagrama Entidade-Relacionamento (DER PostgreSQL 16)](miro/ddd-06-der-postgresql.svg)

```mermaid
erDiagram
    CLIENTES ||--o{ VEICULOS : "possui (ON DELETE RESTRICT)"
    CLIENTES ||--o{ ORDENS_SERVICO : "solicita (ON DELETE RESTRICT)"
    VEICULOS ||--o{ ORDENS_SERVICO : "é reparado em (ON DELETE RESTRICT)"
    ORDENS_SERVICO ||--o{ OS_ITENS_SERVICO : "contém (ON DELETE CASCADE)"
    ORDENS_SERVICO ||--o{ OS_ITENS_PECA : "contém (ON DELETE CASCADE)"
    PECAS ||--o{ OS_ITENS_PECA : "referenciada em (ON DELETE RESTRICT)"

    CLIENTES {
        UUID id PK
        VARCHAR_150 nome
        VARCHAR_14 cpf_cnpj UK
        VARCHAR_20 telefone
        VARCHAR_150 email
    }

    VEICULOS {
        UUID id PK
        UUID cliente_id FK
        VARCHAR_7 placa UK
        VARCHAR_60 marca
        VARCHAR_80 modelo
        INTEGER ano_fabricacao
        INTEGER ano_modelo
        VARCHAR_40 cor
        INTEGER quilometragem_atual
    }

    SERVICOS {
        UUID id PK
        VARCHAR_50 codigo UK
        VARCHAR_150 nome
        TEXT descricao
        NUMERIC_12_2 preco_padrao
        INTEGER tempo_estimado_minutos
    }

    PECAS {
        UUID id PK
        VARCHAR_50 codigo UK
        VARCHAR_150 nome
        TEXT descricao
        NUMERIC_12_2 preco_unitario
        INTEGER saldo_estoque "CHECK (saldo_estoque >= 0)"
        INTEGER estoque_minimo "CHECK (estoque_minimo >= 0)"
    }

    ORDENS_SERVICO {
        UUID id PK
        UUID cliente_id FK
        UUID veiculo_id FK
        VARCHAR_30 status
        TEXT descricao_problema
        TEXT diagnostico
        TIMESTAMP data_abertura
        TIMESTAMP data_aprovacao
        TIMESTAMP data_finalizacao
        TIMESTAMP data_entrega
    }

    OS_ITENS_SERVICO {
        UUID id PK
        UUID ordem_servico_id FK
        VARCHAR_255 descricao
        INTEGER quantidade "CHECK (quantidade > 0)"
        NUMERIC_12_2 valor_unitario
    }

    OS_ITENS_PECA {
        UUID id PK
        UUID ordem_servico_id FK
        UUID peca_id FK
        VARCHAR_150 nome_peca
        INTEGER quantidade "CHECK (quantidade > 0)"
        NUMERIC_12_2 valor_unitario
    }
```

---

## 6. Diagramas de Arquitetura — Modelo C4 Completo

Os diagramas abaixo seguem a notação visual do **Modelo C4 (Structurizr)**, cobrindo o **System Landscape** e os **4 níveis do C4 Model** (**Contexto**, **Contêiner**, **Componente** e **Código**). O detalhamento completo encontra-se em [`docs/architecture/c4-model.md`](../architecture/c4-model.md).

### 6.1 Visão Organizacional: System Landscape (`[System Landscape] Oficina Mecânica`)
![System Landscape - C4 Model](../architecture/c4/c4-00-system-landscape.svg)

### 6.2 Nível 1: Diagrama de Contexto (`[System Context] Sistema de Gestão de Oficina Mecânica`)
![System Context - Nível 1 C4](../architecture/c4/c4-01-system-context.svg)

### 6.3 Nível 2: Diagrama de Contêineres (`[Container] Sistema de Gestão de Oficina Mecânica`)
![Container Diagram - Nível 2 C4](../architecture/c4/c4-02-container-diagram.svg)

### 6.4 Nível 3: Diagrama de Componentes (`[Component] Sistema de Gestão de Oficina Mecânica - API Application`)
![Component Diagram - Nível 3 C4](../architecture/c4/c4-03-component-diagram.svg)

### 6.5 Nível 4: Diagrama de Código (`[Code] Sistema de Gestão de Oficina Mecânica - AprovarOrcamentoUseCase & Agregados`)
![Code Diagram - Nível 4 C4](../architecture/c4/c4-04-code-diagram.svg)


