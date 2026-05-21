-- Documentos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_documentos_fiscal', 'Permite visualizar documentos fiscais', 'fiscal', 'documentos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'documentos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_documentos_fiscal', 'Permite criar documentos fiscais', 'fiscal', 'documentos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'documentos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_documentos_fiscal', 'Permite atualizar documentos fiscais', 'fiscal', 'documentos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'documentos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_documentos_fiscal', 'Permite remover documentos fiscais', 'fiscal', 'documentos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'documentos' AND acao = 'delete'
);

-- ECD Registros
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_ecd_registros_fiscal', 'Permite visualizar registros ECD', 'fiscal', 'ecdRegistros', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'ecdRegistros' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_ecd_registros_fiscal', 'Permite criar registros ECD', 'fiscal', 'ecdRegistros', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'ecdRegistros' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_ecd_registros_fiscal', 'Permite atualizar registros ECD', 'fiscal', 'ecdRegistros', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'ecdRegistros' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_ecd_registros_fiscal', 'Permite remover registros ECD', 'fiscal', 'ecdRegistros', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'ecdRegistros' AND acao = 'delete'
);

-- EFD Registros
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_efd_registros_fiscal', 'Permite visualizar registros EFD', 'fiscal', 'efdRegistros', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'efdRegistros' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_efd_registros_fiscal', 'Permite criar registros EFD', 'fiscal', 'efdRegistros', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'efdRegistros' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_efd_registros_fiscal', 'Permite atualizar registros EFD', 'fiscal', 'efdRegistros', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'efdRegistros' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_efd_registros_fiscal', 'Permite remover registros EFD', 'fiscal', 'efdRegistros', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'efdRegistros' AND acao = 'delete'
);

-- eSocial Eventos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_esocial_eventos_fiscal', 'Permite visualizar eventos eSocial', 'fiscal', 'esocialEventos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'esocialEventos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_esocial_eventos_fiscal', 'Permite criar eventos eSocial', 'fiscal', 'esocialEventos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'esocialEventos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_esocial_eventos_fiscal', 'Permite atualizar eventos eSocial', 'fiscal', 'esocialEventos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'esocialEventos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_esocial_eventos_fiscal', 'Permite remover eventos eSocial', 'fiscal', 'esocialEventos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'fiscal' AND recurso = 'esocialEventos' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'fiscal'
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
  ON pe.modulo = 'fiscal'
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
  ON pe.modulo = 'fiscal'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );