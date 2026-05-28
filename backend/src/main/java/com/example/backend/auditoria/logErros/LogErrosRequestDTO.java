package com.example.backend.auditoria.logErros;

import java.net.InetAddress;
import java.util.Map;

public record LogErrosRequestDTO
        (
                Integer erroCodigo,
                String erroMensagem,
                String modulo,
                Integer usuario,
                String url,
                Map<String, Object> parametros,
                InetAddress ipAddress
        ) {
}
