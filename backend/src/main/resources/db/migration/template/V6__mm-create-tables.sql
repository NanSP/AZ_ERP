--SCHEMA PARA MM

-- Materiais/Produtos (complemento do core.produtos)
CREATE TABLE materiais.materiais (
    id SERIAL PRIMARY KEY,
    produto_id INTEGER REFERENCES core.produtos(id),
    tipo_material VARCHAR(30),
    categoria VARCHAR(50),
    subcategoria VARCHAR(50),
    marca VARCHAR(50),
    modelo VARCHAR(50),
    especificacoes_tecnicas TEXT,
    condicao_armazenamento TEXT,
    classe_perigo VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Estoques
CREATE TABLE materiais.estoques (
    id SERIAL PRIMARY KEY,
    produto_id INTEGER REFERENCES core.produtos(id),
    empresa_id INTEGER REFERENCES core.empresas(id),
    localizacao VARCHAR(100),
    lote VARCHAR(50),
    quantidade DECIMAL(15,4) DEFAULT 0,
    quantidade_minima DECIMAL(15,4) DEFAULT 0,
    quantidade_maxima DECIMAL(15,4) DEFAULT 0,
    valor_unitario DECIMAL(15,4),
    data_validade DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Movimentações de Estoque
CREATE TABLE materiais.movimentacoes (
    id SERIAL PRIMARY KEY,
    estoque_id INTEGER REFERENCES materiais.estoques(id),
    tipo_movimento VARCHAR(20) CHECK (tipo_movimento IN ('entrada', 'saida', 'transferencia', 'ajuste', 'inventario')),
    quantidade DECIMAL(15,4) NOT NULL,
    valor_unitario DECIMAL(15,4),
    valor_total DECIMAL(15,2),
    documento_referencia VARCHAR(50),
    motivo VARCHAR(255),
    usuario_id INTEGER REFERENCES sys.usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Compras
CREATE TABLE materiais.compras (
    id SERIAL PRIMARY KEY,
    fornecedor_id INTEGER REFERENCES core.parceiros(id),
    data_pedido DATE NOT NULL,
    data_prevista_entrega DATE,
    data_entrega DATE,
    valor_total DECIMAL(15,2),
    condicoes_pagamento TEXT,
    status VARCHAR(20) DEFAULT 'aberto',
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE materiais.compra_itens (
    id SERIAL PRIMARY KEY,
    compra_id INTEGER REFERENCES materiais.compras(id),
    produto_id INTEGER REFERENCES core.produtos(id),
    quantidade DECIMAL(15,4) NOT NULL,
    valor_unitario DECIMAL(15,4),
    valor_total DECIMAL(15,2),
    quantidade_recebida DECIMAL(15,4) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inventário
CREATE TABLE materiais.inventarios (
    id SERIAL PRIMARY KEY,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    tipo_inventario VARCHAR(30) CHECK (tipo_inventario IN ('anual', 'rotativo', 'amostragem')),
    status VARCHAR(20) DEFAULT 'planejado',
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);