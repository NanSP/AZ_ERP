-- Documentos Fiscais
CREATE TABLE fiscal.documentos (
    id SERIAL PRIMARY KEY,
    tipo_documento VARCHAR(10) CHECK (tipo_documento IN ('nfe', 'nfce', 'cte', 'nfse')),
    numero VARCHAR(20) NOT NULL,
    serie VARCHAR(5),
    chave_acesso VARCHAR(44) UNIQUE,
    data_emissao TIMESTAMP NOT NULL,
    pedido_id INTEGER REFERENCES vendas.pedidos(id),
    cliente_id INTEGER REFERENCES core.parceiros(id),
    valor_total DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'digitado',
    xml_file TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SPED
CREATE TABLE sped.ecd_registros (
    id BIGSERIAL PRIMARY KEY,
    periodo DATE NOT NULL,
    registro VARCHAR(4) NOT NULL,
    conteudo JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sped.efd_registros (
    id BIGSERIAL PRIMARY KEY,
    periodo DATE NOT NULL,
    registro VARCHAR(4) NOT NULL,
    conteudo JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sped.esocial_eventos (
    id BIGSERIAL PRIMARY KEY,
    periodo_apuracao DATE NOT NULL,
    tipo_evento VARCHAR(10) NOT NULL,
    evento_id VARCHAR(40) UNIQUE,
    conteudo XML,
    status VARCHAR(20) DEFAULT 'gerado',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
