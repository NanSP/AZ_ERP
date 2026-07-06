package com.example.backend.grc.solicitacoesTitular;

import java.util.Map;

public record SolicitacaoTitularEventoRequestDTO(
        String tipoEvento,
        String titulo,
        String descricao,
        Map<String, Object> detalhesJson
) {
}
