package com.example.backend.qm.naoConformidade;

import com.example.backend.qm.inspecoes.Inspecoes;
import com.example.backend.rh.colaboradores.Colaboradores;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NaoConformidadeResponseDTO
        (
                Integer id,
                Inspecoes inspecaoId,
                String tipoNaoConformidade,
                String descricao,
                String causaRaiz,
                String acaoImediata,
                String acaoCorretiva,
                Colaboradores responsavelId,
                LocalDate dataIdentificacao,
                LocalDate dataResolucao,
                String status,
                LocalDateTime createdAt
        ) {
    public NaoConformidadeResponseDTO(NaoConformidade naoConformidade) {
        this(
                naoConformidade.getId(),
                naoConformidade.getInspecaoId(),
                naoConformidade.getTipoNaoConformidade(),
                naoConformidade.getDescricao(),
                naoConformidade.getCausaRaiz(),
                naoConformidade.getAcaoImediata(),
                naoConformidade.getAcaoCorretiva(),
                naoConformidade.getResponsavelId(),
                naoConformidade.getDataIdentificacao(),
                naoConformidade.getDataResolucao(),
                naoConformidade.getStatus(),
                naoConformidade.getCreatedAt()
        );
    }
}
