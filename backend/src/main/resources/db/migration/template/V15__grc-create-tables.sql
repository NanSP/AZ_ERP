CREATE TABLE grc.riscos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(50),
    probabilidade INTEGER CHECK (probabilidade BETWEEN 1 AND 5),
    impacto INTEGER CHECK (impacto BETWEEN 1 AND 5),
    nivel_risco VARCHAR(20),
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    plano_mitigacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grc.auditorias (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    tipo_auditoria VARCHAR(30) CHECK (tipo_auditoria IN ('interna', 'externa', 'regulatoria')),
    escopo TEXT,
    data_inicio DATE,
    data_fim DATE,
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    status VARCHAR(20) DEFAULT 'planejada',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grc.controles (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE,
    descricao TEXT NOT NULL,
    tipo_controle VARCHAR(50),
    frequencia VARCHAR(20),
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    efetivo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LGPD
CREATE TABLE grc.consentimentos (
    id SERIAL PRIMARY KEY,
    titular_id INTEGER,
    tipo_titular VARCHAR(30),
    finalidade VARCHAR(100),
    data_consentimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_revogacao TIMESTAMP,
    ip_address INET,
    user_agent TEXT
);