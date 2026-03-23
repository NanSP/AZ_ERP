package com.example.backend.fiscal.esocialEventos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EsocialEventosRequestDTO
        (
                LocalDate periodoApuracao,
                String tipoEvento,
                String eventoId,
                String conteudo,
                LocalDateTime createdAt
        ) {
}
