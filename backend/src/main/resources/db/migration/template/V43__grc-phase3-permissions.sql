-- Relatorios de impacto
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_relatorios_impacto', 'Permite visualizar relatorios de impacto', 'grc', 'relatoriosImpacto', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'relatoriosImpacto' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_relatorios_impacto', 'Permite criar relatorios de impacto', 'grc', 'relatoriosImpacto', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'relatoriosImpacto' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_relatorios_impacto', 'Permite atualizar relatorios de impacto', 'grc', 'relatoriosImpacto', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'relatoriosImpacto' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_relatorios_impacto', 'Permite excluir relatorios de impacto', 'grc', 'relatoriosImpacto', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'relatoriosImpacto' AND acao = 'delete'
);

-- Incidentes de seguranca
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_incidentes_seguranca', 'Permite visualizar incidentes de seguranca', 'grc', 'incidentesSeguranca', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'incidentesSeguranca' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_incidentes_seguranca', 'Permite criar incidentes de seguranca', 'grc', 'incidentesSeguranca', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'incidentesSeguranca' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_incidentes_seguranca', 'Permite atualizar incidentes de seguranca', 'grc', 'incidentesSeguranca', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'incidentesSeguranca' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_incidentes_seguranca', 'Permite excluir incidentes de seguranca', 'grc', 'incidentesSeguranca', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'incidentesSeguranca' AND acao = 'delete'
);

-- Governanca de privacidade
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_governanca_privacidade', 'Permite visualizar governanca de privacidade', 'grc', 'governancaPrivacidade', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'governancaPrivacidade' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_governanca_privacidade', 'Permite criar governanca de privacidade', 'grc', 'governancaPrivacidade', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'governancaPrivacidade' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_governanca_privacidade', 'Permite atualizar governanca de privacidade', 'grc', 'governancaPrivacidade', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'governancaPrivacidade' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_governanca_privacidade', 'Permite excluir governanca de privacidade', 'grc', 'governancaPrivacidade', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'governancaPrivacidade' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo nos recursos da fase 3
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'grc'
 AND pe.recurso IN ('relatoriosImpacto', 'incidentesSeguranca', 'governancaPrivacidade')
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: leitura e atualizacao
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'grc'
 AND pe.recurso IN ('relatoriosImpacto', 'incidentesSeguranca', 'governancaPrivacidade')
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );
