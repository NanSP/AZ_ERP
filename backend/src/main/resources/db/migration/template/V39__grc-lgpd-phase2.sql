CREATE TABLE grc.registros_tratamento (
    id SERIAL PRIMARY KEY,
    modulo VARCHAR(50) NOT NULL,
    entidade VARCHAR(50) NOT NULL,
    finalidade VARCHAR(255) NOT NULL,
    base_legal VARCHAR(50) NOT NULL,
    categoria_titular VARCHAR(30) NOT NULL,
    categoria_dados VARCHAR(30) NOT NULL,
    retencao_dias INTEGER,
    compartilhamento VARCHAR(255),
    requer_consentimento BOOLEAN DEFAULT false,
    ativo BOOLEAN DEFAULT true,
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_registros_tratamento UNIQUE (modulo, entidade, finalidade)
);

CREATE TABLE grc.solicitacoes_titular (
    id SERIAL PRIMARY KEY,
    protocolo VARCHAR(40) NOT NULL UNIQUE,
    titular_nome VARCHAR(150) NOT NULL,
    titular_contato VARCHAR(150) NOT NULL,
    tipo_titular VARCHAR(30) NOT NULL,
    direito_solicitado VARCHAR(50) NOT NULL,
    modulo VARCHAR(50),
    entidade VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'aberta',
    canal_origem VARCHAR(30),
    detalhes TEXT,
    prazo_resposta TIMESTAMP,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_conclusao TIMESTAMP,
    resposta_resumo TEXT,
    atendido_por_id INTEGER REFERENCES sys.usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
