package com.example.backend.sys.perfis;


public record PerfisRequestDTO
        (
                String nome,
                String descricao,
                Integer nivelAcesso
        ) {
}
