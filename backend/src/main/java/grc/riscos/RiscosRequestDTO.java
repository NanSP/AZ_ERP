package grc.riscos;

import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record RiscosRequestDTO
        (
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
}
