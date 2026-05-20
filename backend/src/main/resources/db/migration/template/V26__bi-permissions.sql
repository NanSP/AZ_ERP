-- V26__bi-permissions.sql

-- Metricas
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_metricas', 'Permite visualizar metricas', 'bi', 'metricas', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'metricas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_metricas', 'Permite criar metricas', 'bi', 'metricas', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'metricas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_metricas', 'Permite atualizar metricas', 'bi', 'metricas', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'metricas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_metricas', 'Permite excluir metricas', 'bi', 'metricas', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'metricas' AND acao = 'delete'
);

-- Historico de Metricas
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_historico_metricas', 'Permite visualizar historico de metricas', 'bi', 'historico_metricas', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'historico_metricas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_historico_metricas', 'Permite criar historico de metricas', 'bi', 'historico_metricas', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'historico_metricas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_historico_metricas', 'Permite atualizar historico de metricas', 'bi', 'historico_metricas', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'historico_metricas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_historico_metricas', 'Permite excluir historico de metricas', 'bi', 'historico_metricas', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'historico_metricas' AND acao = 'delete'
);

-- Dashboards
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_dashboards', 'Permite visualizar dashboards', 'bi', 'dashboards', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'dashboards' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_dashboards', 'Permite criar dashboards', 'bi', 'dashboards', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'dashboards' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_dashboards', 'Permite atualizar dashboards', 'bi', 'dashboards', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'dashboards' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_dashboards', 'Permite excluir dashboards', 'bi', 'dashboards', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'dashboards' AND acao = 'delete'
);

-- Relatorios
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_relatorios', 'Permite visualizar relatorios', 'bi', 'relatorios', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'relatorios' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_relatorios', 'Permite criar relatorios', 'bi', 'relatorios', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'relatorios' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_relatorios', 'Permite atualizar relatorios', 'bi', 'relatorios', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'relatorios' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_relatorios', 'Permite excluir relatorios', 'bi', 'relatorios', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'bi' AND recurso = 'relatorios' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo em BI
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'bi'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update em todo BI
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'bi'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO: read em metricas, dashboards e relatorios
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'bi'
 AND pe.acao = 'read'
 AND pe.recurso IN ('metricas', 'dashboards', 'relatorios')
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );