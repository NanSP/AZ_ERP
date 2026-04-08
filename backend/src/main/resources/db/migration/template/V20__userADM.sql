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
    ('Master Tecnico', 'master@tenant.local', 'master.tenant', '$2a$10$8N0JmLw8mQ4r6Q5eWQn8QeYQm2g7Hj0rD6xTt8m0xK1sP9bL2aF5G', 'sistema', 'ativo'),
    ('Administrador Tenant', 'admin@tenant.local', 'admin.tenant', '$2a$10$8N0JmLw8mQ4r6Q5eWQn8QeYQm2g7Hj0rD6xTt8m0xK1sP9bL2aF5G', 'administrador', 'ativo'),
    ('Usuario Padrao', 'user@tenant.local', 'user.tenant', '$2y$10$D1iNdmIQ9k3vMVtcQ9Js5ORtB8ZzQNFzVZzMABX8z1ir472MUBOq.', 'usuario', 'ativo');

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
