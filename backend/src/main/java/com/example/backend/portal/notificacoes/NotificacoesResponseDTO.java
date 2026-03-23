package com.example.backend.portal.notificacoes;

import com.example.backend.sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record NotificacoesResponseDTO
        (
                Integer id,
                Usuarios usuarioId,
                String titulo,
                String mensagem,
                String tipo,
                Boolean lida,
                LocalDateTime dataLeitura,
                LocalDateTime createdAt
        ) {
    public NotificacoesResponseDTO(Notificacoes notificacoes) {
        this
                (
                        notificacoes.getId(),
                        notificacoes.getUsuarioId(),
                        notificacoes.getTitulo(),
                        notificacoes.getMensagem(),
                        notificacoes.getTipo(),
                        notificacoes.getLida(),
                        notificacoes.getDataLeitura(),
                        notificacoes.getCreatedAt()
                );
    }
}
