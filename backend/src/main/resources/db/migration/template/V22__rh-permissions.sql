-- Colaboradores
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_colaboradores', 'Permite visualizar colaboradores', 'rh', 'colaboradores', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'colaboradores' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_colaboradores', 'Permite criar colaboradores', 'rh', 'colaboradores', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'colaboradores' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_colaboradores', 'Permite atualizar colaboradores', 'rh', 'colaboradores', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'colaboradores' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_colaboradores', 'Permite excluir colaboradores', 'rh', 'colaboradores', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'colaboradores' AND acao = 'delete'
);

-- Dependentes
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_dependentes', 'Permite visualizar dependentes', 'rh', 'dependentes', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'dependentes' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_dependentes', 'Permite criar dependentes', 'rh', 'dependentes', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'dependentes' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_dependentes', 'Permite atualizar dependentes', 'rh', 'dependentes', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'dependentes' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_dependentes', 'Permite excluir dependentes', 'rh', 'dependentes', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'dependentes' AND acao = 'delete'
);

-- Folha de Pagamento
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_folha_pagamento', 'Permite visualizar folha de pagamento', 'rh', 'folha_pagamento', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'folha_pagamento' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_folha_pagamento', 'Permite criar folha de pagamento', 'rh', 'folha_pagamento', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'folha_pagamento' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_folha_pagamento', 'Permite atualizar folha de pagamento', 'rh', 'folha_pagamento', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'folha_pagamento' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_folha_pagamento', 'Permite excluir folha de pagamento', 'rh', 'folha_pagamento', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'folha_pagamento' AND acao = 'delete'
);

-- Beneficios
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_beneficios', 'Permite visualizar beneficios', 'rh', 'beneficios', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'beneficios' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_beneficios', 'Permite criar beneficios', 'rh', 'beneficios', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'beneficios' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_beneficios', 'Permite atualizar beneficios', 'rh', 'beneficios', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'beneficios' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_beneficios', 'Permite excluir beneficios', 'rh', 'beneficios', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'beneficios' AND acao = 'delete'
);

-- Controle de Ponto
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_controle_de_ponto', 'Permite visualizar controle de ponto', 'rh', 'controle_de_ponto', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'controle_de_ponto' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_controle_de_ponto', 'Permite criar controle de ponto', 'rh', 'controle_de_ponto', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'controle_de_ponto' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_controle_de_ponto', 'Permite atualizar controle de ponto', 'rh', 'controle_de_ponto', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'controle_de_ponto' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_controle_de_ponto', 'Permite excluir controle de ponto', 'rh', 'controle_de_ponto', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'rh' AND recurso = 'controle_de_ponto' AND acao = 'delete'
);

-- ADMIN_TENANT: CRUD completo em RH
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'rh'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- USUARIO_PADRAO: somente leitura em colaboradores, dependentes, beneficios e controle_de_ponto
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'rh'
 AND pe.acao = 'read'
 AND pe.recurso IN ('colaboradores', 'dependentes', 'beneficios', 'controle_de_ponto')
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- MASTER_TECNICO: read/update em colaboradores, dependentes, beneficios e controle_de_ponto
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'rh'
 AND (
      (pe.recurso IN ('colaboradores', 'dependentes', 'beneficios', 'controle_de_ponto') AND pe.acao IN ('read', 'update'))
      OR
      (pe.recurso = 'folha_pagamento' AND pe.acao = 'read')
 )
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );