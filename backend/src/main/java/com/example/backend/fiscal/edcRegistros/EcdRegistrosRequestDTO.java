package com.example.backend.fiscal.edcRegistros;

import java.time.LocalDate;
import java.util.Map;

public record EcdRegistrosRequestDTO
        (
                LocalDate periodo,
                String registro,
                Map<String, Object> conteudo
        ) {
}
