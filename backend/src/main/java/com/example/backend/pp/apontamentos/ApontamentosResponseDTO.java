package com.example.backend.pp.apontamentos;
import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ApontamentosResponseDTO
        (
                Integer id,
                OrdemProducao opId,
                Integer maquinaId,
                Colaboradores operadorId,
                LocalTime dataHoraInicio,
                LocalTime dataHoraFim,
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
                    apontamentos.getOpId(),
                    apontamentos.getMaquinaId(),
                    apontamentos.getOperadorId(),
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
