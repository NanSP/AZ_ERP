package com.example.backend.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/tenant/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers("/platform/systemUsers/**").hasRole("ADMIN_SISTEMA")
                        .requestMatchers("/platform/tenantProvisioning/**").hasRole("ADMIN_SISTEMA")
                        .requestMatchers("/platform/templateMigration/**").hasRole("ADMIN_SISTEMA")
                        .requestMatchers("/platform/tenantDatabases/**").hasRole("ADMIN_SISTEMA")
                        .requestMatchers("/platform/tenantAdminUsers/**").hasRole("ADMIN_SISTEMA")

                        .requestMatchers(HttpMethod.GET, "/platform/tenants/**").hasAnyRole("ADMIN_SISTEMA", "SUPORTE")
                        .requestMatchers(HttpMethod.GET, "/platform/provisioningLogs/**").hasAnyRole("ADMIN_SISTEMA", "SUPORTE")

                        .requestMatchers("/platform/**").hasAuthority("SCOPE_MASTER")

                        .requestMatchers("/core/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/sys/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/fi/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/rh/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/mm/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/sd/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/pp/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/sm/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/qm/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/ps/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/portal/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/bi/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/grc/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/am/**").hasAuthority("SCOPE_TENANT")
                        .requestMatchers("/fiscal/**").hasAuthority("SCOPE_TENANT")

                        .anyRequest().permitAll()


                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
