ALTER TABLE platform.tenant_admin_users
    DROP CONSTRAINT IF EXISTS ck_tenant_admin_role;

ALTER TABLE platform.tenant_admin_users
    DROP CONSTRAINT IF EXISTS ck_tenant_admin_status;

UPDATE platform.tenant_admin_users
SET role = CASE UPPER(role)
    WHEN 'ADMIN_TENANT' THEN 'TENANT_ADMIN'
    WHEN 'GESTOR' THEN 'TENANT_ADMIN'
    WHEN 'USUARIO' THEN 'SUPPORT'
    ELSE UPPER(role)
END
WHERE role IS NOT NULL;

UPDATE platform.tenant_admin_users
SET status = CASE UPPER(status)
    WHEN 'BLOQUEADO' THEN 'SUSPENSO'
    ELSE UPPER(status)
END
WHERE status IS NOT NULL;

ALTER TABLE platform.tenant_admin_users
    ADD CONSTRAINT ck_tenant_admin_role
        CHECK (role IN ('MASTER_ADMIN', 'TENANT_ADMIN', 'SUPPORT'));

ALTER TABLE platform.tenant_admin_users
    ADD CONSTRAINT ck_tenant_admin_status
        CHECK (status IN ('ATIVO', 'INATIVO', 'SUSPENSO'));
