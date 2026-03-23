package com.example.backend.grc.auditorias;

import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AuditoriasRequestDTO
        (
                String titulo,
                String tipoAuditoria,
                String escopo,
                LocalDate dataInicio,
                LocalDate dataFim,
                Usuarios responsavelId,
                String status,
                LocalDateTime createdAt
        ) {
}
