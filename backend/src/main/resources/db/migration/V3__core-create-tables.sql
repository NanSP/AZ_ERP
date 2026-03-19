-- Tabelas compartilhadas

-- Empresas/Filiais
CREATE TABLE core.empresas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    razao_social VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    cnpj VARCHAR(14) UNIQUE NOT NULL,
    inscricao_estadual VARCHAR(20),
    inscricao_municipal VARCHAR(20),
    regime_tributario VARCHAR(30),
    data_fundacao DATE,
    situacao VARCHAR(20) DEFAULT 'ativo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Endereços
CREATE TABLE core.enderecos (
    id SERIAL PRIMARY KEY,
    entidade_tipo VARCHAR(30), -- 'empresa', 'filial', 'cliente', 'fornecedor', 'colaborador'
    entidade_id INTEGER NOT NULL,
    tipo_endereco VARCHAR(20) DEFAULT 'comercial',
    logradouro VARCHAR(200),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    uf CHAR(2),
    cep VARCHAR(8),
    pais VARCHAR(50) DEFAULT 'BRASIL',
    principal BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Contatos (telefones, emails)
CREATE TABLE core.contatos (
    id SERIAL PRIMARY KEY,
    entidade_tipo VARCHAR(30),
    entidade_id INTEGER NOT NULL,
    tipo_contato VARCHAR(20), -- 'telefone', 'email', 'whatsapp', 'site'
    valor VARCHAR(100),
    principal BOOLEAN DEFAULT false,
    observacao VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Parceiros (Clientes, Fornecedores, Transportadoras)
CREATE TABLE core.parceiros (
    id SERIAL PRIMARY KEY,
    tipo_parceiro VARCHAR(30) CHECK (tipo_parceiro IN ('cliente', 'fornecedor', 'transportadora', 'representante')),
    codigo VARCHAR(20) UNIQUE,
    nome VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    documento VARCHAR(20), -- CPF/CNPJ
    tipo_pessoa CHAR(1) CHECK (tipo_pessoa IN ('F', 'J')),
    situacao VARCHAR(20) DEFAULT 'ativo',
    limite_credito DECIMAL(15,2),
    dias_prazo INTEGER,
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Produtos/Serviços
CREATE TABLE core.produtos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    codigo_barras VARCHAR(50),
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    tipo_item VARCHAR(20) CHECK (tipo_item IN ('produto', 'servico', 'insumo', 'embalagem')),
    unidade_medida VARCHAR(10),
    ncm VARCHAR(8),
    cest VARCHAR(7),
    peso_bruto DECIMAL(15,4),
    peso_liquido DECIMAL(15,4),
    origem INTEGER DEFAULT 0, -- 0=nacional, 1=estrangeira
    situacao VARCHAR(20) DEFAULT 'ativo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);