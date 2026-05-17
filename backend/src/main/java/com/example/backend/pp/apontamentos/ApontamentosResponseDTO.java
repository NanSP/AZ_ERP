package com.example.backend.pp.apontamentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApontamentosResponseDTO
        (
                Integer id,
                Integer op,
                Integer maquinaId,
                Integer operador,
                LocalDateTime dataHoraInicio,
                LocalDateTime dataHoraFim,
                BigDecimal quantidadeProduzida,
                BigDecimal quantidadeRefugo,
                BigDecimal tempoParado,
                String observacoes,
                LocalDateTime createdAt
        )
    {
        public ApontamentosResponseDTO(Apontamentos apontamentos) {
            this(
                    apontamentos.getId(),
                    apontamentos.getOp() != null ? apontamentos.getOp().getId() : null,
                    apontamentos.getMaquinaId(),
                    apontamentos.getOperador() != null ?  apontamentos.getOperador().getId() : null,
                    apontamentos.getDataHoraInicio(),
                    apontamentos.getDataHoraFim(),
                    apontamentos.getQuantidadeProduzida(),
                    apontamentos.getQuantidadeRefugo(),
                    apontamentos.getTempoParado(),
                    apontamentos.getObservacoes(),
                    apontamentos.getCreatedAt()
            );
        }
}
