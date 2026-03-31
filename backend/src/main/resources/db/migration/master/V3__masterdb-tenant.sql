INSERT INTO platform.tenants (
    codigo,
    nome,
    nome_fantasia,
    documento,
    tipo_documento,
    email_responsavel,
    telefone_responsavel,
    status,
    plano
) VALUES (
    'TENANT_DEMO',
    'Empresa Demo Ltda',
    'Empresa Demo',
    '12345678000199',
    'CNPJ',
    'contato@demo.com',
    '11999999999',
    'PENDENTE',
    'BASICO'
);
