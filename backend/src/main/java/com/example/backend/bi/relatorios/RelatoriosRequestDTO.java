package com.example.backend.bi.relatorios;

import java.util.Map;

public record RelatoriosRequestDTO
        (
                String nome,
                String descricao,
                String tipoRelatorio,
                String querySql,
                Map<String, Object> parametros
        ) {
}
