-- V1__init_schema.sql
-- Esquema inicial do banco de dados (PostgreSQL 16+)

-- Tabela: clientes
CREATE TABLE clientes (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_clientes_documento UNIQUE (documento),
    CONSTRAINT uk_clientes_email UNIQUE (email)
);

-- Tabela: veiculos
CREATE TABLE veiculos (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cliente_id UUID NOT NULL,
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    ano_fabricacao INT NOT NULL,
    ano_modelo INT NOT NULL,
    cor VARCHAR(30),
    quilometragem INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_veiculos_placa UNIQUE (placa),
    CONSTRAINT fk_veiculos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT chk_veiculos_quilometragem CHECK (quilometragem >= 0),
    CONSTRAINT chk_veiculos_ano_fabricacao CHECK (ano_fabricacao > 1900),
    CONSTRAINT chk_veiculos_ano_modelo CHECK (ano_modelo >= ano_fabricacao)
);

-- Tabela: pecas
CREATE TABLE pecas (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255),
    valor_unitario NUMERIC(12, 2) NOT NULL,
    saldo_estoque INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_pecas_codigo UNIQUE (codigo),
    CONSTRAINT chk_pecas_valor_unitario CHECK (valor_unitario >= 0),
    CONSTRAINT chk_pecas_saldo_estoque CHECK (saldo_estoque >= 0),
    CONSTRAINT chk_pecas_estoque_minimo CHECK (estoque_minimo >= 0)
);

-- Tabela: servicos
CREATE TABLE servicos (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255),
    valor_unitario NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_servicos_codigo UNIQUE (codigo),
    CONSTRAINT chk_servicos_valor_unitario CHECK (valor_unitario >= 0)
);

-- Tabela: ordens_servico
CREATE TABLE ordens_servico (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cliente_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'RECEBIDA',
    descricao_problema VARCHAR(500) NOT NULL,
    diagnostico VARCHAR(500),
    valor_total NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    data_abertura TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_aprovacao TIMESTAMP WITH TIME ZONE,
    data_finalizacao TIMESTAMP WITH TIME ZONE,
    data_entrega TIMESTAMP WITH TIME ZONE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_os_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_os_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos(id) ON DELETE RESTRICT,
    CONSTRAINT chk_os_valor_total CHECK (valor_total >= 0),
    CONSTRAINT chk_os_status CHECK (status IN ('RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', 'APROVADA', 'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE', 'CANCELADA'))
);

-- Tabela: itens_servico_os
CREATE TABLE itens_servico_os (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    ordem_servico_id UUID NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    quantidade NUMERIC(10, 2) NOT NULL DEFAULT 1.00,
    valor_unitario NUMERIC(12, 2) NOT NULL,
    valor_total NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_itens_servico_os_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id) ON DELETE CASCADE,
    CONSTRAINT chk_itens_servico_quantidade CHECK (quantidade > 0),
    CONSTRAINT chk_itens_servico_valor_unitario CHECK (valor_unitario >= 0),
    CONSTRAINT chk_itens_servico_valor_total CHECK (valor_total >= 0)
);

-- Tabela: itens_peca_os
CREATE TABLE itens_peca_os (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    ordem_servico_id UUID NOT NULL,
    peca_id UUID NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    valor_unitario NUMERIC(12, 2) NOT NULL,
    valor_total NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_itens_peca_os_ordem FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_peca_os_peca FOREIGN KEY (peca_id) REFERENCES pecas(id) ON DELETE RESTRICT,
    CONSTRAINT chk_itens_peca_quantidade CHECK (quantidade > 0),
    CONSTRAINT chk_itens_peca_valor_unitario CHECK (valor_unitario >= 0),
    CONSTRAINT chk_itens_peca_valor_total CHECK (valor_total >= 0)
);

-- Índices
CREATE INDEX idx_clientes_documento ON clientes(documento);
CREATE INDEX idx_veiculos_placa ON veiculos(placa);
CREATE INDEX idx_veiculos_cliente_id ON veiculos(cliente_id);
CREATE INDEX idx_pecas_codigo ON pecas(codigo);
CREATE INDEX idx_servicos_codigo ON servicos(codigo);
CREATE INDEX idx_ordens_servico_status ON ordens_servico(status);
CREATE INDEX idx_ordens_servico_cliente_id ON ordens_servico(cliente_id);
CREATE INDEX idx_ordens_servico_veiculo_id ON ordens_servico(veiculo_id);
CREATE INDEX idx_itens_servico_os_ordem_servico_id ON itens_servico_os(ordem_servico_id);
CREATE INDEX idx_itens_peca_os_ordem_servico_id ON itens_peca_os(ordem_servico_id);
CREATE INDEX idx_itens_peca_os_peca_id ON itens_peca_os(peca_id);

