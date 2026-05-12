package com.example.backend.sys.perfis;

import java.time.LocalDateTime;

public record PerfisRequestDTO
        (
                String nome,
                String descricao,
                Integer nivelAcesso,
                LocalDateTime createdAt
        ) {
}
