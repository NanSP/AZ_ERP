package com.example.backend.privacy;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.privacy.retention")
public class DataRetentionProperties {

    private String cron = "0 30 2 * * *";
    private int actionLogsDays = 180;
    private int errorLogsDays = 90;
    private int provisioningLogsDays = 180;
    private int sessionsDays = 30;
    private int inactiveDevicesDays = 90;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getActionLogsDays() {
        return actionLogsDays;
    }

    public void setActionLogsDays(int actionLogsDays) {
        this.actionLogsDays = actionLogsDays;
    }

    public int getErrorLogsDays() {
        return errorLogsDays;
    }

    public void setErrorLogsDays(int errorLogsDays) {
        this.errorLogsDays = errorLogsDays;
    }

    public int getProvisioningLogsDays() {
        return provisioningLogsDays;
    }

    public void setProvisioningLogsDays(int provisioningLogsDays) {
        this.provisioningLogsDays = provisioningLogsDays;
    }

    public int getSessionsDays() {
        return sessionsDays;
    }

    public void setSessionsDays(int sessionsDays) {
        this.sessionsDays = sessionsDays;
    }

    public int getInactiveDevicesDays() {
        return inactiveDevicesDays;
    }

    public void setInactiveDevicesDays(int inactiveDevicesDays) {
        this.inactiveDevicesDays = inactiveDevicesDays;
    }
}
