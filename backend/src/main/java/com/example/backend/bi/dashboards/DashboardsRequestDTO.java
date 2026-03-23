package com.example.backend.bi.dashboards;

import java.time.LocalDateTime;
import java.util.Map;

public record DashboardsRequestDTO
        (
                String nome,
                String descricao,
                Map<String, Object> layout,
                Map<String, Object> configuracoes,
                LocalDateTime createdAt
        ) {
}
