package com.example.backend.portal.sessoes;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record SessoesRequestDTO
        (
                Integer usuario,
                String tokenSessao,
                InetAddress ipAddress,
                String userAgent,
                LocalDateTime dataLogin,
                LocalDateTime dataLogout,
                LocalDateTime expiracao
        ) {
}
