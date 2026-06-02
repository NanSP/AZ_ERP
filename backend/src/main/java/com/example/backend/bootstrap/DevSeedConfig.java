package com.example.backend.bootstrap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        DevSeedProperties.class,
        MasterBootstrapProperties.class
})
public class DevSeedConfig {
}
