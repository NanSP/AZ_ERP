CREATE TABLE ativos.bens_patrimoniais (
    id SERIAL PRIMARY KEY,
    codigo_patrimonio VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    tipo_ativo VARCHAR(50),
    localizacao VARCHAR(100),
    data_aquisicao DATE,
    valor_aquisicao DECIMAL(15,2),
    valor_atual DECIMAL(15,2),
    vida_util_anos INTEGER,
    taxa_depreciacao DECIMAL(5,2),
    data_depreciacao DATE,
    fornecedor_id INTEGER REFERENCES core.parceiros(id),
    responsavel_id INTEGER REFERENCES rh.colaboradores(id),
    status VARCHAR(20) DEFAULT 'ativo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ativos.manutencoes (
    id SERIAL PRIMARY KEY,
    ativo_id INTEGER REFERENCES ativos.bens_patrimoniais(id),
    tipo_manutencao VARCHAR(30) CHECK (tipo_manutencao IN ('preventiva', 'corretiva', 'preditiva')),
    data_solicitacao DATE,
    data_execucao DATE,
    descricao TEXT,
    custo_mao_obra DECIMAL(15,2),
    custo_material DECIMAL(15,2),
    custo_total DECIMAL(15,2),
    tecnico_id INTEGER REFERENCES rh.colaboradores(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);