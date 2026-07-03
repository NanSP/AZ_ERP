package com.example.backend.auditoria.logAcoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LogAcoesRepository extends JpaRepository<LogAcoes, Long> {
    long deleteByCreatedAtBefore(LocalDateTime createdAt);
}
