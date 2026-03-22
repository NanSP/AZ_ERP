package sys.perfis;

import java.time.LocalDateTime;

public record PerfisRequestDTO
        (
                Integer id,
                String nome,
                String descricao,
                Integer nivelAcesso,
                LocalDateTime createdAt
        ) {
}
