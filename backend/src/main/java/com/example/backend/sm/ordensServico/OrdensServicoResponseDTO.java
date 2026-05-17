package com.example.backend.sm.ordensServico;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrdensServicoResponseDTO
        (
                Integer id,
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
                String status,
                LocalDateTime createdAt
        ) {
    public OrdensServicoResponseDTO(OrdensServico ordensServico) {
        this
                (
                        ordensServico.getId(),
                        ordensServico.getNumeroOs(),
                        ordensServico.getCliente() != null ? ordensServico.getCliente().getId() : null,
                        ordensServico.getProduto() != null ? ordensServico.getProduto().getId() : null,
                        ordensServico.getTipoServico(),
                        ordensServico.getDescricaoProblema(),
                        ordensServico.getPrioridade(),
                        ordensServico.getDataAbertura(),
                        ordensServico.getDataAgendamento(),
                        ordensServico.getDataInicio(),
                        ordensServico.getDataFim(),
                        ordensServico.getTecnico() != null ? ordensServico.getTecnico().getId() : null,
                        ordensServico.getStatus(),
                        ordensServico.getCreatedAt()
                );
    }
}
