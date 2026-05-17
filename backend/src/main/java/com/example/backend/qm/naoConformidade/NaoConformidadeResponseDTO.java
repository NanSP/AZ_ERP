package com.example.backend.qm.naoConformidade;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NaoConformidadeResponseDTO
        (
                Integer id,
                Integer inspecao,
                String tipoNaoConformidade,
                String descricao,
                String causaRaiz,
                String acaoImediata,
                String acaoCorretiva,
                Integer responsavel,
                LocalDate dataIdentificacao,
                LocalDate dataResolucao,
                String status,
                LocalDateTime createdAt
        ) {
    public NaoConformidadeResponseDTO(NaoConformidade naoConformidade) {
        this(
                naoConformidade.getId(),
                naoConformidade.getInspecao() != null ? naoConformidade.getInspecao().getId() : null,
                naoConformidade.getTipoNaoConformidade(),
                naoConformidade.getDescricao(),
                naoConformidade.getCausaRaiz(),
                naoConformidade.getAcaoImediata(),
                naoConformidade.getAcaoCorretiva(),
                naoConformidade.getResponsavel() != null ? naoConformidade.getResponsavel().getId() : null,
                naoConformidade.getDataIdentificacao(),
                naoConformidade.getDataResolucao(),
                naoConformidade.getStatus(),
                naoConformidade.getCreatedAt()
        );
    }
}
