package sys.permissoes;

import java.time.LocalDateTime;

public record PermissoesRequestDTO
        (
                Integer id,
                String nome,
                String descricao,
                String modulo,
                String recurso,
                String acao,
                LocalDateTime createdAt
        ) {
}
