package com.example.backend.master.platform.tenantAdminUsers;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantAdminUsersRepository extends JpaRepository<TenantAdminUsers, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByLoginIgnoreCase(String login);
    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);
    boolean existsByTenantIdId(Long tenantId);
}
