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

                        .requestMatchers(HttpMethod.GET, "/sys/usuarios/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuarios:read")
                        .requestMatchers(HttpMethod.POST, "/sys/usuarios/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuarios:create")
                        .requestMatchers(HttpMethod.PUT, "/sys/usuarios/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuarios:update")
                        .requestMatchers(HttpMethod.DELETE, "/sys/usuarios/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuarios:delete")

                        .requestMatchers(HttpMethod.GET, "/sys/perfis/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfis:read")
                        .requestMatchers(HttpMethod.POST, "/sys/perfis/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfis:create")
                        .requestMatchers(HttpMethod.PUT, "/sys/perfis/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfis:update")
                        .requestMatchers(HttpMethod.DELETE, "/sys/perfis/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfis:delete")

                        .requestMatchers(HttpMethod.GET, "/sys/permissoes/**")
                        .hasAnyAuthority("PERMISSAO_sys:permissoes:read")
                        .requestMatchers(HttpMethod.POST, "/sys/permissoes/**")
                        .hasAnyAuthority("PERMISSAO_sys:permissoes:create")
                        .requestMatchers(HttpMethod.PUT, "/sys/permissoes/**")
                        .hasAnyAuthority("PERMISSAO_sys:permissoes:update")
                        .requestMatchers(HttpMethod.DELETE, "/sys/permissoes/**")
                        .hasAnyAuthority("PERMISSAO_sys:permissoes:delete")

                        .requestMatchers(HttpMethod.GET, "/sys/usuarioPerfil/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuario_perfil:read")
                        .requestMatchers(HttpMethod.POST, "/sys/usuarioPerfil/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuario_perfil:create")
                        .requestMatchers(HttpMethod.PUT, "/sys/usuarioPerfil/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuario_perfil:update")
                        .requestMatchers(HttpMethod.DELETE, "/sys/usuarioPerfil/**")
                        .hasAnyAuthority("PERMISSAO_sys:usuario_perfil:delete")

                        .requestMatchers(HttpMethod.GET, "/sys/perfilPermissao/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfil_permissao:read")
                        .requestMatchers(HttpMethod.POST, "/sys/perfilPermissao/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfil_permissao:create")
                        .requestMatchers(HttpMethod.PUT, "/sys/perfilPermissao/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfil_permissao:update")
                        .requestMatchers(HttpMethod.DELETE, "/sys/perfilPermissao/**")
                        .hasAnyAuthority("PERMISSAO_sys:perfil_permissao:delete")

                        .requestMatchers("/core/**").hasAuthority("SCOPE_TENANT")
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
