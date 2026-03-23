package portal.notificacoes;

import sys.usuarios.Usuarios;

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
