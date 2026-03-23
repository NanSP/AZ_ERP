package com.example.backend.sm.slaConfig;

import java.time.LocalDateTime;

public record SlaConfigResponseDTO
        (
                Integer id,
                String tipoServico,
                String prioridade,
                Integer tempoAtendimentoHoras,
                Integer tempoResolucaoHoras,
                LocalDateTime createdAt
        ) {
    public SlaConfigResponseDTO(SlaConfig slaConfig) {
        this
                (
                        slaConfig.getId(),
                        slaConfig.getTipoServico(),
                        slaConfig.getPrioridade(),
                        slaConfig.getTempoAtendimentoHoras(),
                        slaConfig.getTempoResolucaoHoras(),
                        slaConfig.getCreatedAt()
                );
    }
}
