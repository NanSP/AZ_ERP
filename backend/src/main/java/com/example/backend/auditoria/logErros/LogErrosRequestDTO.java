package com.example.backend.auditoria.logErros;

import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogErrosRequestDTO
        (
                Integer erroCodigo,
                String erroMensagem,
                String modulo,
                Usuarios usuarioId,
                String url,
                Map<String, Object> parametros,
                InetAddress ipAddress,
                LocalDateTime createdAt
        ) {
}
