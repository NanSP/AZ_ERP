package com.example.backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUserPrincipal {

    private final Long userId;
    private final String login;
    private final String role;
    private final String scope;

    public SecurityUserPrincipal(Long userId, String login, String role, String scope) {
        this.userId = userId;
        this.login = login;
        this.role = role;
        this.scope = scope;
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
