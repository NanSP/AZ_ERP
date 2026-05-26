package com.example.backend.sm.slaConfig;


public record SlaConfigRequestDTO
        (
                String tipoServico,
                String prioridade,
                Integer tempoAtendimentoHoras,
                Integer tempoResolucaoHoras
        ) {
}
