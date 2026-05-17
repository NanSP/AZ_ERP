package com.example.backend.portal.sessoes;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record SessoesResponseDTO
        (
                Integer id,
                Integer usuario,
                String tokenSessao,
                InetAddress ipAddress,
                String userAgent,
                LocalDateTime dataLogin,
                LocalDateTime dataLogout,
                LocalDateTime expiracao
        ) {
    public SessoesResponseDTO(Sessoes sessoes) {
        this
                (
                        sessoes.getId(),
                        sessoes.getUsuario() != null ? sessoes.getUsuario().getId() : null,
                        sessoes.getTokenSessao(),
                        sessoes.getIpAddress(),
                        sessoes.getUserAgent(),
                        sessoes.getDataLogin(),
                        sessoes.getDataLogout(),
                        sessoes.getExpiracao()
                );
    }
}
