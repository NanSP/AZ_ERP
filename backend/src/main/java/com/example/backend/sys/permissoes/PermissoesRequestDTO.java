package com.example.backend.sys.permissoes;

import java.time.LocalDateTime;

public record PermissoesRequestDTO
        (
                String nome,
                String descricao,
                String modulo,
                String recurso,
                String acao,
                LocalDateTime createdAt
        ) {
}
