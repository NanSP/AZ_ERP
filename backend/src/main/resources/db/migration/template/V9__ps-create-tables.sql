--SCHEMA PARA MODULO PS

CREATE TABLE projetos.projetos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE,
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    cliente_id INTEGER REFERENCES core.parceiros(id),
    gerente_id INTEGER REFERENCES sys.usuarios(id),
    data_inicio DATE,
    data_fim DATE,
    data_prevista_inicio DATE,
    data_prevista_fim DATE,
    orcamento_total DECIMAL(15,2),
    orcamento_gasto DECIMAL(15,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'planejado',
    prioridade INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projetos.tarefas (
    id SERIAL PRIMARY KEY,
    projeto_id INTEGER REFERENCES projetos.projetos(id),
    tarefa_pai_id INTEGER REFERENCES projetos.tarefas(id),
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    responsavel_id INTEGER REFERENCES sys.usuarios(id),
    data_inicio DATE,
    data_fim DATE,
    horas_estimadas DECIMAL(10,2),
    horas_realizadas DECIMAL(10,2) DEFAULT 0,
    percentual_concluido INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'pendente',
    prioridade INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projetos.recursos_alocados (
    id SERIAL PRIMARY KEY,
    projeto_id INTEGER REFERENCES projetos.projetos(id),
    tarefa_id INTEGER REFERENCES projetos.tarefas(id),
    tipo_recurso VARCHAR(30), -- 'humano', 'material', 'financeiro'
    recurso_id INTEGER NOT NULL,
    quantidade DECIMAL(15,4),
    valor_unitario DECIMAL(15,4),
    valor_total DECIMAL(15,2),
    data_alocacao DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);