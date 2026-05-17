package com.example.backend.fiscal.edcRegistros;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record EcdRegistrosResponseDTO
        (
                Long id,
                LocalDate periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
        ) {
    public EcdRegistrosResponseDTO(EcdRegistros ecdRegistros) {
        this
                (
                        ecdRegistros.getId(),
                        ecdRegistros.getPeriodo(),
                        ecdRegistros.getRegistro(),
                        ecdRegistros.getConteudo(),
                        ecdRegistros.getCreatedAt()
                );
    }
}
