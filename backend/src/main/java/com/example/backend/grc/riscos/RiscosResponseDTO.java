package com.example.backend.grc.riscos;

import com.example.backend.sys.usuarios.Usuarios;

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
                Usuarios responsavelId,
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
                        riscos.getResponsavelId(),
                        riscos.getPlanoMitigacao(),
                        riscos.getCreatedAt()
                );
    }
}
