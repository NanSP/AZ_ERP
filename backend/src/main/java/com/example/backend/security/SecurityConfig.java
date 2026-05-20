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

                        .requestMatchers("/platform/**").hasAnyAuthority("SCOPE_MASTER")

                        .requestMatchers(HttpMethod.GET, "/core/empresas/**")
                        .hasAnyAuthority("PERMISSAO_core:empresas:read")
                        .requestMatchers(HttpMethod.POST, "/core/empresas/**")
                        .hasAnyAuthority("PERMISSAO_core:empresas:create")
                        .requestMatchers(HttpMethod.PUT, "/core/empresas/**")
                        .hasAnyAuthority("PERMISSAO_core:empresas:update")
                        .requestMatchers(HttpMethod.DELETE, "/core/empresas/**")
                        .hasAnyAuthority("PERMISSAO_core:empresas:delete")

                        .requestMatchers(HttpMethod.GET, "/core/enderecos/**")
                        .hasAnyAuthority("PERMISSAO_core:enderecos:read")
                        .requestMatchers(HttpMethod.POST, "/core/enderecos/**")
                        .hasAnyAuthority("PERMISSAO_core:enderecos:create")
                        .requestMatchers(HttpMethod.PUT, "/core/enderecos/**")
                        .hasAnyAuthority("PERMISSAO_core:enderecos:update")
                        .requestMatchers(HttpMethod.DELETE, "/core/enderecos/**")
                        .hasAnyAuthority("PERMISSAO_core:enderecos:delete")

                        .requestMatchers(HttpMethod.GET, "/core/contatos/**")
                        .hasAnyAuthority("PERMISSAO_core:contatos:read")
                        .requestMatchers(HttpMethod.POST, "/core/contatos/**")
                        .hasAnyAuthority("PERMISSAO_core:contatos:create")
                        .requestMatchers(HttpMethod.PUT, "/core/contatos/**")
                        .hasAnyAuthority("PERMISSAO_core:contatos:update")
                        .requestMatchers(HttpMethod.DELETE, "/core/contatos/**")
                        .hasAnyAuthority("PERMISSAO_core:contatos:delete")

                        .requestMatchers(HttpMethod.GET, "/core/parceiros/**")
                        .hasAnyAuthority("PERMISSAO_core:parceiros:read")
                        .requestMatchers(HttpMethod.POST, "/core/parceiros/**")
                        .hasAnyAuthority("PERMISSAO_core:parceiros:create")
                        .requestMatchers(HttpMethod.PUT, "/core/parceiros/**")
                        .hasAnyAuthority("PERMISSAO_core:parceiros:update")
                        .requestMatchers(HttpMethod.DELETE, "/core/parceiros/**")
                        .hasAnyAuthority("PERMISSAO_core:parceiros:delete")

                        .requestMatchers(HttpMethod.GET, "/core/produtos/**")
                        .hasAnyAuthority("PERMISSAO_core:produtos:read")
                        .requestMatchers(HttpMethod.POST, "/core/produtos/**")
                        .hasAnyAuthority("PERMISSAO_core:produtos:create")
                        .requestMatchers(HttpMethod.PUT, "/core/produtos/**")
                        .hasAnyAuthority("PERMISSAO_core:produtos:update")
                        .requestMatchers(HttpMethod.DELETE, "/core/produtos/**")
                        .hasAnyAuthority("PERMISSAO_core:produtos:delete")

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

                        .requestMatchers(HttpMethod.GET, "/fi/planoContas/**")
                        .hasAnyAuthority("PERMISSAO_fi:plano_contas:read")
                        .requestMatchers(HttpMethod.POST, "/fi/planoContas/**")
                        .hasAnyAuthority("PERMISSAO_fi:plano_contas:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/planoContas/**")
                        .hasAnyAuthority("PERMISSAO_fi:plano_contas:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/planoContas/**")
                        .hasAnyAuthority("PERMISSAO_fi:plano_contas:delete")

                        .requestMatchers(HttpMethod.GET, "/fi/centrosCusto/**")
                        .hasAnyAuthority("PERMISSAO_fi:centros_custo:read")
                        .requestMatchers(HttpMethod.POST, "/fi/centrosCusto/**")
                        .hasAnyAuthority("PERMISSAO_fi:centros_custo:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/centrosCusto/**")
                        .hasAnyAuthority("PERMISSAO_fi:centros_custo:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/centrosCusto/**")
                        .hasAnyAuthority("PERMISSAO_fi:centros_custo:delete")

                        .requestMatchers(HttpMethod.GET, "/fi/contasPagar/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_pagar:read")
                        .requestMatchers(HttpMethod.POST, "/fi/contasPagar/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_pagar:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/contasPagar/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_pagar:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/contasPagar/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_pagar:delete")

                        .requestMatchers(HttpMethod.GET, "/fi/contasReceber/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_receber:read")
                        .requestMatchers(HttpMethod.POST, "/fi/contasReceber/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_receber:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/contasReceber/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_receber:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/contasReceber/**")
                        .hasAnyAuthority("PERMISSAO_fi:contas_receber:delete")

                        .requestMatchers(HttpMethod.GET, "/fi/movimentacoesBancarias/**")
                        .hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:read")
                        .requestMatchers(HttpMethod.POST, "/fi/movimentacoesBancarias/**")
                        .hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/movimentacoesBancarias/**")
                        .hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/movimentacoesBancarias/**")
                        .hasAnyAuthority("PERMISSAO_fi:movimentacoes_bancarias:delete")

                        .requestMatchers(HttpMethod.GET, "/fi/fluxoCaixa/**")
                        .hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:read")
                        .requestMatchers(HttpMethod.POST, "/fi/fluxoCaixa/**")
                        .hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:create")
                        .requestMatchers(HttpMethod.PUT, "/fi/fluxoCaixa/**")
                        .hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:update")
                        .requestMatchers(HttpMethod.DELETE, "/fi/fluxoCaixa/**")
                        .hasAnyAuthority("PERMISSAO_fi:fluxo_caixa:delete")

                        .requestMatchers(HttpMethod.GET, "/rh/colaboradores/**")
                        .hasAnyAuthority("PERMISSAO_rh:colaboradores:read")
                        .requestMatchers(HttpMethod.POST, "/rh/colaboradores/**")
                        .hasAnyAuthority("PERMISSAO_rh:colaboradores:create")
                        .requestMatchers(HttpMethod.PUT, "/rh/colaboradores/**")
                        .hasAnyAuthority("PERMISSAO_rh:colaboradores:update")
                        .requestMatchers(HttpMethod.DELETE, "/rh/colaboradores/**")
                        .hasAnyAuthority("PERMISSAO_rh:colaboradores:delete")

                        .requestMatchers(HttpMethod.GET, "/rh/dependentes/**")
                        .hasAnyAuthority("PERMISSAO_rh:dependentes:read")
                        .requestMatchers(HttpMethod.POST, "/rh/dependentes/**")
                        .hasAnyAuthority("PERMISSAO_rh:dependentes:create")
                        .requestMatchers(HttpMethod.PUT, "/rh/dependentes/**")
                        .hasAnyAuthority("PERMISSAO_rh:dependentes:update")
                        .requestMatchers(HttpMethod.DELETE, "/rh/dependentes/**")
                        .hasAnyAuthority("PERMISSAO_rh:dependentes:delete")

                        .requestMatchers(HttpMethod.GET, "/rh/folhaDePagamento/**")
                        .hasAnyAuthority("PERMISSAO_rh:folha_pagamento:read")
                        .requestMatchers(HttpMethod.POST, "/rh/folhaDePagamento/**")
                        .hasAnyAuthority("PERMISSAO_rh:folha_pagamento:create")
                        .requestMatchers(HttpMethod.PUT, "/rh/folhaDePagamento/**")
                        .hasAnyAuthority("PERMISSAO_rh:folha_pagamento:update")
                        .requestMatchers(HttpMethod.DELETE, "/rh/folhaDePagamento/**")
                        .hasAnyAuthority("PERMISSAO_rh:folha_pagamento:delete")

                        .requestMatchers(HttpMethod.GET, "/rh/beneficios/**")
                        .hasAnyAuthority("PERMISSAO_rh:beneficios:read")
                        .requestMatchers(HttpMethod.POST, "/rh/beneficios/**")
                        .hasAnyAuthority("PERMISSAO_rh:beneficios:create")
                        .requestMatchers(HttpMethod.PUT, "/rh/beneficios/**")
                        .hasAnyAuthority("PERMISSAO_rh:beneficios:update")
                        .requestMatchers(HttpMethod.DELETE, "/rh/beneficios/**")
                        .hasAnyAuthority("PERMISSAO_rh:beneficios:delete")

                        .requestMatchers(HttpMethod.GET, "/rh/controleDePonto/**")
                        .hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:read")
                        .requestMatchers(HttpMethod.POST, "/rh/controleDePonto/**")
                        .hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:create")
                        .requestMatchers(HttpMethod.PUT, "/rh/controleDePonto/**")
                        .hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:update")
                        .requestMatchers(HttpMethod.DELETE, "/rh/controleDePonto/**")
                        .hasAnyAuthority("PERMISSAO_rh:controle_de_ponto:delete")

                        .requestMatchers(HttpMethod.GET, "/grc/riscos/**")
                        .hasAnyAuthority("PERMISSAO_grc:riscos:read")
                        .requestMatchers(HttpMethod.POST, "/grc/riscos/**")
                        .hasAnyAuthority("PERMISSAO_grc:riscos:create")
                        .requestMatchers(HttpMethod.PUT, "/grc/riscos/**")
                        .hasAnyAuthority("PERMISSAO_grc:riscos:update")
                        .requestMatchers(HttpMethod.DELETE, "/grc/riscos/**")
                        .hasAnyAuthority("PERMISSAO_grc:riscos:delete")

                        .requestMatchers(HttpMethod.GET, "/grc/controles/**")
                        .hasAnyAuthority("PERMISSAO_grc:controles:read")
                        .requestMatchers(HttpMethod.POST, "/grc/controles/**")
                        .hasAnyAuthority("PERMISSAO_grc:controles:create")
                        .requestMatchers(HttpMethod.PUT, "/grc/controles/**")
                        .hasAnyAuthority("PERMISSAO_grc:controles:update")
                        .requestMatchers(HttpMethod.DELETE, "/grc/controles/**")
                        .hasAnyAuthority("PERMISSAO_grc:controles:delete")

                        .requestMatchers(HttpMethod.GET, "/grc/auditorias/**")
                        .hasAnyAuthority("PERMISSAO_grc:auditorias:read")
                        .requestMatchers(HttpMethod.POST, "/grc/auditorias/**")
                        .hasAnyAuthority("PERMISSAO_grc:auditorias:create")
                        .requestMatchers(HttpMethod.PUT, "/grc/auditorias/**")
                        .hasAnyAuthority("PERMISSAO_grc:auditorias:update")
                        .requestMatchers(HttpMethod.DELETE, "/grc/auditorias/**")
                        .hasAnyAuthority("PERMISSAO_grc:auditorias:delete")

                        .requestMatchers(HttpMethod.GET, "/grc/consentimentos/**")
                        .hasAnyAuthority("PERMISSAO_grc:consentimentos:read")
                        .requestMatchers(HttpMethod.POST, "/grc/consentimentos/**")
                        .hasAnyAuthority("PERMISSAO_grc:consentimentos:create")
                        .requestMatchers(HttpMethod.PUT, "/grc/consentimentos/**")
                        .hasAnyAuthority("PERMISSAO_grc:consentimentos:update")
                        .requestMatchers(HttpMethod.DELETE, "/grc/consentimentos/**")
                        .hasAnyAuthority("PERMISSAO_grc:consentimentos:delete")

                        .requestMatchers(HttpMethod.GET, "/portal/sessoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:sessoes:read")
                        .requestMatchers(HttpMethod.POST, "/portal/sessoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:sessoes:create")
                        .requestMatchers(HttpMethod.PUT, "/portal/sessoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:sessoes:update")
                        .requestMatchers(HttpMethod.DELETE, "/portal/sessoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:sessoes:delete")

                        .requestMatchers(HttpMethod.GET, "/portal/notificacoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:notificacoes:read")
                        .requestMatchers(HttpMethod.POST, "/portal/notificacoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:notificacoes:create")
                        .requestMatchers(HttpMethod.PUT, "/portal/notificacoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:notificacoes:update")
                        .requestMatchers(HttpMethod.DELETE, "/portal/notificacoes/**")
                        .hasAnyAuthority("PERMISSAO_portal:notificacoes:delete")

                        .requestMatchers(HttpMethod.GET, "/portal/dispositivos/**")
                        .hasAnyAuthority("PERMISSAO_portal:dispositivos:read")
                        .requestMatchers(HttpMethod.POST, "/portal/dispositivos/**")
                        .hasAnyAuthority("PERMISSAO_portal:dispositivos:create")
                        .requestMatchers(HttpMethod.PUT, "/portal/dispositivos/**")
                        .hasAnyAuthority("PERMISSAO_portal:dispositivos:update")
                        .requestMatchers(HttpMethod.DELETE, "/portal/dispositivos/**")
                        .hasAnyAuthority("PERMISSAO_portal:dispositivos:delete")

                        .requestMatchers(HttpMethod.GET, "/bi/metricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:metricas:read")
                        .requestMatchers(HttpMethod.POST, "/bi/metricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:metricas:create")
                        .requestMatchers(HttpMethod.PUT, "/bi/metricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:metricas:update")
                        .requestMatchers(HttpMethod.DELETE, "/bi/metricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:metricas:delete")

                        .requestMatchers(HttpMethod.GET, "/bi/historicoMetricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:historico_metricas:read")
                        .requestMatchers(HttpMethod.POST, "/bi/historicoMetricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:historico_metricas:create")
                        .requestMatchers(HttpMethod.PUT, "/bi/historicoMetricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:historico_metricas:update")
                        .requestMatchers(HttpMethod.DELETE, "/bi/historicoMetricas/**")
                        .hasAnyAuthority("PERMISSAO_bi:historico_metricas:delete")

                        .requestMatchers(HttpMethod.GET, "/bi/dashboards/**")
                        .hasAnyAuthority("PERMISSAO_bi:dashboards:read")
                        .requestMatchers(HttpMethod.POST, "/bi/dashboards/**")
                        .hasAnyAuthority("PERMISSAO_bi:dashboards:create")
                        .requestMatchers(HttpMethod.PUT, "/bi/dashboards/**")
                        .hasAnyAuthority("PERMISSAO_bi:dashboards:update")
                        .requestMatchers(HttpMethod.DELETE, "/bi/dashboards/**")
                        .hasAnyAuthority("PERMISSAO_bi:dashboards:delete")

                        .requestMatchers(HttpMethod.GET, "/bi/relatorios/**")
                        .hasAnyAuthority("PERMISSAO_bi:relatorios:read")
                        .requestMatchers(HttpMethod.POST, "/bi/relatorios/**")
                        .hasAnyAuthority("PERMISSAO_bi:relatorios:create")
                        .requestMatchers(HttpMethod.PUT, "/bi/relatorios/**")
                        .hasAnyAuthority("PERMISSAO_bi:relatorios:update")
                        .requestMatchers(HttpMethod.DELETE, "/bi/relatorios/**")
                        .hasAnyAuthority("PERMISSAO_bi:relatorios:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/compras/**")
                        .hasAnyAuthority("PERMISSAO_mm:compras:read")
                        .requestMatchers(HttpMethod.POST, "/mm/compras/**")
                        .hasAnyAuthority("PERMISSAO_mm:compras:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/compras/**")
                        .hasAnyAuthority("PERMISSAO_mm:compras:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/compras/**")
                        .hasAnyAuthority("PERMISSAO_mm:compras:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/compraItens/**")
                        .hasAnyAuthority("PERMISSAO_mm:compraItens:read")
                        .requestMatchers(HttpMethod.POST, "/mm/compraItens/**")
                        .hasAnyAuthority("PERMISSAO_mm:compraItens:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/compraItens/**")
                        .hasAnyAuthority("PERMISSAO_mm:compraItens:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/compraItens/**")
                        .hasAnyAuthority("PERMISSAO_mm:compraItens:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/materiais/**")
                        .hasAnyAuthority("PERMISSAO_mm:materiais:read")
                        .requestMatchers(HttpMethod.POST, "/mm/materiais/**")
                        .hasAnyAuthority("PERMISSAO_mm:materiais:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/materiais/**")
                        .hasAnyAuthority("PERMISSAO_mm:materiais:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/materiais/**")
                        .hasAnyAuthority("PERMISSAO_mm:materiais:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/inventarios/**")
                        .hasAnyAuthority("PERMISSAO_mm:inventarios:read")
                        .requestMatchers(HttpMethod.POST, "/mm/inventarios/**")
                        .hasAnyAuthority("PERMISSAO_mm:inventarios:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/inventarios/**")
                        .hasAnyAuthority("PERMISSAO_mm:inventarios:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/inventarios/**")
                        .hasAnyAuthority("PERMISSAO_mm:inventarios:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/estoques/**")
                        .hasAnyAuthority("PERMISSAO_mm:estoques:read")
                        .requestMatchers(HttpMethod.POST, "/mm/estoques/**")
                        .hasAnyAuthority("PERMISSAO_mm:estoques:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/estoques/**")
                        .hasAnyAuthority("PERMISSAO_mm:estoques:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/estoques/**")
                        .hasAnyAuthority("PERMISSAO_mm:estoques:delete")

                        .requestMatchers(HttpMethod.GET, "/mm/movimentacoes/**")
                        .hasAnyAuthority("PERMISSAO_mm:movimentacoes:read")
                        .requestMatchers(HttpMethod.POST, "/mm/movimentacoes/**")
                        .hasAnyAuthority("PERMISSAO_mm:movimentacoes:create")
                        .requestMatchers(HttpMethod.PUT, "/mm/movimentacoes/**")
                        .hasAnyAuthority("PERMISSAO_mm:movimentacoes:update")
                        .requestMatchers(HttpMethod.DELETE, "/mm/movimentacoes/**")
                        .hasAnyAuthority("PERMISSAO_mm:movimentacoes:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:read")
                        .requestMatchers(HttpMethod.POST, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/clientes/**").hasAnyAuthority("PERMISSAO_sd:clientes:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:read")
                        .requestMatchers(HttpMethod.POST, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/oportunidades/**").hasAnyAuthority("PERMISSAO_sd:oportunidades:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:read")
                        .requestMatchers(HttpMethod.POST, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/pedidos/**").hasAnyAuthority("PERMISSAO_sd:pedidos:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:read")
                        .requestMatchers(HttpMethod.POST, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/pedidoItens/**").hasAnyAuthority("PERMISSAO_sd:pedidoItens:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:read")
                        .requestMatchers(HttpMethod.POST, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/faturas/**").hasAnyAuthority("PERMISSAO_sd:faturas:delete")

                        .requestMatchers(HttpMethod.GET, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:read")
                        .requestMatchers(HttpMethod.POST, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:create")
                        .requestMatchers(HttpMethod.PUT, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:update")
                        .requestMatchers(HttpMethod.DELETE, "/sd/contratos/**").hasAnyAuthority("PERMISSAO_sd:contratos:delete")

                        .requestMatchers("/pp/**").hasAnyAuthority("SCOPE_TENANT")
                        .requestMatchers("/sm/**").hasAnyAuthority("SCOPE_TENANT")
                        .requestMatchers("/qm/**").hasAnyAuthority("SCOPE_TENANT")
                        .requestMatchers("/ps/**").hasAnyAuthority("SCOPE_TENANT")
                        .requestMatchers("/am/**").hasAnyAuthority("SCOPE_TENANT")
                        .requestMatchers("/fiscal/**").hasAnyAuthority("SCOPE_TENANT")

                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
