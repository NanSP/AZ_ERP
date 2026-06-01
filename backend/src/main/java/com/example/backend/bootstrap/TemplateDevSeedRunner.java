package com.example.backend.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(30)
public class TemplateDevSeedRunner implements ApplicationRunner {

    private final TemplateDevSeedService templateDevSeedService;

    public TemplateDevSeedRunner(TemplateDevSeedService templateDevSeedService) {
        this.templateDevSeedService = templateDevSeedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        templateDevSeedService.ensureTemplateDevUsers();
    }
}
