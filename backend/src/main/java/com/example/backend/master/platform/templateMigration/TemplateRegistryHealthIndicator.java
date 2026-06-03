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

    private Health avaliarRegistry(TemplateRegistry registry) {
        Health.Builder builder = statusBase(registry)
                .withDetail("database", registry.getDatabaseName())
                .withDetail("status", registry.getStatus())
                .withDetail("version", registry.getCurrentVersion())
                .withDetail("lockActive", registry.isLockActive());

        if (registry.isLockActive()) {
            return builder
                    .withDetail("reason", "template is locked for migration or clone")
                    .build();
        }

        if (registry.getCurrentVersion() == null || registry.getCurrentVersion().isBlank()) {
            return builder
                    .withDetail("reason", "template version is not defined")
                    .build();
        }

        if (!"READY".equalsIgnoreCase(registry.getStatus())) {
            return builder
                    .withDetail("reason", "template is not ready")
                    .build();
        }

        return Health.up()
                .withDetail("database", registry.getDatabaseName())
                .withDetail("status", registry.getStatus())
                .withDetail("version", registry.getCurrentVersion())
                .withDetail("lockActive", registry.isLockActive())
                .build();
    }

    private Health.Builder statusBase(TemplateRegistry registry) {
        String status = registry.getStatus();

        if ("ERROR".equalsIgnoreCase(status)) {
            return Health.down();
        }

        return Health.outOfService();
    }
}
