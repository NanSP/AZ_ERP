package com.example.backend.auditoria.logErros;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

public record LogErrosResponseDTO
        (
                Long id,
                Integer erroCodigo,
                String erroMensagem,
                String modulo,
                Integer usuario,
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
                        logErros.getUsuario() != null ? logErros.getUsuario().getId() : null,
                        logErros.getUrl(),
                        logErros.getParametros(),
                        logErros.getIpAddress(),
                        logErros.getCreatedAt()
                );
    }
}
