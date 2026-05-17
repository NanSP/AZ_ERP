package com.example.backend.grc.consentimentos;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record ConsentimentosRequestDTO
        (
                Integer titular,
                String tipoTitular,
                String finalidade,
                LocalDateTime dataConsentimento,
                LocalDateTime dataRevogacao,
                InetAddress ipAddress,
                String userAgent
        ) {
}
