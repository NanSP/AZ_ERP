package qm.naoConformidade;

import qm.inspecoes.Inspecoes;
import rh.colaboradores.Colaboradores;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record NaoConformidadeRequestDTO
        (
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
}
