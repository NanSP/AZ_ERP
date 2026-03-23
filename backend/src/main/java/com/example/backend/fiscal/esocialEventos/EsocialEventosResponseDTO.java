package com.example.backend.fiscal.esocialEventos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EsocialEventosResponseDTO
        (
                Integer id,
                LocalDate periodoApuracao,
                String tipoEvento,
                String eventoId,
                String conteudo,
                LocalDateTime createdAt
        ) {
    public EsocialEventosResponseDTO(EsocialEventos esocialEventos) {
        this
                (
                        esocialEventos.getId(),
                        esocialEventos.getPeriodoApuracao(),
                        esocialEventos.getTipoEvento(),
                        esocialEventos.getEventoId(),
                        esocialEventos.getConteudo(),
                        esocialEventos.getCreatedAt()
                );
    }
}
