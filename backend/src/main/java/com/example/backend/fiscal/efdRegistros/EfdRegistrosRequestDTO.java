package com.example.backend.fiscal.efdRegistros;

import java.time.LocalDate;
import java.util.Map;

public record EfdRegistrosRequestDTO
        (
                LocalDate periodo,
                String registro,
                Map<String, Object> conteudo
        ) {
}
