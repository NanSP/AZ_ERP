-- Sessoes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_sessoes', 'Permite visualizar sessoes', 'portal', 'sessoes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'sessoes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_sessoes', 'Permite criar sessoes', 'portal', 'sessoes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'sessoes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_sessoes', 'Permite atualizar sessoes', 'portal', 'sessoes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'sessoes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_sessoes', 'Permite excluir sessoes', 'portal', 'sessoes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'sessoes' AND acao = 'delete'
);

-- Notificacoes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_notificacoes', 'Permite visualizar notificacoes', 'portal', 'notificacoes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'notificacoes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_notificacoes', 'Permite criar notificacoes', 'portal', 'notificacoes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'notificacoes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_notificacoes', 'Permite atualizar notificacoes', 'portal', 'notificacoes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'notificacoes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_notificacoes', 'Permite excluir notificacoes', 'portal', 'notificacoes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'notificacoes' AND acao = 'delete'
);

-- Dispositivos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_dispositivos', 'Permite visualizar dispositivos', 'portal', 'dispositivos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'dispositivos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_dispositivos', 'Permite criar dispositivos', 'portal', 'dispositivos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'dispositivos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_dispositivos', 'Permite atualizar dispositivos', 'portal', 'dispositivos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'dispositivos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_dispositivos', 'Permite excluir dispositivos', 'portal', 'dispositivos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'portal' AND recurso = 'dispositivos' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo em PORTAL
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'portal'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update em sessoes, notificacoes e dispositivos
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'portal'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO: read em notificacoes e dispositivos
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'portal'
 AND pe.acao = 'read'
 AND pe.recurso IN ('notificacoes', 'dispositivos')
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );