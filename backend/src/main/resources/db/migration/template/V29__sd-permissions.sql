-- Clientes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_clientes_sd', 'Permite visualizar clientes do SD', 'sd', 'clientes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'clientes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_clientes_sd', 'Permite criar clientes do SD', 'sd', 'clientes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'clientes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_clientes_sd', 'Permite atualizar clientes do SD', 'sd', 'clientes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'clientes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_clientes_sd', 'Permite remover clientes do SD', 'sd', 'clientes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'clientes' AND acao = 'delete'
);

-- Oportunidades
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_oportunidades', 'Permite visualizar oportunidades', 'sd', 'oportunidades', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'oportunidades' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_oportunidades', 'Permite criar oportunidades', 'sd', 'oportunidades', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'oportunidades' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_oportunidades', 'Permite atualizar oportunidades', 'sd', 'oportunidades', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'oportunidades' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_oportunidades', 'Permite remover oportunidades', 'sd', 'oportunidades', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'oportunidades' AND acao = 'delete'
);

-- Pedidos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_pedidos_sd', 'Permite visualizar pedidos', 'sd', 'pedidos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_pedidos_sd', 'Permite criar pedidos', 'sd', 'pedidos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_pedidos_sd', 'Permite atualizar pedidos', 'sd', 'pedidos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_pedidos_sd', 'Permite remover pedidos', 'sd', 'pedidos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidos' AND acao = 'delete'
);

-- Pedido Itens
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_pedido_itens', 'Permite visualizar itens de pedido', 'sd', 'pedidoItens', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidoItens' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_pedido_itens', 'Permite criar itens de pedido', 'sd', 'pedidoItens', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidoItens' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_pedido_itens', 'Permite atualizar itens de pedido', 'sd', 'pedidoItens', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidoItens' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_pedido_itens', 'Permite remover itens de pedido', 'sd', 'pedidoItens', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'pedidoItens' AND acao = 'delete'
);

-- Faturas
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_faturas_sd', 'Permite visualizar faturas', 'sd', 'faturas', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'faturas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_faturas_sd', 'Permite criar faturas', 'sd', 'faturas', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'faturas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_faturas_sd', 'Permite atualizar faturas', 'sd', 'faturas', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'faturas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_faturas_sd', 'Permite remover faturas', 'sd', 'faturas', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'faturas' AND acao = 'delete'
);

-- Contratos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_contratos_sd', 'Permite visualizar contratos', 'sd', 'contratos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'contratos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_contratos_sd', 'Permite criar contratos', 'sd', 'contratos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'contratos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_contratos_sd', 'Permite atualizar contratos', 'sd', 'contratos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'contratos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_contratos_sd', 'Permite remover contratos', 'sd', 'contratos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sd' AND recurso = 'contratos' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'sd'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1 FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id AND pp.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'sd'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1 FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id AND pp.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'sd'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1 FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id AND pp.permissao_id = pe.id
  );