INSERT INTO platform.system_users (
    nome,
    email,
    login,
    senha_hash,
    role,
    status
) VALUES (
    'Administrador do Sistema',
    'admin@azerp.com',
    'admin.sistema',
    '$argon2id$v=19$m=65536,t=3,p=4$pNox9EB/zttWypEKaBdyGA$uKZQ9Gn2CPgYkHh2QTunK9Nmwz/QmOubi6n9K4ciTps',
    'ADMIN_SISTEMA',
    'ATIVO'
);
