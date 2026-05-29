package com.example.backend.sys.permissoes;

public record PermissoesRequestDTO
        (
                String nome,
                String descricao,
                String modulo,
                String recurso,
                String acao
        ) {
}
