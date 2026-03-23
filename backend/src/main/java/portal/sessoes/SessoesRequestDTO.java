package portal.sessoes;

import sys.usuarios.Usuarios;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record SessoesRequestDTO
        (
                Usuarios usuarioId,
                String tokenSessao,
                InetAddress ipAddress,
                String userAgent,
                LocalDateTime dataLogin,
                LocalDateTime dataLogout,
                LocalDateTime expiracao
        ) {
}
