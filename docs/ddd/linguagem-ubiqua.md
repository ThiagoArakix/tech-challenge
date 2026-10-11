# Linguagem Ubíqua (Ubiquitous Language) - Oficina Mecânica

Este documento formaliza o vocabulário padronizado do domínio da **Oficina Mecânica**, estabelecido em conjunto entre especialistas de negócio (domain experts) e a equipe de engenharia de software da pós-graduação SOAT (Fase 1).

A precisão terminológica garante que o modelo de domínio, o código-fonte, a documentação e a comunicação de negócio expressem as mesmas ideias sem ambiguidade.

---

## 1. Termos Formais e Conceitos Centrais do Domínio

### 1.1 Ordem de Serviço (OS)
* **Definição:** Entidade central e Aggregate Root do atendimento automotivo. Representa o contrato formal de prestação de serviços entre a oficina e o cliente para a manutenção de um veículo específico.
* **Ciclo de Vida:** Controlada por uma máquina de estados estrita: `RECEBIDA` $\rightarrow$ `EM_DIAGNOSTICO` $\rightarrow$ `AGUARDANDO_APROVACAO` $\rightarrow$ `EM_EXECUCAO` $\rightarrow$ `FINALIZADA` $\rightarrow$ `ENTREGUE` (ou `CANCELADA`).
* **Regras de Negócio Associadas:**
  - O veículo informado deve obrigatoriamente pertencer ao cliente solicitante.
  - O valor total da OS é calculado dinamicamente pela soma de seus itens de serviço e peças.
  - A adição ou remoção de itens é bloqueada após a saída da fase de diagnóstico.

### 1.2 Orçamento
* **Definição:** Composição valorada gerada durante a fase de diagnóstico técnico, compreendendo os itens de serviço propostos e as peças necessárias para a solução das avarias identificadas.
* **Regras de Negócio Associadas:**
  - Requer diagnóstico técnico formal registrado.
  - Exige ao menos um serviço ou uma peça para ser submetido à aprovação do cliente.
  - Pode ser consultado publicamente pelo cliente via link/código seguro sem necessidade de credenciais administrativas.
  - A aprovação formal do orçamento pelo cliente deflagra a reserva e baixa física imediata do estoque das peças.

### 1.3 Item de Serviço
* **Definição:** Representação da mão de obra especializada aplicada na resolução de uma avaria na Ordem de Serviço (ex.: *Alinhamento e Balanceamento*, *Troca de Pastilhas de Freio*).
* **Atributos:** Descrição técnica, quantidade de horas ou unidades e valor unitário da mão de obra.

### 1.4 Item de Peça
* **Definição:** Componente físico do inventário da oficina alocado para aplicação em uma Ordem de Serviço específica (ex.: *Filtro de Óleo*, *Amortecedor Dianteiro*).
* **Atributos:** Vínculo ao identificador da Peça no catálogo, quantidade aplicada e valor unitário no momento do orçamento.
* **Regras de Negócio Associadas:**
  - Validação prévia de saldo disponível no momento da inserção na OS.
  - Débito definitivo do estoque físico disparado no evento de aprovação do orçamento.

### 1.5 Status da OS
* **Definição:** Enumeração finita representativa da etapa operacional atual da Ordem de Serviço:
  - `RECEBIDA`: Veículo deu entrada na oficina com relato inicial do problema.
  - `EM_DIAGNOSTICO`: Mecânico está inspecionando o veículo, definindo serviços e peças necessárias.
  - `AGUARDANDO_APROVACAO`: Laudo e orçamento elaborados, aguardando validação do cliente.
  - `EM_EXECUCAO`: Orçamento aprovado pelo cliente, peças baixadas do estoque e reparos em andamento.
  - `FINALIZADA`: Serviços técnicos executados, montagem concluída e veículo pronto para retirada.
  - `ENTREGUE`: Veículo retirado pelo cliente e atendimento formalmente concluído.
  - `CANCELADA`: Atendimento abortado em conformidade com as regras de cancelamento.

### 1.6 Tempo de Execução e Tempo Médio de Execução
* **Definição:** Métrica operacional e gerencial de desempenho da oficina.
* **Fórmula do Tempo de Execução:** Diferença temporal entre o instante de finalização técnica dos serviços (`dataFinalizacao`) e a abertura do chamado (`dataAbertura`), mensurada em minutos/horas.
* **Tempo Médio de Execução:** Média aritmética dos tempos de execução de todas as Ordens de Serviço com status `FINALIZADA` ou `ENTREGUE` registradas no sistema.

### 1.7 Cliente
* **Definição:** Pessoa física ou jurídica titular da contratação dos serviços e proprietária registrada dos veículos atendidos.
* **Identificação:** Documento único validado matematicamente (CPF com 11 dígitos ou CNPJ com 14 dígitos).

### 1.8 Veículo
* **Definição:** Automóvel sob custódia da oficina mecânica para realização de reparos ou revisões preventivas.
* **Identificação:** Placa alfanumérica única no padrão tradicional brasileiro (`ABC-1234`) ou no padrão Mercosul (`ABC1D23`).
* **Invariante:** Cada veículo deve estar obrigatoriamente associado a um único cliente proprietário ativo.

### 1.9 Peça e Inventário
* **Definição:** Item de consumo ou reposição pertencente ao catálogo da oficina mecânica.
* **Saldo de Estoque:** Quantidade física de unidades disponíveis para aplicação. Não admite valores negativos.
* **Estoque Mínimo:** Nível mínimo de segurança operacional. Quando o saldo de estoque atinge ou fica abaixo deste valor, alertas de reposição são emitidos para a gestão.

---

## 2. Glossário de Termos Proibidos e Evitados (Anti-Glossário)

Para preservar a pureza conceitual da arquitetura e evitar confusão entre camadas de aplicação e domínios adjacentes, os seguintes termos são **estritamente proibidos ou desaconselhados**:

| Termo Proibido / Evitado | Termo Canônico Correto | Motivo / Contexto |
| :--- | :--- | :--- |
| **Ticket / Chamado** | **Ordem de Serviço (OS)** | "Ticket" ou "Chamado" remete a centrais de suporte de TI (ITSM). Na mecânica automotiva, o termo legal e operacional é Ordem de Serviço. |
| **Carrinho / Pedido de Compra** | **Orçamento / Itens da OS** | O sistema não é um e-commerce. A oficina trabalha com composição de orçamento técnico vinculada à OS. |
| **Produto** | **Peça** (ou **Serviço**) | "Produto" é vago e confunde peças físicas do estoque com mão de obra de serviços técnicos. Deve-se explicitar se é Peça ou Serviço. |
| **Carro / Auto** | **Veículo** | O domínio atende motocicletas, automóveis de passeio e utilitários. "Veículo" é a terminologia neutra e abrangente correta. |
| **Usuário (como dono do carro)** | **Cliente** | "Usuário" refere-se à conta de acesso do sistema (autenticação/segurança). A pessoa que contrata os serviços é o **Cliente**. |
| **Fechada / Concluída / Pronta** | **FINALIZADA** ou **ENTREGUE** | Ambíguo. O sistema distingue claramente o término técnico da manutenção (`FINALIZADA`) do momento em que o cliente retira as chaves (`ENTREGUE`). |
| **Preço** | **Valor Unitário / Total** | Na modelagem financeira de DDD e contabilidade da oficina, utiliza-se a estrutura monetária `Valor` (Value Object `Dinheiro`). |
| **Peça Baixada** (sem contexto) | **Débito de Estoque** | Ação explícita no agregador de estoque disparada na aprovação do orçamento técnico. |

