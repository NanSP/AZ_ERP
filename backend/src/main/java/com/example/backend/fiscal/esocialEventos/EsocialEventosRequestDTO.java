package com.example.backend.fiscal.esocialEventos;

import java.time.LocalDate;

public record EsocialEventosRequestDTO
        (
                LocalDate periodoApuracao,
                String tipoEvento,
                String eventoId,
                String conteudo,
                String status
        ) {
}
