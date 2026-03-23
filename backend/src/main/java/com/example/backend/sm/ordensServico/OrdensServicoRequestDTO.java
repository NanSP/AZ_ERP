package com.example.backend.sm.ordensServico;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.time.LocalDateTime;

public record OrdensServicoRequestDTO
        (
                String numeroOs,
                Parceiros clienteId,
                Produtos produtoId,
                String tipoServico,
                String descricaoProblema,
                String prioridade,
                LocalDateTime dataAbertura,
                LocalDateTime dataAgendamento,
                LocalDateTime dataInicio,
                LocalDateTime dataFim,
                Colaboradores tecnicoId,
                String status,
                LocalDateTime createdAt
        ) {
}
