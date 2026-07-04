package com.example.backend.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Autenticacao stateful nao utilizada nesta aplicacao");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> {
                    configurarRotasPublicas(auth);
                    configurarPlatform(auth);
                    configurarCore(auth);
                    configurarSys(auth);
                    configurarFi(auth);
                    configurarRh(auth);
                    configurarGrc(auth);
                    configurarPortal(auth);
                    configurarBi(auth);
                    configurarMm(auth);
                    configurarSd(auth);
                    configurarPp(auth);
                    configurarSm(auth);
                    configurarQm(auth);
                    configurarPs(auth);
                    configurarAm(auth);
                    configurarFiscal(auth);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.allowed-origins}") String allowedOriginsProperty
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOriginsProperty.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void configurarRotasPublicas(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/auth/login").permitAll();
        auth.requestMatchers("/tenant/auth/login").permitAll();
        auth.requestMatchers("/auth/me").hasAnyAuthority("SCOPE_MASTER", "SCOPE_TENANT");
        auth.requestMatchers("/tenant/auth/me").hasAnyAuthority("SCOPE_TENANT");
        auth.requestMatchers("/auth/logout").hasAnyAuthority("SCOPE_MASTER", "SCOPE_TENANT");
        auth.requestMatchers("/tenant/auth/logout").hasAnyAuthority("SCOPE_TENANT");
        auth.requestMatchers("/auth/change-password").hasAnyAuthority("SCOPE_MASTER");
        auth.requestMatchers("/tenant/auth/change-password").hasAnyAuthority("SCOPE_TENANT");
        auth.requestMatchers("/error").permitAll();
        auth.requestMatchers("/actuator/health").permitAll();
        auth.requestMatchers("/actuator/health/**").permitAll();
        auth.requestMatchers("/actuator/info").permitAll();
    }

    private void configurarPlatform(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers("/platform/systemUsers/**").hasRole("ADMIN_SISTEMA");
        auth.requestMatchers("/platform/tenantProvisioning/**").hasRole("ADMIN_SISTEMA");
        auth.requestMatchers("/platform/templateMigration/**").hasRole("ADMIN_SISTEMA");
        auth.requestMatchers("/platform/tenantDatabases/**").hasRole("ADMIN_SISTEMA");
        auth.requestMatchers("/platform/tenantAdminUsers/**").hasRole("ADMIN_SISTEMA");

        auth.requestMatchers(HttpMethod.GET, "/platform/tenants/**").hasAnyRole("ADMIN_SISTEMA", "SUPORTE");
        auth.requestMatchers(HttpMethod.GET, "/platform/provisioningLogs/**").hasAnyRole("ADMIN_SISTEMA", "SUPORTE");

        auth.requestMatchers("/platform/**").hasAnyAuthority("SCOPE_MASTER");
    }

    private void configurarCore(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/core/empresas/**").hasAnyAuthority("PERMISSAO_core:empresas:read");
        auth.requestMatchers(HttpMethod.POST, "/core/empresas/**").hasAnyAuthority("PERMISSAO_core:empresas:create");
        auth.requestMatchers(HttpMethod.PUT, "/core/empresas/**").hasAnyAuthority("PERMISSAO_core:empresas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/core/empresas/**").hasAnyAuthority("PERMISSAO_core:empresas:delete");

        auth.requestMatchers(HttpMethod.GET, "/core/enderecos/**").hasAnyAuthority("PERMISSAO_core:enderecos:read");
        auth.requestMatchers(HttpMethod.POST, "/core/enderecos/**").hasAnyAuthority("PERMISSAO_core:enderecos:create");
        auth.requestMatchers(HttpMethod.PUT, "/core/enderecos/**").hasAnyAuthority("PERMISSAO_core:enderecos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/core/enderecos/**").hasAnyAuthority("PERMISSAO_core:enderecos:delete");

        auth.requestMatchers(HttpMethod.GET, "/core/contatos/**").hasAnyAuthority("PERMISSAO_core:contatos:read");
        auth.requestMatchers(HttpMethod.POST, "/core/contatos/**").hasAnyAuthority("PERMISSAO_core:contatos:create");
        auth.requestMatchers(HttpMethod.PUT, "/core/contatos/**").hasAnyAuthority("PERMISSAO_core:contatos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/core/contatos/**").hasAnyAuthority("PERMISSAO_core:contatos:delete");

        auth.requestMatchers(HttpMethod.GET, "/core/parceiros/**").hasAnyAuthority("PERMISSAO_core:parceiros:read");
        auth.requestMatchers(HttpMethod.POST, "/core/parceiros/**").hasAnyAuthority("PERMISSAO_core:parceiros:create");
        auth.requestMatchers(HttpMethod.PUT, "/core/parceiros/**").hasAnyAuthority("PERMISSAO_core:parceiros:update");
        auth.requestMatchers(HttpMethod.DELETE, "/core/parceiros/**").hasAnyAuthority("PERMISSAO_core:parceiros:delete");

        auth.requestMatchers(HttpMethod.GET, "/core/produtos/**").hasAnyAuthority("PERMISSAO_core:produtos:read");
        auth.requestMatchers(HttpMethod.POST, "/core/produtos/**").hasAnyAuthority("PERMISSAO_core:produtos:create");
        auth.requestMatchers(HttpMethod.PUT, "/core/produtos/**").hasAnyAuthority("PERMISSAO_core:produtos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/core/produtos/**").hasAnyAuthority("PERMISSAO_core:produtos:delete");
    }

    private void configurarSys(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/sys/usuarios/**").hasAnyAuthority("PERMISSAO_sys:usuarios:read");
        auth.requestMatchers(HttpMethod.POST, "/sys/usuarios/**").hasAnyAuthority("PERMISSAO_sys:usuarios:create");
        auth.requestMatchers(HttpMethod.PUT, "/sys/usuarios/**").hasAnyAuthority("PERMISSAO_sys:usuarios:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sys/usuarios/**").hasAnyAuthority("PERMISSAO_sys:usuarios:delete");

        auth.requestMatchers(HttpMethod.GET, "/sys/perfis/**").hasAnyAuthority("PERMISSAO_sys:perfis:read");
        auth.requestMatchers(HttpMethod.POST, "/sys/perfis/**").hasAnyAuthority("PERMISSAO_sys:perfis:create");
        auth.requestMatchers(HttpMethod.PUT, "/sys/perfis/**").hasAnyAuthority("PERMISSAO_sys:perfis:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sys/perfis/**").hasAnyAuthority("PERMISSAO_sys:perfis:delete");

        auth.requestMatchers(HttpMethod.GET, "/sys/permissoes/**").hasAnyAuthority("PERMISSAO_sys:permissoes:read");
        auth.requestMatchers(HttpMethod.POST, "/sys/permissoes/**").hasAnyAuthority("PERMISSAO_sys:permissoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/sys/permissoes/**").hasAnyAuthority("PERMISSAO_sys:permissoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sys/permissoes/**").hasAnyAuthority("PERMISSAO_sys:permissoes:delete");

        auth.requestMatchers(HttpMethod.GET, "/sys/usuarioPerfil/**").hasAnyAuthority("PERMISSAO_sys:usuario_perfil:read");
        auth.requestMatchers(HttpMethod.POST, "/sys/usuarioPerfil/**").hasAnyAuthority("PERMISSAO_sys:usuario_perfil:create");
        auth.requestMatchers(HttpMethod.PUT, "/sys/usuarioPerfil/**").hasAnyAuthority("PERMISSAO_sys:usuario_perfil:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sys/usuarioPerfil/**").hasAnyAuthority("PERMISSAO_sys:usuario_perfil:delete");

        auth.requestMatchers(HttpMethod.GET, "/sys/perfilPermissao/**").hasAnyAuthority("PERMISSAO_sys:perfil_permissao:read");
        auth.requestMatchers(HttpMethod.POST, "/sys/perfilPermissao/**").hasAnyAuthority("PERMISSAO_sys:perfil_permissao:create");
        auth.requestMatchers(HttpMethod.PUT, "/sys/perfilPermissao/**").hasAnyAuthority("PERMISSAO_sys:perfil_permissao:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sys/perfilPermissao/**").hasAnyAuthority("PERMISSAO_sys:perfil_permissao:delete");
    }

    private void configurarFi(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/fi/planoContas/**").hasAnyAuthority("PERMISSAO_fi:plano_contas:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/planoContas/**").hasAnyAuthority("PERMISSAO_fi:plano_contas:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/planoContas/**").hasAnyAuthority("PERMISSAO_fi:plano_contas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/planoContas/**").hasAnyAuthority("PERMISSAO_fi:plano_contas:delete");

        auth.requestMatchers(HttpMethod.GET, "/fi/centrosCusto/**").hasAnyAuthority("PERMISSAO_fi:centros_custo:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/centrosCusto/**").hasAnyAuthority("PERMISSAO_fi:centros_custo:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/centrosCusto/**").hasAnyAuthority("PERMISSAO_fi:centros_custo:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/centrosCusto/**").hasAnyAuthority("PERMISSAO_fi:centros_custo:delete");

        auth.requestMatchers(HttpMethod.GET, "/fi/contasPagar/**").hasAnyAuthority("PERMISSAO_fi:contas_pagar:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/contasPagar/**").hasAnyAuthority("PERMISSAO_fi:contas_pagar:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/contasPagar/**").hasAnyAuthority("PERMISSAO_fi:contas_pagar:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/contasPagar/**").hasAnyAuthority("PERMISSAO_fi:contas_pagar:delete");

        auth.requestMatchers(HttpMethod.GET, "/fi/contasReceber/**").hasAnyAuthority("PERMISSAO_fi:contas_receber:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/contasReceber/**").hasAnyAuthority("PERMISSAO_fi:contas_receber:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/contasReceber/**").hasAnyAuthority("PERMISSAO_fi:contas_receber:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/contasReceber/**").hasAnyAuthority("PERMISSAO_fi:contas_receber:delete");

        auth.requestMatchers(HttpMethod.GET, "/fi/movimentacoesBancarias/**").hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/movimentacoesBancarias/**").hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/movimentacoesBancarias/**").hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/movimentacoesBancarias/**").hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:delete");

        auth.requestMatchers(HttpMethod.GET, "/fi/fluxoCaixa/**").hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:read");
        auth.requestMatchers(HttpMethod.POST, "/fi/fluxoCaixa/**").hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:create");
        auth.requestMatchers(HttpMethod.PUT, "/fi/fluxoCaixa/**").hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fi/fluxoCaixa/**").hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:delete");
    }

    private void configurarRh(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/rh/colaboradores/**").hasAnyAuthority("PERMISSAO_rh:colaboradores:read");
        auth.requestMatchers(HttpMethod.POST, "/rh/colaboradores/**").hasAnyAuthority("PERMISSAO_rh:colaboradores:create");
        auth.requestMatchers(HttpMethod.PUT, "/rh/colaboradores/**").hasAnyAuthority("PERMISSAO_rh:colaboradores:update");
        auth.requestMatchers(HttpMethod.DELETE, "/rh/colaboradores/**").hasAnyAuthority("PERMISSAO_rh:colaboradores:delete");

        auth.requestMatchers(HttpMethod.GET, "/rh/dependentes/**").hasAnyAuthority("PERMISSAO_rh:dependentes:read");
        auth.requestMatchers(HttpMethod.POST, "/rh/dependentes/**").hasAnyAuthority("PERMISSAO_rh:dependentes:create");
        auth.requestMatchers(HttpMethod.PUT, "/rh/dependentes/**").hasAnyAuthority("PERMISSAO_rh:dependentes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/rh/dependentes/**").hasAnyAuthority("PERMISSAO_rh:dependentes:delete");

        auth.requestMatchers(HttpMethod.GET, "/rh/folhaDePagamento/**").hasAnyAuthority("PERMISSAO_rh:folha_pagamento:read");
        auth.requestMatchers(HttpMethod.POST, "/rh/folhaDePagamento/**").hasAnyAuthority("PERMISSAO_rh:folha_pagamento:create");
        auth.requestMatchers(HttpMethod.PUT, "/rh/folhaDePagamento/**").hasAnyAuthority("PERMISSAO_rh:folha_pagamento:update");
        auth.requestMatchers(HttpMethod.DELETE, "/rh/folhaDePagamento/**").hasAnyAuthority("PERMISSAO_rh:folha_pagamento:delete");

        auth.requestMatchers(HttpMethod.GET, "/rh/beneficios/**").hasAnyAuthority("PERMISSAO_rh:beneficios:read");
        auth.requestMatchers(HttpMethod.POST, "/rh/beneficios/**").hasAnyAuthority("PERMISSAO_rh:beneficios:create");
        auth.requestMatchers(HttpMethod.PUT, "/rh/beneficios/**").hasAnyAuthority("PERMISSAO_rh:beneficios:update");
        auth.requestMatchers(HttpMethod.DELETE, "/rh/beneficios/**").hasAnyAuthority("PERMISSAO_rh:beneficios:delete");

        auth.requestMatchers(HttpMethod.GET, "/rh/controleDePonto/**").hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:read");
        auth.requestMatchers(HttpMethod.POST, "/rh/controleDePonto/**").hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:create");
        auth.requestMatchers(HttpMethod.PUT, "/rh/controleDePonto/**").hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:update");
        auth.requestMatchers(HttpMethod.DELETE, "/rh/controleDePonto/**").hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:delete");
    }

    private void configurarGrc(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/grc/riscos/**").hasAnyAuthority("PERMISSAO_grc:riscos:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/riscos/**").hasAnyAuthority("PERMISSAO_grc:riscos:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/riscos/**").hasAnyAuthority("PERMISSAO_grc:riscos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/riscos/**").hasAnyAuthority("PERMISSAO_grc:riscos:delete");

        auth.requestMatchers(HttpMethod.GET, "/grc/controles/**").hasAnyAuthority("PERMISSAO_grc:controles:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/controles/**").hasAnyAuthority("PERMISSAO_grc:controles:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/controles/**").hasAnyAuthority("PERMISSAO_grc:controles:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/controles/**").hasAnyAuthority("PERMISSAO_grc:controles:delete");

        auth.requestMatchers(HttpMethod.GET, "/grc/auditorias/**").hasAnyAuthority("PERMISSAO_grc:auditorias:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/auditorias/**").hasAnyAuthority("PERMISSAO_grc:auditorias:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/auditorias/**").hasAnyAuthority("PERMISSAO_grc:auditorias:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/auditorias/**").hasAnyAuthority("PERMISSAO_grc:auditorias:delete");

        auth.requestMatchers(HttpMethod.GET, "/grc/consentimentos/**").hasAnyAuthority("PERMISSAO_grc:consentimentos:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/consentimentos/**").hasAnyAuthority("PERMISSAO_grc:consentimentos:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/consentimentos/**").hasAnyAuthority("PERMISSAO_grc:consentimentos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/consentimentos/**").hasAnyAuthority("PERMISSAO_grc:consentimentos:delete");

        auth.requestMatchers(HttpMethod.GET, "/grc/registrosTratamento/**").hasAnyAuthority("PERMISSAO_grc:registrosTratamento:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/registrosTratamento/**").hasAnyAuthority("PERMISSAO_grc:registrosTratamento:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/registrosTratamento/**").hasAnyAuthority("PERMISSAO_grc:registrosTratamento:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/registrosTratamento/**").hasAnyAuthority("PERMISSAO_grc:registrosTratamento:delete");

        auth.requestMatchers(HttpMethod.GET, "/grc/solicitacoesTitular/**").hasAnyAuthority("PERMISSAO_grc:solicitacoesTitular:read");
        auth.requestMatchers(HttpMethod.POST, "/grc/solicitacoesTitular/**").hasAnyAuthority("PERMISSAO_grc:solicitacoesTitular:create");
        auth.requestMatchers(HttpMethod.PUT, "/grc/solicitacoesTitular/**").hasAnyAuthority("PERMISSAO_grc:solicitacoesTitular:update");
        auth.requestMatchers(HttpMethod.DELETE, "/grc/solicitacoesTitular/**").hasAnyAuthority("PERMISSAO_grc:solicitacoesTitular:delete");
    }

    private void configurarPortal(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/portal/sessoes/**").hasAnyAuthority("PERMISSAO_portal:sessoes:read");
        auth.requestMatchers(HttpMethod.POST, "/portal/sessoes/**").hasAnyAuthority("PERMISSAO_portal:sessoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/portal/sessoes/**").hasAnyAuthority("PERMISSAO_portal:sessoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/portal/sessoes/**").hasAnyAuthority("PERMISSAO_portal:sessoes:delete");

        auth.requestMatchers(HttpMethod.GET, "/portal/notificacoes/**").hasAnyAuthority("PERMISSAO_portal:notificacoes:read");
        auth.requestMatchers(HttpMethod.POST, "/portal/notificacoes/**").hasAnyAuthority("PERMISSAO_portal:notificacoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/portal/notificacoes/**").hasAnyAuthority("PERMISSAO_portal:notificacoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/portal/notificacoes/**").hasAnyAuthority("PERMISSAO_portal:notificacoes:delete");

        auth.requestMatchers(HttpMethod.GET, "/portal/dispositivos/**").hasAnyAuthority("PERMISSAO_portal:dispositivos:read");
        auth.requestMatchers(HttpMethod.POST, "/portal/dispositivos/**").hasAnyAuthority("PERMISSAO_portal:dispositivos:create");
        auth.requestMatchers(HttpMethod.PUT, "/portal/dispositivos/**").hasAnyAuthority("PERMISSAO_portal:dispositivos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/portal/dispositivos/**").hasAnyAuthority("PERMISSAO_portal:dispositivos:delete");
    }

    private void configurarBi(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/bi/metricas/**").hasAnyAuthority("PERMISSAO_bi:metricas:read");
        auth.requestMatchers(HttpMethod.POST, "/bi/metricas/**").hasAnyAuthority("PERMISSAO_bi:metricas:create");
        auth.requestMatchers(HttpMethod.PUT, "/bi/metricas/**").hasAnyAuthority("PERMISSAO_bi:metricas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/bi/metricas/**").hasAnyAuthority("PERMISSAO_bi:metricas:delete");

        auth.requestMatchers(HttpMethod.GET, "/bi/historicoMetricas/**").hasAnyAuthority("PERMISSAO_bi:historico_metricas:read");
        auth.requestMatchers(HttpMethod.POST, "/bi/historicoMetricas/**").hasAnyAuthority("PERMISSAO_bi:historico_metricas:create");
        auth.requestMatchers(HttpMethod.PUT, "/bi/historicoMetricas/**").hasAnyAuthority("PERMISSAO_bi:historico_metricas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/bi/historicoMetricas/**").hasAnyAuthority("PERMISSAO_bi:historico_metricas:delete");

        auth.requestMatchers(HttpMethod.GET, "/bi/dashboards/**").hasAnyAuthority("PERMISSAO_bi:dashboards:read");
        auth.requestMatchers(HttpMethod.POST, "/bi/dashboards/**").hasAnyAuthority("PERMISSAO_bi:dashboards:create");
        auth.requestMatchers(HttpMethod.PUT, "/bi/dashboards/**").hasAnyAuthority("PERMISSAO_bi:dashboards:update");
        auth.requestMatchers(HttpMethod.DELETE, "/bi/dashboards/**").hasAnyAuthority("PERMISSAO_bi:dashboards:delete");

        auth.requestMatchers(HttpMethod.GET, "/bi/relatorios/**").hasAnyAuthority("PERMISSAO_bi:relatorios:read");
        auth.requestMatchers(HttpMethod.POST, "/bi/relatorios/**").hasAnyAuthority("PERMISSAO_bi:relatorios:create");
        auth.requestMatchers(HttpMethod.PUT, "/bi/relatorios/**").hasAnyAuthority("PERMISSAO_bi:relatorios:update");
        auth.requestMatchers(HttpMethod.DELETE, "/bi/relatorios/**").hasAnyAuthority("PERMISSAO_bi:relatorios:delete");
    }

    private void configurarMm(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/mm/compras/**").hasAnyAuthority("PERMISSAO_mm:compras:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/compras/**").hasAnyAuthority("PERMISSAO_mm:compras:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/compras/**").hasAnyAuthority("PERMISSAO_mm:compras:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/compras/**").hasAnyAuthority("PERMISSAO_mm:compras:delete");

        auth.requestMatchers(HttpMethod.GET, "/mm/compraItens/**").hasAnyAuthority("PERMISSAO_mm:compraItens:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/compraItens/**").hasAnyAuthority("PERMISSAO_mm:compraItens:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/compraItens/**").hasAnyAuthority("PERMISSAO_mm:compraItens:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/compraItens/**").hasAnyAuthority("PERMISSAO_mm:compraItens:delete");

        auth.requestMatchers(HttpMethod.GET, "/mm/materiais/**").hasAnyAuthority("PERMISSAO_mm:materiais:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/materiais/**").hasAnyAuthority("PERMISSAO_mm:materiais:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/materiais/**").hasAnyAuthority("PERMISSAO_mm:materiais:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/materiais/**").hasAnyAuthority("PERMISSAO_mm:materiais:delete");

        auth.requestMatchers(HttpMethod.GET, "/mm/inventarios/**").hasAnyAuthority("PERMISSAO_mm:inventarios:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/inventarios/**").hasAnyAuthority("PERMISSAO_mm:inventarios:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/inventarios/**").hasAnyAuthority("PERMISSAO_mm:inventarios:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/inventarios/**").hasAnyAuthority("PERMISSAO_mm:inventarios:delete");

        auth.requestMatchers(HttpMethod.GET, "/mm/estoques/**").hasAnyAuthority("PERMISSAO_mm:estoques:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/estoques/**").hasAnyAuthority("PERMISSAO_mm:estoques:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/estoques/**").hasAnyAuthority("PERMISSAO_mm:estoques:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/estoques/**").hasAnyAuthority("PERMISSAO_mm:estoques:delete");

        auth.requestMatchers(HttpMethod.GET, "/mm/movimentacoes/**").hasAnyAuthority("PERMISSAO_mm:movimentacoes:read");
        auth.requestMatchers(HttpMethod.POST, "/mm/movimentacoes/**").hasAnyAuthority("PERMISSAO_mm:movimentacoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/mm/movimentacoes/**").hasAnyAuthority("PERMISSAO_mm:movimentacoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/mm/movimentacoes/**").hasAnyAuthority("PERMISSAO_mm:movimentacoes:delete");

    }

    private void configurarSd(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:delete");

        auth.requestMatchers(HttpMethod.GET, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:delete");

        auth.requestMatchers(HttpMethod.GET, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:delete");

        auth.requestMatchers(HttpMethod.GET, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:delete");

        auth.requestMatchers(HttpMethod.GET, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:delete");

        auth.requestMatchers(HttpMethod.GET, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:read");
        auth.requestMatchers(HttpMethod.POST, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:create");
        auth.requestMatchers(HttpMethod.PUT, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:delete");
    }

    private void configurarPp(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/pp/bom/**").hasAnyAuthority("PERMISSAO_pp:bom:read");
        auth.requestMatchers(HttpMethod.POST, "/pp/bom/**").hasAnyAuthority("PERMISSAO_pp:bom:create");
        auth.requestMatchers(HttpMethod.PUT, "/pp/bom/**").hasAnyAuthority("PERMISSAO_pp:bom:update");
        auth.requestMatchers(HttpMethod.DELETE, "/pp/bom/**").hasAnyAuthority("PERMISSAO_pp:bom:delete");

        auth.requestMatchers(HttpMethod.GET, "/pp/ordemProducao/**").hasAnyAuthority("PERMISSAO_pp:ordemProducao:read");
        auth.requestMatchers(HttpMethod.POST, "/pp/ordemProducao/**").hasAnyAuthority("PERMISSAO_pp:ordemProducao:create");
        auth.requestMatchers(HttpMethod.PUT, "/pp/ordemProducao/**").hasAnyAuthority("PERMISSAO_pp:ordemProducao:update");
        auth.requestMatchers(HttpMethod.DELETE, "/pp/ordemProducao/**").hasAnyAuthority("PERMISSAO_pp:ordemProducao:delete");

        auth.requestMatchers(HttpMethod.GET, "/pp/apontamentos/**").hasAnyAuthority("PERMISSAO_pp:apontamentos:read");
        auth.requestMatchers(HttpMethod.POST, "/pp/apontamentos/**").hasAnyAuthority("PERMISSAO_pp:apontamentos:create");
        auth.requestMatchers(HttpMethod.PUT, "/pp/apontamentos/**").hasAnyAuthority("PERMISSAO_pp:apontamentos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/pp/apontamentos/**").hasAnyAuthority("PERMISSAO_pp:apontamentos:delete");

        auth.requestMatchers(HttpMethod.GET, "/pp/mrp/**").hasAnyAuthority("PERMISSAO_pp:mrp:read");
        auth.requestMatchers(HttpMethod.POST, "/pp/mrp/**").hasAnyAuthority("PERMISSAO_pp:mrp:create");
        auth.requestMatchers(HttpMethod.PUT, "/pp/mrp/**").hasAnyAuthority("PERMISSAO_pp:mrp:update");
        auth.requestMatchers(HttpMethod.DELETE, "/pp/mrp/**").hasAnyAuthority("PERMISSAO_pp:mrp:delete");
    }

    private void configurarSm(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/sm/ordensServico/**").hasAnyAuthority("PERMISSAO_sm:ordensServico:read");
        auth.requestMatchers(HttpMethod.POST, "/sm/ordensServico/**").hasAnyAuthority("PERMISSAO_sm:ordensServico:create");
        auth.requestMatchers(HttpMethod.PUT, "/sm/ordensServico/**").hasAnyAuthority("PERMISSAO_sm:ordensServico:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sm/ordensServico/**").hasAnyAuthority("PERMISSAO_sm:ordensServico:delete");

        auth.requestMatchers(HttpMethod.GET, "/sm/atendimentos/**").hasAnyAuthority("PERMISSAO_sm:atendimentos:read");
        auth.requestMatchers(HttpMethod.POST, "/sm/atendimentos/**").hasAnyAuthority("PERMISSAO_sm:atendimentos:create");
        auth.requestMatchers(HttpMethod.PUT, "/sm/atendimentos/**").hasAnyAuthority("PERMISSAO_sm:atendimentos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sm/atendimentos/**").hasAnyAuthority("PERMISSAO_sm:atendimentos:delete");

        auth.requestMatchers(HttpMethod.GET, "/sm/slaConfig/**").hasAnyAuthority("PERMISSAO_sm:slaConfig:read");
        auth.requestMatchers(HttpMethod.POST, "/sm/slaConfig/**").hasAnyAuthority("PERMISSAO_sm:slaConfig:create");
        auth.requestMatchers(HttpMethod.PUT, "/sm/slaConfig/**").hasAnyAuthority("PERMISSAO_sm:slaConfig:update");
        auth.requestMatchers(HttpMethod.DELETE, "/sm/slaConfig/**").hasAnyAuthority("PERMISSAO_sm:slaConfig:delete");
    }

    private void configurarQm(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/qm/inspecoes/**").hasAnyAuthority("PERMISSAO_qm:inspecoes:read");
        auth.requestMatchers(HttpMethod.POST, "/qm/inspecoes/**").hasAnyAuthority("PERMISSAO_qm:inspecoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/qm/inspecoes/**").hasAnyAuthority("PERMISSAO_qm:inspecoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/qm/inspecoes/**").hasAnyAuthority("PERMISSAO_qm:inspecoes:delete");

        auth.requestMatchers(HttpMethod.GET, "/qm/naoConformidade/**").hasAnyAuthority("PERMISSAO_qm:naoConformidade:read");
        auth.requestMatchers(HttpMethod.POST, "/qm/naoConformidade/**").hasAnyAuthority("PERMISSAO_qm:naoConformidade:create");
        auth.requestMatchers(HttpMethod.PUT, "/qm/naoConformidade/**").hasAnyAuthority("PERMISSAO_qm:naoConformidade:update");
        auth.requestMatchers(HttpMethod.DELETE, "/qm/naoConformidade/**").hasAnyAuthority("PERMISSAO_qm:naoConformidade:delete");

    }

    private void configurarPs(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/ps/projetos/**").hasAnyAuthority("PERMISSAO_ps:projetos:read");
        auth.requestMatchers(HttpMethod.POST, "/ps/projetos/**").hasAnyAuthority("PERMISSAO_ps:projetos:create");
        auth.requestMatchers(HttpMethod.PUT, "/ps/projetos/**").hasAnyAuthority("PERMISSAO_ps:projetos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/ps/projetos/**").hasAnyAuthority("PERMISSAO_ps:projetos:delete");

        auth.requestMatchers(HttpMethod.GET, "/ps/tarefas/**").hasAnyAuthority("PERMISSAO_ps:tarefas:read");
        auth.requestMatchers(HttpMethod.POST, "/ps/tarefas/**").hasAnyAuthority("PERMISSAO_ps:tarefas:create");
        auth.requestMatchers(HttpMethod.PUT, "/ps/tarefas/**").hasAnyAuthority("PERMISSAO_ps:tarefas:update");
        auth.requestMatchers(HttpMethod.DELETE, "/ps/tarefas/**").hasAnyAuthority("PERMISSAO_ps:tarefas:delete");

        auth.requestMatchers(HttpMethod.GET, "/ps/recursosAlocados/**").hasAnyAuthority("PERMISSAO_ps:recursosAlocados:read");
        auth.requestMatchers(HttpMethod.POST, "/ps/recursosAlocados/**").hasAnyAuthority("PERMISSAO_ps:recursosAlocados:create");
        auth.requestMatchers(HttpMethod.PUT, "/ps/recursosAlocados/**").hasAnyAuthority("PERMISSAO_ps:recursosAlocados:update");
        auth.requestMatchers(HttpMethod.DELETE, "/ps/recursosAlocados/**").hasAnyAuthority("PERMISSAO_ps:recursosAlocados:delete");
    }

    private void configurarAm(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/am/bensPatrimoniais/**").hasAnyAuthority("PERMISSAO_am:bensPatrimoniais:read");
        auth.requestMatchers(HttpMethod.POST, "/am/bensPatrimoniais/**").hasAnyAuthority("PERMISSAO_am:bensPatrimoniais:create");
        auth.requestMatchers(HttpMethod.PUT, "/am/bensPatrimoniais/**").hasAnyAuthority("PERMISSAO_am:bensPatrimoniais:update");
        auth.requestMatchers(HttpMethod.DELETE, "/am/bensPatrimoniais/**").hasAnyAuthority("PERMISSAO_am:bensPatrimoniais:delete");

        auth.requestMatchers(HttpMethod.GET, "/am/manutencoes/**").hasAnyAuthority("PERMISSAO_am:manutencoes:read");
        auth.requestMatchers(HttpMethod.POST, "/am/manutencoes/**").hasAnyAuthority("PERMISSAO_am:manutencoes:create");
        auth.requestMatchers(HttpMethod.PUT, "/am/manutencoes/**").hasAnyAuthority("PERMISSAO_am:manutencoes:update");
        auth.requestMatchers(HttpMethod.DELETE, "/am/manutencoes/**").hasAnyAuthority("PERMISSAO_am:manutencoes:delete");

    }

    private void configurarFiscal(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(HttpMethod.GET, "/fiscal/documentos/**").hasAnyAuthority("PERMISSAO_fiscal:documentos:read");
        auth.requestMatchers(HttpMethod.POST, "/fiscal/documentos/**").hasAnyAuthority("PERMISSAO_fiscal:documentos:create");
        auth.requestMatchers(HttpMethod.PUT, "/fiscal/documentos/**").hasAnyAuthority("PERMISSAO_fiscal:documentos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fiscal/documentos/**").hasAnyAuthority("PERMISSAO_fiscal:documentos:delete");

        auth.requestMatchers(HttpMethod.GET, "/fiscal/ecdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:ecdRegistros:read");
        auth.requestMatchers(HttpMethod.POST, "/fiscal/ecdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:ecdRegistros:create");
        auth.requestMatchers(HttpMethod.PUT, "/fiscal/ecdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:ecdRegistros:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fiscal/ecdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:ecdRegistros:delete");

        auth.requestMatchers(HttpMethod.GET, "/fiscal/efdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:efdRegistros:read");
        auth.requestMatchers(HttpMethod.POST, "/fiscal/efdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:efdRegistros:create");
        auth.requestMatchers(HttpMethod.PUT, "/fiscal/efdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:efdRegistros:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fiscal/efdRegistros/**").hasAnyAuthority("PERMISSAO_fiscal:efdRegistros:delete");

        auth.requestMatchers(HttpMethod.GET, "/fiscal/esocialEventos/**").hasAnyAuthority("PERMISSAO_fiscal:esocialEventos:read");
        auth.requestMatchers(HttpMethod.POST, "/fiscal/esocialEventos/**").hasAnyAuthority("PERMISSAO_fiscal:esocialEventos:create");
        auth.requestMatchers(HttpMethod.PUT, "/fiscal/esocialEventos/**").hasAnyAuthority("PERMISSAO_fiscal:esocialEventos:update");
        auth.requestMatchers(HttpMethod.DELETE, "/fiscal/esocialEventos/**").hasAnyAuthority("PERMISSAO_fiscal:esocialEventos:delete");
    }
}
