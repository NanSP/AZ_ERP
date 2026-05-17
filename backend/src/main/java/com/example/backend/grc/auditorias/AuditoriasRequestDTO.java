package com.example.backend.grc.auditorias;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AuditoriasRequestDTO
        (
                String titulo,
                String tipoAuditoria,
                String escopo,
                LocalDate dataInicio,
                LocalDate dataFim,
                Integer responsavel,
                String status,
                LocalDateTime createdAt
        ) {
}
