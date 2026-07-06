package com.example.backend.grc.incidentesSeguranca;

import java.time.LocalDateTime;

public record IncidentesSegurancaResponseDTO(
        Integer id,
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
        LocalDateTime dataRegistro,
        LocalDateTime dataAvaliacao,
        LocalDateTime dataResposta,
        LocalDateTime dataComunicacao,
        LocalDateTime dataEncerramento,
        String causaRaiz,
        String acoesContencao,
        String acoesCorretivas,
        Integer responsavel,
        LocalDateTime createdAt
) {
    public IncidentesSegurancaResponseDTO(IncidentesSeguranca entity) {
        this(
                entity.getId(),
                entity.getCodigo(),
                entity.getTitulo(),
                entity.getEscopoCritico(),
                entity.getSeveridade(),
                entity.getEtapaAtual(),
                entity.getOrigemDeteccao(),
                entity.getResumoIncidente(),
                entity.getDadosAfetados(),
                entity.getTitularesEstimados(),
                entity.getSegredoTecnicoExposto(),
                entity.getRequerComunicacaoAnpd(),
                entity.getRequerComunicacaoTitulares(),
                entity.getDataDeteccao(),
                entity.getDataRegistro(),
                entity.getDataAvaliacao(),
                entity.getDataResposta(),
                entity.getDataComunicacao(),
                entity.getDataEncerramento(),
                entity.getCausaRaiz(),
                entity.getAcoesContencao(),
                entity.getAcoesCorretivas(),
                entity.getResponsavel() != null ? entity.getResponsavel().getId() : null,
                entity.getCreatedAt()
        );
    }
}
