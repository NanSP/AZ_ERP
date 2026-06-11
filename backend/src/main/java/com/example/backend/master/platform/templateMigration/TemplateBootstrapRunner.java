package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.bootstrap.template", name = "enabled", havingValue = "true", matchIfMissing = true)
@Order(20)
public class TemplateBootstrapRunner implements ApplicationRunner {

    private final TemplateBootstrapService templateBootstrapService;

    public TemplateBootstrapRunner(TemplateBootstrapService templateBootstrapService) {
        this.templateBootstrapService = templateBootstrapService;
    }

    @Override
    public void run(ApplicationArguments args) {
        templateBootstrapService.initializeTemplateIfNeeded();
    }
}
