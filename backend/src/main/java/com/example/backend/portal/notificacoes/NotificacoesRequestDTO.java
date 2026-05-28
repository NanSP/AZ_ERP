package com.example.backend.portal.notificacoes;

import java.time.LocalDateTime;

public record NotificacoesRequestDTO
        (
                Integer usuario,
                String titulo,
                String mensagem,
                String tipo,
                Boolean lida,
                LocalDateTime dataLeitura
        ) {
}
