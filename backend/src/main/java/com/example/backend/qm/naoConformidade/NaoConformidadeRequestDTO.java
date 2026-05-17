package com.example.backend.qm.naoConformidade;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NaoConformidadeRequestDTO
        (
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
}
