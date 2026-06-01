package com.example.backend.master.platform.templateMigration;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateRegistryRepository extends JpaRepository<TemplateRegistry, Long> {

    Optional<TemplateRegistry> findByDatabaseName(String databaseName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select tr from TemplateRegistry tr where tr.databaseName = :databaseName")
    Optional<TemplateRegistry> findByDatabaseNameForUpdate(@Param("databaseName") String databaseName);
}
