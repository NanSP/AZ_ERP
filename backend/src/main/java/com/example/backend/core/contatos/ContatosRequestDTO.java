package com.example.backend.core.contatos;

import java.time.LocalDateTime;

public record ContatosRequestDTO
        (
                String entidadeTipo,
                Integer entidadeId,
                String tipoContato,
                String valor,
                Boolean principal,
                String observacao,
                LocalDateTime createdAt
        ) {
}
