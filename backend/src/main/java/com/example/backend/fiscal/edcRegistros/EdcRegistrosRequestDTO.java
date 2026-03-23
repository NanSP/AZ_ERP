package com.example.backend.fiscal.edcRegistros;

import java.time.LocalDateTime;
import java.util.Map;

public record EdcRegistrosRequestDTO
        (
                LocalDateTime periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
        ) {
}
