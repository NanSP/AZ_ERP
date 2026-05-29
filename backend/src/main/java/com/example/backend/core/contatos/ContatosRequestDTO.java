package com.example.backend.core.contatos;

public record ContatosRequestDTO
        (
                String entidadeTipo,
                Integer entidadeId,
                String tipoContato,
                String valor,
                Boolean principal,
                String observacao
        ) {
}
