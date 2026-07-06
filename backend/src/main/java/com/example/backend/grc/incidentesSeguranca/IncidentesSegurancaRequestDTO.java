package com.example.backend.grc.incidentesSeguranca;

import java.time.LocalDateTime;

public record IncidentesSegurancaRequestDTO(
        String codigo,
        String titulo,
        String escopoCritico,
        String severidade,
        String etapaAtual,
        String origemDeteccao,
        String resumoIncidente,
        String dadosAfetados,
        Integer titularesEstimados,
        Boolean segredoTecnicoExposto,
        Boolean requerComunicacaoAnpd,
        Boolean requerComunicacaoTitulares,
        LocalDateTime dataDeteccao,
        LocalDateTime dataAvaliacao,
        LocalDateTime dataResposta,
        LocalDateTime dataComunicacao,
        LocalDateTime dataEncerramento,
        String causaRaiz,
        String acoesContencao,
        String acoesCorretivas,
        Integer responsavel
) {
}
