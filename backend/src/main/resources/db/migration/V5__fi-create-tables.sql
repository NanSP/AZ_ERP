--SCHEMA FINANCEIRO

-- Plano de Contas
CREATE TABLE financeiro.plano_contas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(200) NOT NULL,
    tipo_conta VARCHAR(20) CHECK (tipo_conta IN ('analitica', 'sintetica')),
    natureza VARCHAR(10) CHECK (natureza IN ('devedora', 'credora')),
    conta_pai_id INTEGER REFERENCES contabil.plano_contas(id),
    situacao VARCHAR(10) DEFAULT 'ativo'
);

-- Centros de Custo
CREATE TABLE financeiro.centros_custo (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(30),
    responsavel VARCHAR(100),
    ativo BOOLEAN DEFAULT true
);

-- Contas a Pagar
CREATE TABLE financeiro.contas_pagar (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER REFERENCES core.empresas(id),
    fornecedor_id INTEGER REFERENCES core.parceiros(id),
    centro_custo_id INTEGER REFERENCES financeiro.centros_custo(id),
    numero_documento VARCHAR(50),
    descricao VARCHAR(255),
    valor_original DECIMAL(15,2),
    valor_pago DECIMAL(15,2),
    data_emissao DATE,
    data_vencimento DATE,
    data_pagamento DATE,
    status VARCHAR(20) DEFAULT 'pendente',
    forma_pagamento VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Contas a Receber
CREATE TABLE financeiro.contas_receber (
    id SERIAL PRIMARY KEY,
    empresa_id INTEGER REFERENCES core.empresas(id),
    cliente_id INTEGER REFERENCES core.parceiros(id),
    centro_custo_id INTEGER REFERENCES financeiro.centros_custo(id),
    numero_documento VARCHAR(50),
    descricao VARCHAR(255),
    valor_original DECIMAL(15,2),
    valor_recebido DECIMAL(15,2),
    data_emissao DATE,
    data_vencimento DATE,
    data_recebimento DATE,
    status VARCHAR(20) DEFAULT 'pendente',
    forma_pagamento VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movimentações Bancárias
CREATE TABLE financeiro.movimentacoes_bancarias (
    id SERIAL PRIMARY KEY,
    conta_bancaria_id INTEGER,
    tipo_movimento VARCHAR(20) CHECK (tipo_movimento IN ('credito', 'debito', 'transferencia')),
    valor DECIMAL(15,2) NOT NULL,
    data_movimento DATE NOT NULL,
    historico TEXT,
    documento_vinculado VARCHAR(50),
    conciliado BOOLEAN DEFAULT false,
    data_conciliacao DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fluxo de Caixa
CREATE TABLE financeiro.fluxo_caixa (
    id SERIAL PRIMARY KEY,
    data_referencia DATE NOT NULL,
    saldo_inicial DECIMAL(15,2) DEFAULT 0,
    entradas_previstas DECIMAL(15,2) DEFAULT 0,
    saidas_previstas DECIMAL(15,2) DEFAULT 0,
    entradas_realizadas DECIMAL(15,2) DEFAULT 0,
    saidas_realizadas DECIMAL(15,2) DEFAULT 0,
    saldo_final_previsto DECIMAL(15,2) DEFAULT 0,
    saldo_final_real DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
