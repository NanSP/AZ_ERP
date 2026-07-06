package com.example.backend.grc.relatoriosImpacto;

import java.time.LocalDateTime;

public record RelatoriosImpactoResponseDTO(
        Integer id,
        String titulo,
        String escopoCritico,
        String prioridadeRisco,
        String modulo,
        String recurso,
        String finalidade,
        String dadosPessoaisEnvolvidos,
        Boolean dadosSensiveis,
        String baseLegal,
        Integer volumeTitulares,
        Boolean compartilhamentoExterno,
        String medidasTecnicas,
        String medidasOrganizacionais,
        String riscoResidual,
        String decisao,
        Integer aprovadoPor,
        LocalDateTime revisadoEm,
        LocalDateTime createdAt
) {
    public RelatoriosImpactoResponseDTO(RelatoriosImpacto entity) {
        this(
                entity.getId(),
                entity.getTitulo(),
                entity.getEscopoCritico(),
                entity.getPrioridadeRisco(),
                entity.getModulo(),
                entity.getRecurso(),
                entity.getFinalidade(),
                entity.getDadosPessoaisEnvolvidos(),
                entity.getDadosSensiveis(),
                entity.getBaseLegal(),
                entity.getVolumeTitulares(),
                entity.getCompartilhamentoExterno(),
                entity.getMedidasTecnicas(),
                entity.getMedidasOrganizacionais(),
                entity.getRiscoResidual(),
                entity.getDecisao(),
                entity.getAprovadoPor() != null ? entity.getAprovadoPor().getId() : null,
                entity.getRevisadoEm(),
                entity.getCreatedAt()
        );
    }
}
