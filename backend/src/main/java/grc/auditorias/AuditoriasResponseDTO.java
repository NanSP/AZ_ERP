package grc.auditorias;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import sys.usuarios.Usuarios;

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
                Usuarios responsavelId,
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
                        auditorias.getResponsavelId(),
                        auditorias.getStatus(),
                        auditorias.getCreatedAt()
                );
    }
}
