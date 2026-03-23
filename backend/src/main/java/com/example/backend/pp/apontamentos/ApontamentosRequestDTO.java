package com.example.backend.pp.apontamentos;

import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ApontamentosRequestDTO
        (
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
        ) {
}
