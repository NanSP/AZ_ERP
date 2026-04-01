package com.example.backend.master.platform.tenantProvisioning.services;

public class TenantDatabaseProvisioningException extends RuntimeException {
    public TenantDatabaseProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}
