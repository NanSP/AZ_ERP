package com.example.backend.auditoria.logAcoes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogAcoesRequestDTO
        (
                Integer usuario,
                String modulo,
                String acao,
                String tabela,
                Integer registroId,
                Map<String, Object> dadosAntigos,
                Map<String, Object> dadosNovos,
                InetAddress ipAddress,
                String userAgent,
                LocalDateTime createdAt
        ) {
}
