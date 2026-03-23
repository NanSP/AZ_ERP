package com.example.backend.sm.atendimentos;
import com.example.backend.sm.ordensServico.OrdensServico;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentosResponseDTO
        (
                Integer id,
                OrdensServico osId,
                Colaboradores tecnicoId,
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
                        atendimentos.getOsId(),
                        atendimentos.getTecnicoId(),
                        atendimentos.getDataHora(),
                        atendimentos.getDescricao(),
                        atendimentos.getHorasGastas(),
                        atendimentos.getMateriaisUtilizados(),
                        atendimentos.getCreatedAt()
                );
    }
}
