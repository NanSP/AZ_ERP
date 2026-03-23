CREATE TABLE auditoria.log_acoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    modulo VARCHAR(50),
    acao VARCHAR(50),
    tabela VARCHAR(50),
    registro_id INTEGER,
    dados_antigos JSONB,
    dados_novos JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria.log_erros (
    id BIGSERIAL PRIMARY KEY,
    erro_codigo INTEGER,
    erro_mensagem TEXT,
    modulo VARCHAR(50),
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    url TEXT,
    parametros JSONB,
    ip_address INET,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);