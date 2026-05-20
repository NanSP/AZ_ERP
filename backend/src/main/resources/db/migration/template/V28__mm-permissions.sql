-- Compras
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_compras', 'Permite visualizar compras', 'mm', 'compras', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compras' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_compras', 'Permite criar compras', 'mm', 'compras', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compras' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_compras', 'Permite atualizar compras', 'mm', 'compras', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compras' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_compras', 'Permite remover compras', 'mm', 'compras', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compras' AND acao = 'delete'
);

-- Compra Itens
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_compra_itens', 'Permite visualizar itens de compra', 'mm', 'compraItens', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compraItens' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_compra_itens', 'Permite criar itens de compra', 'mm', 'compraItens', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compraItens' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_compra_itens', 'Permite atualizar itens de compra', 'mm', 'compraItens', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compraItens' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_compra_itens', 'Permite remover itens de compra', 'mm', 'compraItens', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'compraItens' AND acao = 'delete'
);

-- Materiais
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_materiais', 'Permite visualizar materiais', 'mm', 'materiais', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'materiais' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_materiais', 'Permite criar materiais', 'mm', 'materiais', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'materiais' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_materiais', 'Permite atualizar materiais', 'mm', 'materiais', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'materiais' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_materiais', 'Permite remover materiais', 'mm', 'materiais', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'materiais' AND acao = 'delete'
);

-- Inventarios
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_inventarios', 'Permite visualizar inventarios', 'mm', 'inventarios', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'inventarios' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_inventarios', 'Permite criar inventarios', 'mm', 'inventarios', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'inventarios' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_inventarios', 'Permite atualizar inventarios', 'mm', 'inventarios', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'inventarios' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_inventarios', 'Permite remover inventarios', 'mm', 'inventarios', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'inventarios' AND acao = 'delete'
);

-- Estoques
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_estoques', 'Permite visualizar estoques', 'mm', 'estoques', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'estoques' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_estoques', 'Permite criar estoques', 'mm', 'estoques', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'estoques' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_estoques', 'Permite atualizar estoques', 'mm', 'estoques', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'estoques' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_estoques', 'Permite remover estoques', 'mm', 'estoques', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'estoques' AND acao = 'delete'
);

-- Movimentacoes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_movimentacoes', 'Permite visualizar movimentacoes', 'mm', 'movimentacoes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'movimentacoes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_movimentacoes', 'Permite criar movimentacoes', 'mm', 'movimentacoes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'movimentacoes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_movimentacoes', 'Permite atualizar movimentacoes', 'mm', 'movimentacoes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'movimentacoes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_movimentacoes', 'Permite remover movimentacoes', 'mm', 'movimentacoes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'mm' AND recurso = 'movimentacoes' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'mm'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'mm'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO: somente leitura
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'mm'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );