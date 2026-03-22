CREATE TABLE qualidade.inspecoes (
    id SERIAL PRIMARY KEY,
    tipo_inspecao VARCHAR(30) CHECK (tipo_inspecao IN ('recebimento', 'processo', 'final', 'expedicao')),
    produto_id INTEGER REFERENCES core.produtos(id),
    lote VARCHAR(50),
    quantidade_inspecionada DECIMAL(15,4),
    quantidade_aprovada DECIMAL(15,4),
    quantidade_reprovada DECIMAL(15,4),
    data_inspecao DATE NOT NULL,
    inspetor_id INTEGER REFERENCES rh.colaboradores(id),
    resultado VARCHAR(20) CHECK (resultado IN ('aprovado', 'reprovado', 'em_analise')),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE qualidade.nao_conformidades (
    id SERIAL PRIMARY KEY,
    inspecao_id INTEGER REFERENCES qualidade.inspecoes(id),
    tipo_nao_conformidade VARCHAR(50),
    descricao TEXT,
    causa_raiz TEXT,
    acao_imediata TEXT,
    acao_corretiva TEXT,
    responsavel_id INTEGER REFERENCES rh.colaboradores(id),
    data_identificacao DATE,
    data_resolucao DATE,
    status VARCHAR(20) DEFAULT 'aberta',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);