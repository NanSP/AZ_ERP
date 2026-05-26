package com.example.backend.sm.ordensServico;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdensServicoRequestDTO
        (
                String numeroOs,
                Integer cliente,
                Integer produto,
                String tipoServico,
                String descricaoProblema,
                String prioridade,
                LocalDateTime dataAbertura,
                LocalDate dataAgendamento,
                LocalDateTime dataInicio,
                LocalDateTime dataFim,
                Integer tecnico,
                String status
        ) {
}
