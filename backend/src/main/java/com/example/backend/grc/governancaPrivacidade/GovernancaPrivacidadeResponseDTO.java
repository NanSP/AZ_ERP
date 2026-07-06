package com.example.backend.grc.governancaPrivacidade;

import java.time.LocalDateTime;

public record GovernancaPrivacidadeResponseDTO(
        Integer id,
        String nomeReferencia,
        String papelPrivacidade,
        String encarregadoNome,
        String encarregadoEmail,
        String encarregadoCanal,
        String baseContratual,
        String clausulasContratuais,
        Boolean suboperadoresDeclarados,
        Boolean transferenciaInternacional,
        String procedimentoIncidente,
        Boolean ativo,
        LocalDateTime vigenteDesde,
        LocalDateTime revisaoProgramadaEm,
        String observacoes,
        LocalDateTime createdAt
) {
    public GovernancaPrivacidadeResponseDTO(GovernancaPrivacidade entity) {
        this(
                entity.getId(),
                entity.getNomeReferencia(),
                entity.getPapelPrivacidade(),
                entity.getEncarregadoNome(),
                entity.getEncarregadoEmail(),
                entity.getEncarregadoCanal(),
                entity.getBaseContratual(),
                entity.getClausulasContratuais(),
                entity.getSuboperadoresDeclarados(),
                entity.getTransferenciaInternacional(),
                entity.getProcedimentoIncidente(),
                entity.getAtivo(),
                entity.getVigenteDesde(),
                entity.getRevisaoProgramadaEm(),
                entity.getObservacoes(),
                entity.getCreatedAt()
        );
    }
}
