package com.example.backend.bi.relatorios;

import java.time.LocalDateTime;
import java.util.Map;

public record RelatoriosResponseDTO
        (
                Integer id,
                String nome,
                String descricao,
                String tipoRelatorio,
                String querySql,
                Map<String, Object> parametros,
                LocalDateTime createdAt
        ) {
    public RelatoriosResponseDTO(Relatorios relatorios) {
        this
                (
                        relatorios.getId(),
                        relatorios.getNome(),
                        relatorios.getDescricao(),
                        relatorios.getTipoRelatorio(),
                        relatorios.getQuerySql(),
                        relatorios.getParametros(),
                        relatorios.getCreatedAt()
                );
    }
}
