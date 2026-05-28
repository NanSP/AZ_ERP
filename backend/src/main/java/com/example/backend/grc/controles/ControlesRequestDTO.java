package com.example.backend.grc.controles;


public record ControlesRequestDTO
        (
                String codigo,
                String descricao,
                String tipoControle,
                String frequencia,
                Integer responsavel,
                Boolean efetivo
        ) {
}
