CREATE TABLE bi.dashboards (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    layout JSONB,
    configuracoes JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bi.relatorios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    tipo_relatorio VARCHAR(50),
    query_sql TEXT,
    parametros JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bi.metricas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(50),
    formula TEXT,
    unidade_medida VARCHAR(20),
    meta DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bi.historico_metricas (
    id BIGSERIAL PRIMARY KEY,
    metrica_id INTEGER REFERENCES bi.metricas(id),
    periodo DATE NOT NULL,
    valor_apurado DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
