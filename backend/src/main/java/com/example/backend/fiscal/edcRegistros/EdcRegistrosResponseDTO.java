package com.example.backend.fiscal.edcRegistros;

import java.time.LocalDateTime;
import java.util.Map;

public record EdcRegistrosResponseDTO
        (
                Integer id,
                LocalDateTime periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
        ) {
    public EdcRegistrosResponseDTO(EdcRegistros edcRegistros) {
        this
                (
                        edcRegistros.getId(),
                        edcRegistros.getPeriodo(),
                        edcRegistros.getRegistro(),
                        edcRegistros.getConteudo(),
                        edcRegistros.getCreatedAt()
                );
    }
}
