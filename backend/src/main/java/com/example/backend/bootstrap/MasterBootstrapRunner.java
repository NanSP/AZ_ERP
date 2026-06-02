package com.example.backend.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class MasterBootstrapRunner implements ApplicationRunner {

    private final MasterBootstrapService masterBootstrapService;

    public MasterBootstrapRunner(MasterBootstrapService masterBootstrapService) {
        this.masterBootstrapService = masterBootstrapService;
    }

    @Override
    public void run(ApplicationArguments args) {
        masterBootstrapService.ensureInitialAdmin();
    }
}
