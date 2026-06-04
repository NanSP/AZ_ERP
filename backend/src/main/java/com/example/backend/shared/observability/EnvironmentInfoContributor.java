package com.example.backend.shared.observability;

import org.springframework.core.env.Environment;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EnvironmentInfoContributor implements InfoContributor {

    private final Environment environment;

    public EnvironmentInfoContributor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("applicationName", environment.getProperty("spring.application.name", "backend"));
        details.put("profiles", Arrays.asList(environment.getActiveProfiles()));
        builder.withDetail("environment", details);
    }
}
