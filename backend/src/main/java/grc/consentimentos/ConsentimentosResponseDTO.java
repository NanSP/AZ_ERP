package grc.consentimentos;

import java.net.InetAddress;
import java.time.LocalDateTime;

public record ConsentimentosResponseDTO
        (
                Integer id,
                Integer titularId,
                String tipoTitular,
                String finalidade,
                LocalDateTime dataConsentimento,
                LocalDateTime dataRevogacao,
                InetAddress ipAddress,
                String userAgent
        ) {
    public ConsentimentosResponseDTO(Consentimentos consentimentos) {
        this
                (
                        consentimentos.getId(),
                        consentimentos.getTitularId(),
                        consentimentos.getTipoTitular(),
                        consentimentos.getFinalidade(),
                        consentimentos.getDataConsentimento(),
                        consentimentos.getDataRevogacao(),
                        consentimentos.getIpAddress(),
                        consentimentos.getUserAgent()
                );
    }
}
