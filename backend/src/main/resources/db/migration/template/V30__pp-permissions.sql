-- BOM
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_bom_pp', 'Permite visualizar BOM', 'pp', 'bom', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'bom' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_bom_pp', 'Permite criar BOM', 'pp', 'bom', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'bom' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_bom_pp', 'Permite atualizar BOM', 'pp', 'bom', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'bom' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_bom_pp', 'Permite remover BOM', 'pp', 'bom', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'bom' AND acao = 'delete'
);

-- Ordem de Producao
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_ordem_producao', 'Permite visualizar ordens de producao', 'pp', 'ordemProducao', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'ordemProducao' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_ordem_producao', 'Permite criar ordens de producao', 'pp', 'ordemProducao', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'ordemProducao' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_ordem_producao', 'Permite atualizar ordens de producao', 'pp', 'ordemProducao', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'ordemProducao' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_ordem_producao', 'Permite remover ordens de producao', 'pp', 'ordemProducao', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'ordemProducao' AND acao = 'delete'
);

-- Apontamentos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_apontamentos_pp', 'Permite visualizar apontamentos', 'pp', 'apontamentos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'apontamentos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_apontamentos_pp', 'Permite criar apontamentos', 'pp', 'apontamentos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'apontamentos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_apontamentos_pp', 'Permite atualizar apontamentos', 'pp', 'apontamentos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'apontamentos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_apontamentos_pp', 'Permite remover apontamentos', 'pp', 'apontamentos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'apontamentos' AND acao = 'delete'
);

-- MRP
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_mrp_pp', 'Permite visualizar MRP', 'pp', 'mrp', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'mrp' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_mrp_pp', 'Permite criar MRP', 'pp', 'mrp', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'mrp' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_mrp_pp', 'Permite atualizar MRP', 'pp', 'mrp', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'mrp' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_mrp_pp', 'Permite remover MRP', 'pp', 'mrp', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'pp' AND recurso = 'mrp' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'pp'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp2
      WHERE pp2.perfil_id = p.id
        AND pp2.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'pp'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp2
      WHERE pp2.perfil_id = p.id
        AND pp2.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'pp'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp2
      WHERE pp2.perfil_id = p.id
        AND pp2.permissao_id = pe.id
  );