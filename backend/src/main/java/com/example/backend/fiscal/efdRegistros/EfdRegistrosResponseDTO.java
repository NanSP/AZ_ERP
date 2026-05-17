package com.example.backend.fiscal.efdRegistros;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record EfdRegistrosResponseDTO
        (
                Long id,
                LocalDate periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
) {
    public EfdRegistrosResponseDTO(EfdRegistros efdRegistros) {
        this
                (
                        efdRegistros.getId(),
                        efdRegistros.getPeriodo(),
                        efdRegistros.getRegistro(),
                        efdRegistros.getConteudo(),
                        efdRegistros.getCreatedAt()
                );
    }
}
