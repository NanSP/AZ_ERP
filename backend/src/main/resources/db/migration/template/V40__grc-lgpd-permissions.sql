-- Registros de tratamento
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_registros_tratamento', 'Permite visualizar registros de tratamento', 'grc', 'registrosTratamento', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'registrosTratamento' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_registros_tratamento', 'Permite criar registros de tratamento', 'grc', 'registrosTratamento', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'registrosTratamento' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_registros_tratamento', 'Permite atualizar registros de tratamento', 'grc', 'registrosTratamento', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'registrosTratamento' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_registros_tratamento', 'Permite excluir registros de tratamento', 'grc', 'registrosTratamento', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'registrosTratamento' AND acao = 'delete'
);

-- Solicitacoes do titular
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_solicitacoes_titular', 'Permite visualizar solicitacoes do titular', 'grc', 'solicitacoesTitular', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'solicitacoesTitular' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_solicitacoes_titular', 'Permite criar solicitacoes do titular', 'grc', 'solicitacoesTitular', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'solicitacoesTitular' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_solicitacoes_titular', 'Permite atualizar solicitacoes do titular', 'grc', 'solicitacoesTitular', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'solicitacoesTitular' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_solicitacoes_titular', 'Permite excluir solicitacoes do titular', 'grc', 'solicitacoesTitular', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'grc' AND recurso = 'solicitacoesTitular' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'grc'
 AND pe.recurso IN ('registrosTratamento', 'solicitacoesTitular')
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
 AND pe.recurso IN ('registrosTratamento', 'solicitacoesTitular')
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );
