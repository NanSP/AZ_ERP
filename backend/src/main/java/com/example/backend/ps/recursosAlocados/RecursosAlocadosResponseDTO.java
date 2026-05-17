package com.example.backend.ps.recursosAlocados;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecursosAlocadosResponseDTO
        (
                Integer id,
                Integer projeto,
                Integer tarefa,
                String tipoRecurso,
                Integer recursoId,
                BigDecimal quantidade,
                BigDecimal valorUnitario,
                BigDecimal valorTotal,
                LocalDate dataAlocacao,
                LocalDateTime createdAt
        ) {
    public RecursosAlocadosResponseDTO(RecursosAlocados recursosAlocados) {
        this(
                recursosAlocados.getId(),
                recursosAlocados.getProjeto() != null ? recursosAlocados.getProjeto().getId() : null,
                recursosAlocados.getTarefa() != null ? recursosAlocados.getTarefa().getId() : null,
                recursosAlocados.getTipoRecurso(),
                recursosAlocados.getRecursoId(),
                recursosAlocados.getQuantidade(),
                recursosAlocados.getValorUnitario(),
                recursosAlocados.getValorTotal(),
                recursosAlocados.getDataAlocacao(),
                recursosAlocados.getCreatedAt()
        );
    }
}
