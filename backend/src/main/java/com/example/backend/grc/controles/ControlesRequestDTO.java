package com.example.backend.grc.controles;

import java.time.LocalDateTime;

public record ControlesRequestDTO
        (
                String codigo,
                String descricao,
                String tipoControle,
                String frequencia,
                Integer responsavel,
                Boolean efetivo,
                LocalDateTime createdAt
        ) {
}
