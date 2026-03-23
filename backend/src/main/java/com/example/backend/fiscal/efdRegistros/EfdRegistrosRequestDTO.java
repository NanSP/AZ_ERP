package com.example.backend.fiscal.efdRegistros;

import java.time.LocalDateTime;
import java.util.Map;

public record EfdRegistrosRequestDTO
        (
                LocalDateTime periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
        ) {
}
