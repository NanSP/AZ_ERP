--SCHEMA SD/CRM

-- Clientes
CREATE TABLE crm.clientes (
    id SERIAL PRIMARY KEY,
    parceiro_id INTEGER REFERENCES core.parceiros(id),
    classificacao VARCHAR(30),
    origem VARCHAR(50),
    website VARCHAR(255),
    faturamento_anual DECIMAL(15,2),
    numero_funcionarios INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Oportunidades/Funil de Vendas
CREATE TABLE crm.oportunidades (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES crm.clientes(id),
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    valor_estimado DECIMAL(15,2),
    probabilidade INTEGER DEFAULT 50,
    estagio VARCHAR(30) CHECK (estagio IN ('prospeccao', 'qualificacao', 'proposta', 'negociacao', 'fechado_ganho', 'fechado_perdido')),
    data_prevista_fechamento DATE,
    motivo_perda TEXT,
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Pedidos de Venda
CREATE TABLE vendas.pedidos (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES core.parceiros(id),
    numero_pedido VARCHAR(30) UNIQUE,
    data_pedido DATE NOT NULL,
    data_entrega DATE,
    valor_total DECIMAL(15,2),
    desconto_total DECIMAL(15,2),
    condicoes_pagamento VARCHAR(255),
    status VARCHAR(20) DEFAULT 'aberto',
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendas.pedido_itens (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES vendas.pedidos(id),
    produto_id INTEGER REFERENCES core.produtos(id),
    quantidade DECIMAL(15,4) NOT NULL,
    valor_unitario DECIMAL(15,4),
    valor_total DECIMAL(15,2),
    desconto DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Faturamento
CREATE TABLE vendas.faturas (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES vendas.pedidos(id),
    numero_fatura VARCHAR(30) UNIQUE,
    data_emissao DATE NOT NULL,
    valor_total DECIMAL(15,2),
    data_vencimento DATE,
    status VARCHAR(20) DEFAULT 'emitida',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Contratos
CREATE TABLE vendas.contratos (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES core.parceiros(id),
    numero_contrato VARCHAR(50) UNIQUE,
    objeto TEXT,
    valor_total DECIMAL(15,2),
    data_inicio DATE,
    data_fim DATE,
    status VARCHAR(20) DEFAULT 'vigente',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);