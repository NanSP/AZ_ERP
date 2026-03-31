CREATE SCHEMA IF NOT EXISTS platform;

CREATE TABLE platform.tenants (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    nome_fantasia VARCHAR(200),
    documento VARCHAR(20),
    tipo_documento VARCHAR(20),
    email_responsavel VARCHAR(150),
    telefone_responsavel VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    plano VARCHAR(50) NOT NULL DEFAULT 'BASICO',
    schema_version VARCHAR(20) NOT NULL DEFAULT 'v1',
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_tenants_status CHECK (status IN ('PENDENTE', 'ATIVO', 'INATIVO', 'SUSPENSO'))
);

CREATE TABLE platform.tenant_databases (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE REFERENCES platform.tenants(id) ON DELETE CASCADE,
    database_name VARCHAR(100) NOT NULL UNIQUE,
    template_name VARCHAR(100) NOT NULL DEFAULT 'az_erp_template',
    db_host VARCHAR(150) NOT NULL DEFAULT 'localhost',
    db_port INTEGER NOT NULL DEFAULT 5432,
    db_username VARCHAR(100) NOT NULL,
    db_password_encrypted TEXT,
    provision_status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    provisioned_at TIMESTAMP,
    last_check_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_tenant_databases_status CHECK (provision_status IN ('PENDENTE', 'CRIANDO', 'ATIVO', 'ERRO', 'ARQUIVADO'))
);

CREATE TABLE platform.system_users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    login VARCHAR(80) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    ultimo_acesso TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_system_users_role CHECK (role IN ('ADMIN_SISTEMA', 'SUPORTE', 'OPERADOR')),
    CONSTRAINT ck_system_users_status CHECK (status IN ('ATIVO', 'INATIVO', 'BLOQUEADO'))
);

CREATE TABLE platform.tenant_admin_users (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES platform.tenants(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    login VARCHAR(80) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ADMIN_TENANT',
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    ultimo_acesso TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_tenant_admin_email UNIQUE (tenant_id, email),
    CONSTRAINT uq_tenant_admin_login UNIQUE (tenant_id, login),
    CONSTRAINT ck_tenant_admin_role CHECK (role IN ('ADMIN_TENANT', 'GESTOR', 'USUARIO')),
    CONSTRAINT ck_tenant_admin_status CHECK (status IN ('ATIVO', 'INATIVO', 'BLOQUEADO'))
);

CREATE TABLE platform.provisioning_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT REFERENCES platform.tenants(id) ON DELETE SET NULL,
    etapa VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    mensagem TEXT,
    detalhes JSONB,
    executado_por BIGINT REFERENCES platform.system_users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_provisioning_logs_status CHECK (status IN ('INFO', 'SUCESSO', 'ERRO'))
);

CREATE INDEX idx_tenants_status ON platform.tenants(status);
CREATE INDEX idx_tenants_plano ON platform.tenants(plano);
CREATE INDEX idx_tenant_databases_status ON platform.tenant_databases(provision_status);
CREATE INDEX idx_tenant_admin_users_tenant_id ON platform.tenant_admin_users(tenant_id);
CREATE INDEX idx_provisioning_logs_tenant_id ON platform.provisioning_logs(tenant_id);
CREATE INDEX idx_provisioning_logs_created_at ON platform.provisioning_logs(created_at);
