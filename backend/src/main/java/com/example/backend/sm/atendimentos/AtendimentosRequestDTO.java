package com.example.backend.sm.atendimentos;

import com.example.backend.sm.ordensServico.OrdensServico;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentosRequestDTO
        (
                OrdensServico osId,
                Colaboradores tecnicoId,
                LocalDateTime dataHora,
                String descricao,
                BigDecimal horasGastas,
                Map<String, Object> materiaisUtilizados,
                LocalDateTime createdAt
        ) {
}
