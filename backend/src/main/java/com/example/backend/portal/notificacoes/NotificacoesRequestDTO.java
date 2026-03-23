package com.example.backend.portal.notificacoes;

import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record NotificacoesRequestDTO
        (
                Usuarios usuarioId,
                String titulo,
                String mensagem,
                String tipo,
                Boolean lida,
                LocalDateTime dataLeitura,
                LocalDateTime createdAt
        ) {
}
