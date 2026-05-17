package com.example.backend.fiscal.efdRegistros;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record EfdRegistrosRequestDTO
        (
                LocalDate periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
        ) {
}
