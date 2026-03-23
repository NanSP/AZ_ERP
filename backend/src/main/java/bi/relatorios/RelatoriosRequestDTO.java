package bi.relatorios;

import java.time.LocalDateTime;
import java.util.Map;

public record RelatoriosRequestDTO
        (
                String nome,
                String descricao,
                String tipoRelatorio,
                String querySql,
                Map<String, Object> parametros,
                LocalDateTime createdAt
        ) {
}
