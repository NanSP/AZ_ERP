package com.example.backend.auditoria.logErros;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LogErrosRepository extends JpaRepository<LogErros, Long> {
    boolean existsByModuloAndErroMensagemAndUrlAndCreatedAtAfter(
            String modulo,
            String erroMensagem,
            String url,
            LocalDateTime createdAt
    );

    long deleteByCreatedAtBefore(LocalDateTime createdAt);
}
