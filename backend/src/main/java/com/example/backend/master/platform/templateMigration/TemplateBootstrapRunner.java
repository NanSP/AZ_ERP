package com.example.backend.master.platform.templateMigration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
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
