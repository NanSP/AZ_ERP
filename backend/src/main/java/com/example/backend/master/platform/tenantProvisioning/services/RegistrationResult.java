package com.example.backend.master.platform.tenantProvisioning.services;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.tenantAdminUsers.TenantAdminUsers;
import com.example.backend.master.platform.tenantDatabases.TenantDatabases;
import com.example.backend.master.platform.tenants.Tenants;

import java.util.List;

public record RegistrationResult(
        Tenants tenant,
        TenantDatabases tenantDatabase,
        TenantAdminUsers tenantAdmin,
        SystemUsers executor,
        List<String> etapasExecutadas
) {
}
