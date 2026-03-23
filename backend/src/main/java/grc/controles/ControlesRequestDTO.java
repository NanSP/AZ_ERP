package grc.controles;

import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record ControlesRequestDTO
        (
                String codigo,
                String descricao,
                String tipoControle,
                String frequencia,
                Usuarios responsavelId,
                Boolean efetivo,
                LocalDateTime createdAt
        ) {
}
