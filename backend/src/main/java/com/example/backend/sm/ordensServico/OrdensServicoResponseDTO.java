package com.example.backend.sm.ordensServico;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.time.LocalDateTime;

public record OrdensServicoResponseDTO
        (
                Integer id,
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
    public OrdensServicoResponseDTO(OrdensServico ordensServico) {
        this
                (
                        ordensServico.getId(),
                        ordensServico.getNumeroOs(),
                        ordensServico.getClienteId(),
                        ordensServico.getProdutoId(),
                        ordensServico.getTipoServico(),
                        ordensServico.getDescricaoProblema(),
                        ordensServico.getPrioridade(),
                        ordensServico.getDataAbertura(),
                        ordensServico.getDataAgendamento(),
                        ordensServico.getDataInicio(),
                        ordensServico.getDataFim(),
                        ordensServico.getTecnicoId(),
                        ordensServico.getStatus(),
                        ordensServico.getCreatedAt()
                );
    }
}
