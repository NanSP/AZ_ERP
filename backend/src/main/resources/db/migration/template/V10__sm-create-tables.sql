CREATE TABLE servicos.ordens_servico (
    id SERIAL PRIMARY KEY,
    numero_os VARCHAR(30) UNIQUE,
    cliente_id INTEGER REFERENCES core.parceiros(id),
    produto_id INTEGER REFERENCES core.produtos(id),
    tipo_servico VARCHAR(50),
    descricao_problema TEXT,
    prioridade VARCHAR(20) DEFAULT 'normal',
    data_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_agendamento DATE,
    data_inicio TIMESTAMP,
    data_fim TIMESTAMP,
    tecnico_id INTEGER REFERENCES rh.colaboradores(id),
    status VARCHAR(20) DEFAULT 'aberta',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE servicos.sla_config (
    id SERIAL PRIMARY KEY,
    tipo_servico VARCHAR(50) NOT NULL,
    prioridade VARCHAR(20),
    tempo_atendimento_horas INTEGER,
    tempo_resolucao_horas INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE servicos.atendimentos (
    id SERIAL PRIMARY KEY,
    os_id INTEGER REFERENCES servicos.ordens_servico(id),
    tecnico_id INTEGER REFERENCES rh.colaboradores(id),
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descricao TEXT,
    horas_gastas DECIMAL(10,2),
    materiais_utilizados JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
