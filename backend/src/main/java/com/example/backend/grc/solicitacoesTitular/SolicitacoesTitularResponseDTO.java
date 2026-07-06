package com.example.backend.grc.solicitacoesTitular;

import java.time.LocalDateTime;

public record SolicitacoesTitularResponseDTO(
        Integer id,
        String protocolo,
        String titularNome,
        String titularContato,
        String tipoTitular,
        String direitoSolicitado,
        String modulo,
        String entidade,
        String status,
        String canalOrigem,
        String detalhes,
        LocalDateTime prazoResposta,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataConclusao,
        String respostaResumo,
        Integer atendidoPor,
        Integer registroTratamentoId,
        Integer consentimentoId,
        LocalDateTime createdAt
) {
    public SolicitacoesTitularResponseDTO(SolicitacoesTitular entity) {
        this(
                entity.getId(),
                entity.getProtocolo(),
                entity.getTitularNome(),
                entity.getTitularContato(),
                entity.getTipoTitular(),
                entity.getDireitoSolicitado(),
                entity.getModulo(),
                entity.getEntidade(),
                entity.getStatus(),
                entity.getCanalOrigem(),
                entity.getDetalhes(),
                entity.getPrazoResposta(),
                entity.getDataSolicitacao(),
                entity.getDataConclusao(),
                entity.getRespostaResumo(),
                entity.getAtendidoPor() != null ? entity.getAtendidoPor().getId() : null,
                entity.getRegistroTratamento() != null ? entity.getRegistroTratamento().getId() : null,
                entity.getConsentimento() != null ? entity.getConsentimento().getId() : null,
                entity.getCreatedAt()
        );
    }
}
