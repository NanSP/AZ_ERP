CREATE TABLE portal.sessoes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    token_sessao VARCHAR(255) UNIQUE,
    ip_address INET,
    user_agent TEXT,
    data_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_logout TIMESTAMP,
    expiracao TIMESTAMP
);

CREATE TABLE portal.notificacoes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    titulo VARCHAR(200),
    mensagem TEXT,
    tipo VARCHAR(50),
    lida BOOLEAN DEFAULT false,
    data_leitura TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mobile.dispositivos (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    device_id VARCHAR(255) UNIQUE,
    device_model VARCHAR(100),
    device_platform VARCHAR(50),
    push_token VARCHAR(255),
    ultimo_acesso TIMESTAMP,
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
