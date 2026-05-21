-- Ordens de Servico
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_ordens_servico_sm', 'Permite visualizar ordens de servico', 'sm', 'ordensServico', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'ordensServico' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_ordens_servico_sm', 'Permite criar ordens de servico', 'sm', 'ordensServico', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'ordensServico' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_ordens_servico_sm', 'Permite atualizar ordens de servico', 'sm', 'ordensServico', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'ordensServico' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_ordens_servico_sm', 'Permite remover ordens de servico', 'sm', 'ordensServico', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'ordensServico' AND acao = 'delete'
);

-- Atendimentos
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_atendimentos_sm', 'Permite visualizar atendimentos', 'sm', 'atendimentos', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'atendimentos' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_atendimentos_sm', 'Permite criar atendimentos', 'sm', 'atendimentos', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'atendimentos' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_atendimentos_sm', 'Permite atualizar atendimentos', 'sm', 'atendimentos', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'atendimentos' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_atendimentos_sm', 'Permite remover atendimentos', 'sm', 'atendimentos', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'atendimentos' AND acao = 'delete'
);

-- SLA Config
INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'read_sla_config_sm', 'Permite visualizar configuracoes de SLA', 'sm', 'slaConfig', 'read'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'slaConfig' AND acao = 'read'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'create_sla_config_sm', 'Permite criar configuracoes de SLA', 'sm', 'slaConfig', 'create'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'slaConfig' AND acao = 'create'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'update_sla_config_sm', 'Permite atualizar configuracoes de SLA', 'sm', 'slaConfig', 'update'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'slaConfig' AND acao = 'update'
);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
SELECT 'delete_sla_config_sm', 'Permite remover configuracoes de SLA', 'sm', 'slaConfig', 'delete'
WHERE NOT EXISTS (
    SELECT 1 FROM sys.permissoes
    WHERE modulo = 'sm' AND recurso = 'slaConfig' AND acao = 'delete'
);

-- Perfis
INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pe.id
FROM sys.perfis p
JOIN sys.permissoes pe ON pe.modulo = 'sm'
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
  ON pe.modulo = 'sm'
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
  ON pe.modulo = 'sm'
 AND pe.acao = 'read'
WHERE p.nome = 'USUARIO_PADRAO'
  AND NOT EXISTS (
      SELECT 1
      FROM sys.perfil_permissao pp
      WHERE pp.perfil_id = p.id
        AND pp.permissao_id = pe.id
  );