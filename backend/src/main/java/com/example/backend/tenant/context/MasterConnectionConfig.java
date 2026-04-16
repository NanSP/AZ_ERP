package com.example.backend.tenant.context;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MasterConnectionProperties.class)
public class MasterConnectionConfig {
}
