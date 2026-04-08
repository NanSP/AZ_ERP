INSERT INTO sys.perfis (nome, descricao, nivel_acesso)
VALUES
    ('MASTER_TECNICO', 'Usuario master tecnico para manutencao do tenant', 10),
    ('ADMIN_TENANT', 'Administrador do tenant', 8),
    ('USUARIO_PADRAO', 'Usuario padrao do tenant', 1);

INSERT INTO sys.permissoes (nome, descricao, modulo, recurso, acao)
VALUES
    ('read_empresas', 'Ler empresas', 'core', 'empresas', 'read'),
    ('write_empresas', 'Editar empresas', 'core', 'empresas', 'update'),
    ('read_usuarios', 'Ler usuarios', 'sys', 'usuarios', 'read'),
    ('write_usuarios', 'Editar usuarios', 'sys', 'usuarios', 'update'),
    ('read_perfis', 'Ler perfis', 'sys', 'perfis', 'read'),
    ('write_perfis', 'Editar perfis', 'sys', 'perfis', 'update');

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM sys.perfis p
JOIN sys.permissoes pm ON pm.nome IN (
    'read_empresas',
    'write_empresas',
    'read_usuarios',
    'write_usuarios',
    'read_perfis',
    'write_perfis'
)
WHERE p.nome = 'MASTER_TECNICO';

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM sys.perfis p
JOIN sys.permissoes pm ON pm.nome IN (
    'read_empresas',
    'write_empresas',
    'read_usuarios',
    'write_usuarios'
)
WHERE p.nome = 'ADMIN_TENANT';

INSERT INTO sys.perfil_permissao (perfil_id, permissao_id)
SELECT p.id, pm.id
FROM sys.perfis p
JOIN sys.permissoes pm ON pm.nome IN (
    'read_empresas'
)
WHERE p.nome = 'USUARIO_PADRAO';

INSERT INTO sys.usuarios (nome, email, login, senha_hash, tipo_usuario, status)
VALUES
    ('Master Tecnico', 'master@tenant.local', 'master.tenant', '240be518fabd2724ddb6f04eebbf2f062801ab8ff993b0b3cf7e8cfa2d789a22', 'sistema', 'ativo'),
    ('Administrador Tenant', 'admin@tenant.local', 'admin.tenant', '240be518fabd2724ddb6f04eebbf2f062801ab8ff993b0b3cf7e8cfa2d789a22', 'administrador', 'ativo'),
    ('Usuario Padrao', 'user@tenant.local', 'user.tenant', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'usuario', 'ativo');

INSERT INTO sys.usuario_perfil (usuario_id, perfil_id)
SELECT u.id, p.id
FROM sys.usuarios u
JOIN sys.perfis p ON p.nome = 'MASTER_TECNICO'
WHERE u.login = 'master.tenant';

INSERT INTO sys.usuario_perfil (usuario_id, perfil_id)
SELECT u.id, p.id
FROM sys.usuarios u
JOIN sys.perfis p ON p.nome = 'ADMIN_TENANT'
WHERE u.login = 'admin.tenant';

INSERT INTO sys.usuario_perfil (usuario_id, perfil_id)
SELECT u.id, p.id
FROM sys.usuarios u
JOIN sys.perfis p ON p.nome = 'USUARIO_PADRAO'
WHERE u.login = 'user.tenant';
