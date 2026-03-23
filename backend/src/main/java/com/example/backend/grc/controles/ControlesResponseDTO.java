package com.example.backend.grc.controles;

import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record ControlesResponseDTO
        (
                Integer id,
                String codigo,
                String descricao,
                String tipoControle,
                String frequencia,
                Usuarios responsavelId,
                Boolean efetivo,
                LocalDateTime createdAt
        ) {
    public ControlesResponseDTO(Controles controles) {
        this
                (
                        controles.getId(),
                        controles.getCodigo(),
                        controles.getDescricao(),
                        controles.getTipoControle(),
                        controles.getFrequencia(),
                        controles.getResponsavelId(),
                        controles.getEfetivo(),
                        controles.getCreatedAt()
                );
    }
}
