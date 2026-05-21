-- Projetos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_projetos_ps', 'Permite visualizar projetos', 'ps', 'projetos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'projetos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_projetos_ps', 'Permite criar projetos', 'ps', 'projetos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'projetos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_projetos_ps', 'Permite atualizar projetos', 'ps', 'projetos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'projetos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_projetos_ps', 'Permite remover projetos', 'ps', 'projetos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'projetos' AND acao = 'delete'
);

-- Tarefas
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_tarefas_ps', 'Permite visualizar tarefas', 'ps', 'tarefas', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'tarefas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_tarefas_ps', 'Permite criar tarefas', 'ps', 'tarefas', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'tarefas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_tarefas_ps', 'Permite atualizar tarefas', 'ps', 'tarefas', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'tarefas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_tarefas_ps', 'Permite remover tarefas', 'ps', 'tarefas', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'tarefas' AND acao = 'delete'
);

-- Recursos Alocados
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_recursos_alocados_ps', 'Permite visualizar recursos alocados', 'ps', 'recursosAlocados', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'recursosAlocados' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_recursos_alocados_ps', 'Permite criar recursos alocados', 'ps', 'recursosAlocados', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'recursosAlocados' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_recursos_alocados_ps', 'Permite atualizar recursos alocados', 'ps', 'recursosAlocados', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'recursosAlocados' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_recursos_alocados_ps', 'Permite remover recursos alocados', 'ps', 'recursosAlocados', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'ps' AND recurso = 'recursosAlocados' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'ps'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'ps'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'ps'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );