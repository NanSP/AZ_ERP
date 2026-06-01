DELETE FROM sys.usuario_perfil
WHERE usuario_id IN (
    SELECT id
    FROM sys.usuarios
    WHERE login IN ('master.tenant', 'admin.tenant', 'user.tenant')
);

DELETE FROM sys.usuarios
WHERE login IN ('master.tenant', 'admin.tenant', 'user.tenant');
