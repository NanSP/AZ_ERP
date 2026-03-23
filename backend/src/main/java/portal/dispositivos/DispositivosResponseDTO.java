package portal.dispositivos;

import sys.usuarios.Usuarios;

import java.time.LocalDateTime;

public record DispositivosResponseDTO
        (
                Integer id,
                Usuarios usuarioId,
                String deviceId,
                String deviceModel,
                String devicePlatform,
                String pushToken,
                LocalDateTime ultimoAcesso,
                Boolean ativo,
                LocalDateTime createdAt
        ) {
    public DispositivosResponseDTO(Dispositivos dispositivos) {
        this
                (
                        dispositivos.getId(),
                        dispositivos.getUsuarioId(),
                        dispositivos.getDeviceId(),
                        dispositivos.getDeviceModel(),
                        dispositivos.getDevicePlatform(),
                        dispositivos.getPushToken(),
                        dispositivos.getUltimoAcesso(),
                        dispositivos.getAtivo(),
                        dispositivos.getCreatedAt()
                );
    }
}
