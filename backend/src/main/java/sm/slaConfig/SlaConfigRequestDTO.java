package sm.slaConfig;

import java.time.LocalDateTime;

public record SlaConfigRequestDTO
        (
                String tipoServico,
                String prioridade,
                Integer tempoAtendimentoHoras,
                Integer tempoResolucaoHoras,
                LocalDateTime createdAt
        ) {
}
