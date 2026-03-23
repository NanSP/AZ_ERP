package com.example.backend.ps.projetos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.ps.tarefas.Tarefas;
import com.example.backend.sys.usuarios.Usuarios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjetosRequestDTO
        (
                List<Tarefas> tarefas,
                String codigo,
                String nome,
                String descricao,
                Parceiros clienteId,
                Usuarios gerenteId,
                LocalDate dataInicio,
                LocalDate dataFim,
                LocalDate dataPrevistaInicio,
                LocalDate dataPrevistaFim,
                BigDecimal orcamentoTotal,
                BigDecimal orcamentoGasto,
                String status,
                Integer prioridade,
                LocalDateTime createdAt
        ) {
}
