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
    '$2a$10$8N0JmLw8mQ4r6Q5eWQn8QeYQm2g7Hj0rD6xTt8m0xK1sP9bL2aF5G',
    'ADMIN_SISTEMA',
    'ATIVO'
);
