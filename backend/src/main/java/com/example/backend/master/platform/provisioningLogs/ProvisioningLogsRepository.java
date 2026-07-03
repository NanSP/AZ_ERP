package com.example.backend.master.platform.provisioningLogs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ProvisioningLogsRepository extends JpaRepository<ProvisioningLogs, Long> {
    boolean existsByTenantIdId(Long tenantId);
    boolean existsByExecutadoPorId(Long executadoPorId);
    long deleteByCreatedAtBefore(LocalDateTime createdAt);
}
