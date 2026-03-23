package com.example.backend.auditoria.logErros;

import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogErrosResponseDTO
        (
                Integer id,
                Integer erroCodigo,
                Integer erroMensagem,
                String modulo,
                Usuarios usuarioId,
                String url,
                Map<String, Object> parametros,
                InetAddress ipAddress,
                LocalDateTime createdAt
        ) {
    public LogErrosResponseDTO(LogErros logErros) {
        this
                (
                        logErros.getId(),
                        logErros.getErroCodigo(),
                        logErros.getErroMensagem(),
                        logErros.getModulo(),
                        logErros.getUsuarioId(),
                        logErros.getUrl(),
                        logErros.getParametros(),
                        logErros.getIpAddress(),
                        logErros.getCreatedAt()
                );
    }
}
