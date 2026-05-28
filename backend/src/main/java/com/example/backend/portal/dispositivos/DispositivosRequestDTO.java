package com.example.backend.portal.dispositivos;

import java.time.LocalDateTime;

public record DispositivosRequestDTO
        (
                Integer usuario,
                String deviceId,
                String deviceModel,
                String devicePlatform,
                String pushToken,
                LocalDateTime ultimoAcesso,
                Boolean ativo
        ) {
}
