package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateInfoContributor implements InfoContributor {

    private final TemplateRegistryRepository repository;

    public TemplateInfoContributor(TemplateRegistryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        repository.findByDatabaseName("az_erp_template")
                .ifPresentOrElse(
                        registry -> builder.withDetail("template", Map.of(
                                "database", registry.getDatabaseName(),
                                "status", registry.getStatus(),
                                "version", registry.getCurrentVersion(),
                                "lockActive", registry.isLockActive()
                        )),
                        () -> builder.withDetail("template", Map.of(
                                "database", "az_erp_template",
                                "status", "NOT_REGISTERED"
                        ))
                );
    }
}
