package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TemplateInfoContributor implements InfoContributor {

    private static final String TEMPLATE_DATABASE = "az_erp_template";

    private final TemplateRegistryRepository repository;

    public TemplateInfoContributor(TemplateRegistryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        repository.findByDatabaseName(TEMPLATE_DATABASE)
                .ifPresentOrElse(
                        registry -> builder.withDetail("template", buildTemplateDetails(registry)),
                        () -> builder.withDetail("template", Map.of(
                                "component", "templateRegistry",
                                "database", TEMPLATE_DATABASE,
                                "status", "NOT_REGISTERED"
                        ))
                );
    }

    private Map<String, Object> buildTemplateDetails(TemplateRegistry registry) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("component", "templateRegistry");
        details.put("database", safe(registry.getDatabaseName()));
        details.put("status", safe(registry.getStatus()));
        details.put("version", safe(registry.getCurrentVersion()));
        details.put("lockActive", registry.isLockActive());
        details.put("lastMigratedAt", safe(registry.getLastMigratedAt()));
        details.put("lastValidatedAt", safe(registry.getLastValidatedAt()));
        details.put("lastClonedAt", safe(registry.getLastClonedAt()));
        return details;
    }

    private Object safe(Object value) {
        return value != null ? value : "N/A";
    }

}
