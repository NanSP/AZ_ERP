package com.example.backend.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(10)
public class MasterDevSeedRunner implements ApplicationRunner {

    private final MasterDevSeedService masterDevSeedService;

    public MasterDevSeedRunner(MasterDevSeedService masterDevSeedService) {
        this.masterDevSeedService = masterDevSeedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        masterDevSeedService.ensureDevAdminSeed();
    }
}
