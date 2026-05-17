package com.example.backend.qm.inspecoes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InspecoesResponseDTO
        (
                Integer id,
                String tipoInspecao,
                Integer produto,
                String lote,
                BigDecimal quantidadeInspecionada,
                BigDecimal quantidadeAprovada,
                BigDecimal quantidadeReprovada,
                LocalDate dataInspecao,
                Integer inspetor,
                String resultado,
                String observacoes,
                LocalDateTime createdAt
        ) {
    public InspecoesResponseDTO(Inspecoes inspecoes) {
        this(
                inspecoes.getId(),
                inspecoes.getTipoInspecao(),
                inspecoes.getProduto() != null ? inspecoes.getProduto().getId() : null,
                inspecoes.getLote(),
                inspecoes.getQuantidadeInspecionada(),
                inspecoes.getQuantidadeAprovada(),
                inspecoes.getQuantidadeReprovada(),
                inspecoes.getDataInspecao(),
                inspecoes.getInspetor() != null ? inspecoes.getInspetor().getId() : null,
                inspecoes.getResultado(),
                inspecoes.getObservacoes(),
                inspecoes.getCreatedAt()
        );
    }
}
