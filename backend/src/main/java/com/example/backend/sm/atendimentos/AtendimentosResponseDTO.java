package com.example.backend.sm.atendimentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentosResponseDTO
        (
                Integer id,
                Integer os,
                Integer tecnico,
                LocalDateTime dataHora,
                String descricao,
                BigDecimal horasGastas,
                Map<String, Object> materiaisUtilizados,
                LocalDateTime createdAt
        ) {
    public AtendimentosResponseDTO(Atendimentos atendimentos) {
        this
                (
                        atendimentos.getId(),
                        atendimentos.getOs() != null ? atendimentos.getOs().getId() : null,
                        atendimentos.getTecnico() != null ? atendimentos.getTecnico().getId() : null,
                        atendimentos.getDataHora(),
                        atendimentos.getDescricao(),
                        atendimentos.getHorasGastas(),
                        atendimentos.getMateriaisUtilizados(),
                        atendimentos.getCreatedAt()
                );
    }
}
