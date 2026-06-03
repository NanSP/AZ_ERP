package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TemplateRegistryHealthIndicator implements HealthIndicator {

    private static final String TEMPLATE_DATABASE = "az_erp_template";

    private final TemplateRegistryRepository repository;

    public TemplateRegistryHealthIndicator(TemplateRegistryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        return repository.findByDatabaseName(TEMPLATE_DATABASE)
                .map(this::avaliarRegistry)
                .orElseGet(() -> Health.outOfService()
                        .withDetail("component", "templateRegistry")
                        .withDetail("database", TEMPLATE_DATABASE)
                        .withDetail("reason", "template registry not found")
                        .build());
    }

    private Health avaliarRegistry(TemplateRegistry registry) {
        Health.Builder builder = statusBase(registry);
        addCommonDetails(builder, registry);

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

        Health.Builder up = Health.up();
        addCommonDetails(up, registry);
        return up.build();
    }

    private void addCommonDetails(Health.Builder builder, TemplateRegistry registry) {
        builder.withDetail("component", "templateRegistry");
        builder.withDetail("database", safe(registry.getDatabaseName()));
        builder.withDetail("status", safe(registry.getStatus()));
        builder.withDetail("version", safe(registry.getCurrentVersion()));
        builder.withDetail("lockActive", registry.isLockActive());
        builder.withDetail("lastMigratedAt", safe(registry.getLastMigratedAt()));
        builder.withDetail("lastValidatedAt", safe(registry.getLastValidatedAt()));
        builder.withDetail("lastClonedAt", safe(registry.getLastClonedAt()));
    }

    private Health.Builder statusBase(TemplateRegistry registry) {
        if ("ERROR".equalsIgnoreCase(registry.getStatus())) {
            return Health.down();
        }

        return Health.outOfService();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Object safe(Object value) {
        return value != null ? value : "N/A";
    }
}
