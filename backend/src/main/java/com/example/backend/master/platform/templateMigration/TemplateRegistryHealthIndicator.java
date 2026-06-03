package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TemplateRegistryHealthIndicator implements HealthIndicator {

    private final TemplateRegistryRepository repository;

    public TemplateRegistryHealthIndicator(TemplateRegistryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        return repository.findByDatabaseName("az_erp_template")
                .filter(registry -> "READY".equals(registry.getStatus()))
                .map(registry -> Health.up()
                        .withDetail("database", registry.getDatabaseName())
                        .withDetail("version", registry.getCurrentVersion())
                        .build())
                .orElseGet(() -> Health.outOfService()
                        .withDetail("database", "az_erp_template")
                        .withDetail("reason", "template not ready")
                        .build());
    }
}
