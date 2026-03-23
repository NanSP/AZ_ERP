package com.example.backend.portal.sessoes;

import com.example.backend.sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record SessoesResponseDTO
        (
                Integer id,
                Usuarios usuarioId,
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
                        sessoes.getUsuarioId(),
                        sessoes.getTokenSessao(),
                        sessoes.getIpAddress(),
                        sessoes.getUserAgent(),
                        sessoes.getDataLogin(),
                        sessoes.getDataLogout(),
                        sessoes.getExpiracao()
                );
    }
}
