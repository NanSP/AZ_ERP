package com.example.backend.grc.relatoriosImpacto;

import java.time.LocalDateTime;

public record RelatoriosImpactoRequestDTO(
        String titulo,
        String escopoCritico,
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
        LocalDateTime revisadoEm
) {
}
