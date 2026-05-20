INSERT INTO sys.permissoes (modulo, recurso, acao, descricao) VALUES
('mm', 'compras', 'read', 'Permite visualizar compras'),
('mm', 'compras', 'create', 'Permite criar compras'),
('mm', 'compras', 'update', 'Permite atualizar compras'),
('mm', 'compras', 'delete', 'Permite remover compras'),

('mm', 'compraItens', 'read', 'Permite visualizar itens de compra'),
('mm', 'compraItens', 'create', 'Permite criar itens de compra'),
('mm', 'compraItens', 'update', 'Permite atualizar itens de compra'),
('mm', 'compraItens', 'delete', 'Permite remover itens de compra'),

('mm', 'materiais', 'read', 'Permite visualizar materiais'),
('mm', 'materiais', 'create', 'Permite criar materiais'),
('mm', 'materiais', 'update', 'Permite atualizar materiais'),
('mm', 'materiais', 'delete', 'Permite remover materiais'),

('mm', 'inventarios', 'read', 'Permite visualizar inventarios'),
('mm', 'inventarios', 'create', 'Permite criar inventarios'),
('mm', 'inventarios', 'update', 'Permite atualizar inventarios'),
('mm', 'inventarios', 'delete', 'Permite remover inventarios'),

('mm', 'estoques', 'read', 'Permite visualizar estoques'),
('mm', 'estoques', 'create', 'Permite criar estoques'),
('mm', 'estoques', 'update', 'Permite atualizar estoques'),
('mm', 'estoques', 'delete', 'Permite remover estoques'),

('mm', 'movimentacoes', 'read', 'Permite visualizar movimentacoes'),
('mm', 'movimentacoes', 'create', 'Permite criar movimentacoes'),
('mm', 'movimentacoes', 'update', 'Permite atualizar movimentacoes'),
('mm', 'movimentacoes', 'delete', 'Permite remover movimentacoes');

-- ADMIN_TENANT: CRUD completo
INSERT INTO sys.perfis_permissoes (perfil_id, permissao_id)
SELECT pf.id, pm.id
FROM sys.perfis pf
JOIN sys.permissoes pm ON pm.modulo = 'mm'
WHERE pf.codigo = 'ADMIN_TENANT';

-- MASTER_TECNICO: leitura e atualização
INSERT INTO sys.perfis_permissoes (perfil_id, permissao_id)
SELECT pf.id, pm.id
FROM sys.perfis pf
JOIN sys.permissoes pm ON pm.modulo = 'mm'
WHERE pf.codigo = 'MASTER_TECNICO'
  AND pm.acao IN ('read', 'update');

-- USUARIO_PADRAO: somente leitura
INSERT INTO sys.perfis_permissoes (perfil_id, permissao_id)
SELECT pf.id, pm.id
FROM sys.perfis pf
JOIN sys.permissoes pm ON pm.modulo = 'mm'
WHERE pf.codigo = 'USUARIO_PADRAO'
  AND pm.acao = 'read';