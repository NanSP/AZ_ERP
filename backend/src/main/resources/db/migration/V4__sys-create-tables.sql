-- script de criação das tables do schema sys

-- Usuários
CREATE TABLE sys.usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    documento VARCHAR(20),
    tipo_usuario VARCHAR(30),
    status VARCHAR(20) DEFAULT 'ativo',
    ultimo_acesso TIMESTAMP,
    expiracao_senha DATE,
    tentativas_login INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys.perfis (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL,
    descricao VARCHAR(255),
    nivel_acesso INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys.usuario_perfil (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    perfil_id INTEGER REFERENCES sys.perfis(id),
    data_atribuicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys.permissoes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao VARCHAR(255),
    modulo VARCHAR(50),
    recurso VARCHAR(100),
    acao VARCHAR(50), -- 'create', 'read', 'update', 'delete', 'execute'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys.perfil_permissao (
    id SERIAL PRIMARY KEY,
    perfil_id INTEGER REFERENCES sys.perfis(id),
    permissao_id INTEGER REFERENCES sys.permissoes(id)
);