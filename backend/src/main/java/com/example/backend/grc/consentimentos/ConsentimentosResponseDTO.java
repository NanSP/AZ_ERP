package com.example.backend.grc.consentimentos;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record ConsentimentosResponseDTO
        (
                Integer id,
                Integer titular,
                String tipoTitular,
                String finalidade,
                LocalDateTime dataConsentimento,
                LocalDateTime dataRevogacao,
                InetAddress ipAddress,
                String userAgent,
                Integer registroTratamentoId
        ) {
    public ConsentimentosResponseDTO(Consentimentos consentimentos) {
        this
                (
                        consentimentos.getId(),
                        consentimentos.getTitular(),
                        consentimentos.getTipoTitular(),
                        consentimentos.getFinalidade(),
                        consentimentos.getDataConsentimento(),
                        consentimentos.getDataRevogacao(),
                        consentimentos.getIpAddress(),
                        consentimentos.getUserAgent(),
                        consentimentos.getRegistroTratamento() != null
                                ? consentimentos.getRegistroTratamento().getId()
                                : null
                );
    }
}
