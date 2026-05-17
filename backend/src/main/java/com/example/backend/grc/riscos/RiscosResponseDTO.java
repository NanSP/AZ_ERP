package com.example.backend.grc.riscos;

import java.time.LocalDateTime;

public record RiscosResponseDTO
        (
                Integer id,
                String codigo,
                String titulo,
                String descricao,
                String categoria,
                Integer probabilidade,
                Integer impacto,
                String nivelRisco,
                Integer responsavel,
                String planoMitigacao,
                LocalDateTime createdAt
        ) {
    public RiscosResponseDTO(Riscos riscos) {
        this
                (
                        riscos.getId(),
                        riscos.getCodigo(),
                        riscos.getTitulo(),
                        riscos.getDescricao(),
                        riscos.getCategoria(),
                        riscos.getProbabilidade(),
                        riscos.getImpacto(),
                        riscos.getNivelRisco(),
                        riscos.getResponsavel() != null ? riscos.getResponsavel().getId() : null,
                        riscos.getPlanoMitigacao(),
                        riscos.getCreatedAt()
                );
    }
}
