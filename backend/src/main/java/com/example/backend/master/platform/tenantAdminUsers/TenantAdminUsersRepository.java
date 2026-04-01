package com.example.backend.master.platform.tenantAdminUsers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantAdminUsersRepository extends JpaRepository<TenantAdminUsers, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByLoginIgnoreCase(String login);
}
