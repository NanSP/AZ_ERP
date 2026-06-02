package com.example.backend.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap.dev")
public class DevSeedProperties {

    private boolean enabled;
    private String masterAdminPassword;
    private String tenantTechnicalPassword;
    private String tenantAdminPassword;
    private String tenantUserPassword;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMasterAdminPassword() {
        return masterAdminPassword;
    }

    public void setMasterAdminPassword(String masterAdminPassword) {
        this.masterAdminPassword = masterAdminPassword;
    }

    public String getTenantTechnicalPassword() {
        return tenantTechnicalPassword;
    }

    public void setTenantTechnicalPassword(String tenantTechnicalPassword) {
        this.tenantTechnicalPassword = tenantTechnicalPassword;
    }

    public String getTenantAdminPassword() {
        return tenantAdminPassword;
    }

    public void setTenantAdminPassword(String tenantAdminPassword) {
        this.tenantAdminPassword = tenantAdminPassword;
    }

    public String getTenantUserPassword() {
        return tenantUserPassword;
    }

    public void setTenantUserPassword(String tenantUserPassword) {
        this.tenantUserPassword = tenantUserPassword;
    }
}
