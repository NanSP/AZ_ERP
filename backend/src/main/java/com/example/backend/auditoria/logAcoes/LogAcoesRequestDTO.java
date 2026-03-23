package com.example.backend.auditoria.logAcoes;

import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogAcoesRequestDTO
        (
                Usuarios usuarioId,
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
