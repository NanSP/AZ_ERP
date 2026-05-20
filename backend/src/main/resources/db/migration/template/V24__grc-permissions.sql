-- Riscos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_riscos', 'Permite visualizar riscos', 'grc', 'riscos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'riscos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_riscos', 'Permite criar riscos', 'grc', 'riscos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'riscos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_riscos', 'Permite atualizar riscos', 'grc', 'riscos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'riscos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_riscos', 'Permite excluir riscos', 'grc', 'riscos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'riscos' AND acao = 'delete'
);

-- Controles
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_controles', 'Permite visualizar controles', 'grc', 'controles', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'controles' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_controles', 'Permite criar controles', 'grc', 'controles', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'controles' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_controles', 'Permite atualizar controles', 'grc', 'controles', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'controles' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_controles', 'Permite excluir controles', 'grc', 'controles', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'controles' AND acao = 'delete'
);

-- Auditorias
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_auditorias', 'Permite visualizar auditorias', 'grc', 'auditorias', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'auditorias' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_auditorias', 'Permite criar auditorias', 'grc', 'auditorias', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'auditorias' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_auditorias', 'Permite atualizar auditorias', 'grc', 'auditorias', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'auditorias' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_auditorias', 'Permite excluir auditorias', 'grc', 'auditorias', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'auditorias' AND acao = 'delete'
);

-- Consentimentos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_consentimentos', 'Permite visualizar consentimentos', 'grc', 'consentimentos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'consentimentos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_consentimentos', 'Permite criar consentimentos', 'grc', 'consentimentos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'consentimentos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_consentimentos', 'Permite atualizar consentimentos', 'grc', 'consentimentos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'consentimentos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_consentimentos', 'Permite excluir consentimentos', 'grc', 'consentimentos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'consentimentos' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo em GRC
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'grc'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update em riscos, controles e auditorias; read em consentimentos
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'grc'
 AND (
      (pe.recurso IN ('riscos', 'controles', 'auditorias') AND pe.acao IN ('read', 'update'))
      OR
      (pe.recurso = 'consentimentos' AND pe.acao = 'read')
 )
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO: read em riscos e controles
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'grc'
 AND pe.acao = 'read'
 AND pe.recurso IN ('riscos', 'controles')
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );