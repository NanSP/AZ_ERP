package com.example.backend.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUserPrincipal {

    private final Long userId;
    private final String login;
    private final String role;
    private final String scope;
    private final Long tenantId;
    private final String tenantCode;
    private final List<String> perfis;
    private final List<String> permissoes;

    public SecurityUserPrincipal(
            Long userId,
            String login,
            String role,
            String scope,
            Long tenantId,
            String tenantCode,
            List<String> perfis,
            List<String> permissoes
    ) {
        this.userId = userId;
        this.login = login;
        this.role = role;
        this.scope = scope;
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.perfis = perfis != null ? perfis : List.of();
        this.permissoes = permissoes != null ? permissoes : List.of();
    }

    public Long getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getRole() {
        return role;
    }

    public String getScope() {
        return scope;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public List<String> getPerfis() {
        return perfis;
    }

    public List<String> getPermissoes() {
        return permissoes;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.toUpperCase()));

        for (String perfil : perfis) {
            authorities.add(new SimpleGrantedAuthority("PERFIL_" + perfil));
        }

        for (String permissao : permissoes) {
            authorities.add(new SimpleGrantedAuthority("PERMISSAO_" + permissao));
        }

        return authorities;
    }
}
