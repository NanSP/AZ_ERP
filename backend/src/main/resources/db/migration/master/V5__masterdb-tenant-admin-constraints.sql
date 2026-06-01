ALTER TABLE platform.tenant_admin_users
    DROP CONSTRAINT IF EXISTS ck_tenant_admin_role;

ALTER TABLE platform.tenant_admin_users
    DROP CONSTRAINT IF EXISTS ck_tenant_admin_status;

ALTER TABLE platform.tenant_admin_users
    ADD CONSTRAINT ck_tenant_admin_role
        CHECK (role IN ('MASTER_ADMIN', 'TENANT_ADMIN', 'SUPPORT'));

ALTER TABLE platform.tenant_admin_users
    ADD CONSTRAINT ck_tenant_admin_status
        CHECK (status IN ('ATIVO', 'INATIVO', 'SUSPENSO'));
