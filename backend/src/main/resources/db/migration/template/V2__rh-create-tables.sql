-- Tabelas para o modulo de RH

-- Colaboradores
CREATE TABLE rh.colaboradores (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    rg VARCHAR(20),
    data_nascimento DATE,
    sexo CHAR(1),
    estado_civil VARCHAR(20),
    nacionalidade VARCHAR(50),
    email_pessoal VARCHAR(100),
    email_corporativo VARCHAR(100),
    telefone VARCHAR(20),
    celular VARCHAR(20),
    data_admissao DATE,
    data_demissao DATE,
    cargo VARCHAR(100),
    departamento VARCHAR(50),
    salario DECIMAL(15,2),
    tipo_contrato VARCHAR(30),
    jornada_semanal INTEGER,
    situacao VARCHAR(20) DEFAULT 'ativo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dependentes
CREATE TABLE rh.dependentes (
    id SERIAL PRIMARY KEY,
    colaborador_id INTEGER REFERENCES rh.colaboradores(id),
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    parentesco VARCHAR(30),
    cpf VARCHAR(11),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Folha de Pagamento
CREATE TABLE rh.folha_pagamento (
    id SERIAL PRIMARY KEY,
    colaborador_id INTEGER REFERENCES rh.colaboradores(id),
    competencia DATE NOT NULL,
    salario_base DECIMAL(15,2),
    horas_normais DECIMAL(10,2),
    horas_extras DECIMAL(10,2),
    adicionais DECIMAL(15,2),
    descontos DECIMAL(15,2),
    valor_liquido DECIMAL(15,2),
    data_pagamento DATE,
    status VARCHAR(20) DEFAULT 'calculado',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Benefícios
CREATE TABLE rh.beneficios (
    id SERIAL PRIMARY KEY,
    colaborador_id INTEGER REFERENCES rh.colaboradores(id),
    tipo_beneficio VARCHAR(50) CHECK (tipo_beneficio IN ('vale_transporte', 'vale_refeicao', 'plano_saude', 'plano_odontologico')),
    valor DECIMAL(15,2),
    data_inicio DATE,
    data_fim DATE,
    ativo BOOLEAN DEFAULT true
);

-- Controle de Ponto
CREATE TABLE rh.ponto_eletronico (
    id SERIAL PRIMARY KEY,
    colaborador_id INTEGER REFERENCES rh.colaboradores(id),
    data DATE NOT NULL,
    hora_entrada TIME,
    hora_saida_almoco TIME,
    hora_retorno_almoco TIME,
    hora_saida TIME,
    horas_trabalhadas DECIMAL(10,2),
    horas_extras DECIMAL(10,2),
    atrasos INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);