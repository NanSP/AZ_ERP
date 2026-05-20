-- Empresas
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_empresas', 'Permite visualizar empresas', 'core', 'empresas', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'empresas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_empresas', 'Permite criar empresas', 'core', 'empresas', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'empresas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_empresas', 'Permite atualizar empresas', 'core', 'empresas', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'empresas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_empresas', 'Permite excluir empresas', 'core', 'empresas', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'empresas' AND acao = 'delete'
);

-- Enderecos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_enderecos', 'Permite visualizar enderecos', 'core', 'enderecos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'enderecos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_enderecos', 'Permite criar enderecos', 'core', 'enderecos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'enderecos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_enderecos', 'Permite atualizar enderecos', 'core', 'enderecos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'enderecos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_enderecos', 'Permite excluir enderecos', 'core', 'enderecos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'enderecos' AND acao = 'delete'
);

-- Contatos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_contatos', 'Permite visualizar contatos', 'core', 'contatos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'contatos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_contatos', 'Permite criar contatos', 'core', 'contatos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'contatos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_contatos', 'Permite atualizar contatos', 'core', 'contatos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'contatos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_contatos', 'Permite excluir contatos', 'core', 'contatos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'contatos' AND acao = 'delete'
);

-- Parceiros
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_parceiros', 'Permite visualizar parceiros', 'core', 'parceiros', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'parceiros' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_parceiros', 'Permite criar parceiros', 'core', 'parceiros', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'parceiros' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_parceiros', 'Permite atualizar parceiros', 'core', 'parceiros', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'parceiros' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_parceiros', 'Permite excluir parceiros', 'core', 'parceiros', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'parceiros' AND acao = 'delete'
);

-- Produtos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_produtos', 'Permite visualizar produtos', 'core', 'produtos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'produtos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_produtos', 'Permite criar produtos', 'core', 'produtos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'produtos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_produtos', 'Permite atualizar produtos', 'core', 'produtos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'produtos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_produtos', 'Permite excluir produtos', 'core', 'produtos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'core' AND recurso = 'produtos' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo em CORE
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'core'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'core'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO:
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'core'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );