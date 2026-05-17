package com.example.backend.grc.auditorias;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AuditoriasResponseDTO
        (
                Integer id,
                String titulo,
                String tipoAuditoria,
                String escopo,
                LocalDate dataInicio,
                LocalDate dataFim,
                Integer responsavel,
                String status,
                LocalDateTime createdAt
        ) {
    public AuditoriasResponseDTO(Auditorias auditorias) {
        this
                (
                        auditorias.getId(),
                        auditorias.getTitulo(),
                        auditorias.getTipoAuditoria(),
                        auditorias.getEscopo(),
                        auditorias.getDataInicio(),
                        auditorias.getDataFim(),
                        auditorias.getResponsavel() != null ? auditorias.getResponsavel().getId() : null,
                        auditorias.getStatus(),
                        auditorias.getCreatedAt()
                );
    }
}
