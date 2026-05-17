package com.example.backend.portal.notificacoes;

import java.time.LocalDateTime;

public record NotificacoesResponseDTO
        (
                Integer id,
                Integer usuario,
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
                        notificacoes.getUsuario() != null ? notificacoes.getUsuario().getId() : null,
                        notificacoes.getTitulo(),
                        notificacoes.getMensagem(),
                        notificacoes.getTipo(),
                        notificacoes.getLida(),
                        notificacoes.getDataLeitura(),
                        notificacoes.getCreatedAt()
                );
    }
}
