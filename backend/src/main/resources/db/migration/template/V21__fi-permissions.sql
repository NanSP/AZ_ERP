-- Permissoes base do modulo financeiro
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_plano_contas', 'Permite visualizar plano de contas', 'fi', 'plano_contas', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'plano_contas' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_plano_contas', 'Permite criar plano de contas', 'fi', 'plano_contas', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'plano_contas' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_plano_contas', 'Permite atualizar plano de contas', 'fi', 'plano_contas', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'plano_contas' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_plano_contas', 'Permite excluir plano de contas', 'fi', 'plano_contas', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'plano_contas' AND acao = 'delete'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_centros_custo', 'Permite visualizar centros de custo', 'fi', 'centros_custo', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'centros_custo' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_centros_custo', 'Permite criar centros de custo', 'fi', 'centros_custo', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'centros_custo' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_centros_custo', 'Permite atualizar centros de custo', 'fi', 'centros_custo', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'centros_custo' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_centros_custo', 'Permite excluir centros de custo', 'fi', 'centros_custo', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'centros_custo' AND acao = 'delete'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_contas_pagar', 'Permite visualizar contas a pagar', 'fi', 'contas_pagar', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_pagar' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_contas_pagar', 'Permite criar contas a pagar', 'fi', 'contas_pagar', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_pagar' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_contas_pagar', 'Permite atualizar contas a pagar', 'fi', 'contas_pagar', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_pagar' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_contas_pagar', 'Permite excluir contas a pagar', 'fi', 'contas_pagar', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_pagar' AND acao = 'delete'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_contas_receber', 'Permite visualizar contas a receber', 'fi', 'contas_receber', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_receber' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_contas_receber', 'Permite criar contas a receber', 'fi', 'contas_receber', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_receber' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_contas_receber', 'Permite atualizar contas a receber', 'fi', 'contas_receber', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_receber' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_contas_receber', 'Permite excluir contas a receber', 'fi', 'contas_receber', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'contas_receber' AND acao = 'delete'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_movimentacoes_bancarias', 'Permite visualizar movimentacoes bancarias', 'fi', 'movimentacoes_bancarias', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'movimentacoes_bancarias' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_movimentacoes_bancarias', 'Permite criar movimentacoes bancarias', 'fi', 'movimentacoes_bancarias', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'movimentacoes_bancarias' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_movimentacoes_bancarias', 'Permite atualizar movimentacoes bancarias', 'fi', 'movimentacoes_bancarias', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'movimentacoes_bancarias' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_movimentacoes_bancarias', 'Permite excluir movimentacoes bancarias', 'fi', 'movimentacoes_bancarias', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'movimentacoes_bancarias' AND acao = 'delete'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_fluxo_caixa', 'Permite visualizar fluxo de caixa', 'fi', 'fluxo_caixa', 'read'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'fluxo_caixa' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_fluxo_caixa', 'Permite criar fluxo de caixa', 'fi', 'fluxo_caixa', 'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'fluxo_caixa' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_fluxo_caixa', 'Permite atualizar fluxo de caixa', 'fi', 'fluxo_caixa', 'update'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'fluxo_caixa' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_fluxo_caixa', 'Permite excluir fluxo de caixa', 'fi', 'fluxo_caixa', 'delete'
WHERE NOT EXISTS (
    SELECT 1
    FROM sys.permissoes
    WHERE modulo = 'fi' AND recurso = 'fluxo_caixa' AND acao = 'delete'
);

-- Vinculo: ADMIN_TENANT recebe CRUD completo em FI
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'fi'
WHERE p.nome = 'ADMIN_TENANT'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- Vinculo: USUARIO_PADRAO recebe somente leitura em FI
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'fi'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );

-- Vinculo: MASTER_TECNICO recebe leitura + update em FI
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe
  ON pe.modulo = 'fi'
 AND pe.acao IN ('read', 'update')
WHERE p.nome = 'MASTER_TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );