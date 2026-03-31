--SCHEMA FOR PP module

-- Estrutura de Produto (BOM)
CREATE TABLE producao.bom (
    id SERIAL PRIMARY KEY,
    produto_pai_id INTEGER REFERENCES core.produtos(id),
    componente_id INTEGER REFERENCES core.produtos(id),
    quantidade DECIMAL(15,4) NOT NULL,
    unidade_medida VARCHAR(10),
    nivel INTEGER DEFAULT 1,
    tempo_preparacao DECIMAL(10,2),
    tempo_producao DECIMAL(10,2),
    roteiro_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ordens de Produção
CREATE TABLE producao.ordens_producao (
    id SERIAL PRIMARY KEY,
    numero_op VARCHAR(30) UNIQUE,
    produto_id INTEGER REFERENCES core.produtos(id),
    quantidade_planejada DECIMAL(15,4) NOT NULL,
    quantidade_produzida DECIMAL(15,4) DEFAULT 0,
    data_emissao DATE NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    data_prevista DATE,
    status VARCHAR(20) DEFAULT 'planejada',
    prioridade INTEGER DEFAULT 1,
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Apontamento de Produção
CREATE TABLE producao.apontamentos (
    id SERIAL PRIMARY KEY,
    op_id INTEGER REFERENCES producao.ordens_producao(id),
    maquina_id INTEGER,
    operador_id INTEGER REFERENCES rh.colaboradores(id),
    data_hora_inicio TIMESTAMP,
    data_hora_fim TIMESTAMP,
    quantidade_produzida DECIMAL(15,4),
    quantidade_refugo DECIMAL(15,4),
    tempo_parado DECIMAL(10,2),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MRP (Planejamento de Necessidades)
CREATE TABLE producao.mrp (
    id SERIAL PRIMARY KEY,
    produto_id INTEGER REFERENCES core.produtos(id),
    periodo DATE NOT NULL,
    demanda_prevista DECIMAL(15,4),
    estoque_atual DECIMAL(15,4),
    estoque_seguranca DECIMAL(15,4),
    necessidade_compra DECIMAL(15,4),
    necessidade_producao DECIMAL(15,4),
    data_necessidade DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);