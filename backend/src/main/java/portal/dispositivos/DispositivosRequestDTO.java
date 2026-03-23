package portal.dispositivos;

import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record DispositivosRequestDTO
        (
                Usuarios usuarioId,
                String deviceId,
                String deviceModel,
                String devicePlatform,
                String pushToken,
                LocalDateTime ultimoAcesso,
                Boolean ativo,
                LocalDateTime createdAt
        ) {
}
