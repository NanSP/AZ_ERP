-- Bens Patrimoniais
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_bens_patrimoniais_am', 'Permite visualizar bens patrimoniais', 'am', 'bensPatrimoniais', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'bensPatrimoniais' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_bens_patrimoniais_am', 'Permite criar bens patrimoniais', 'am', 'bensPatrimoniais', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'bensPatrimoniais' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_bens_patrimoniais_am', 'Permite atualizar bens patrimoniais', 'am', 'bensPatrimoniais', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'bensPatrimoniais' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_bens_patrimoniais_am', 'Permite remover bens patrimoniais', 'am', 'bensPatrimoniais', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'bensPatrimoniais' AND acao = 'delete'
);

-- Manutencoes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_manutencoes_am', 'Permite visualizar manutencoes', 'am', 'manutencoes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'manutencoes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_manutencoes_am', 'Permite criar manutencoes', 'am', 'manutencoes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'manutencoes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_manutencoes_am', 'Permite atualizar manutencoes', 'am', 'manutencoes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'manutencoes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_manutencoes_am', 'Permite remover manutencoes', 'am', 'manutencoes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'am' AND recurso = 'manutencoes' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'am'
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
  ON pe.modulo = 'am'
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
  ON pe.modulo = 'am'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );