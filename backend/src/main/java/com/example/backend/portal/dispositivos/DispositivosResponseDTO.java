package com.example.backend.portal.dispositivos;

import java.time.LocalDateTime;

public record DispositivosResponseDTO
        (
                Integer id,
                Integer usuario,
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
                        dispositivos.getUsuario() != null ? dispositivos.getUsuario().getId() : null,
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
