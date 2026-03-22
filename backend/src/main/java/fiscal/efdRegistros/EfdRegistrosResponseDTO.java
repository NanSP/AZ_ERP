package fiscal.efdRegistros;

import java.time.LocalDateTime;
import java.util.Map;

public record EfdRegistrosResponseDTO
        (
                Integer id,
                LocalDateTime periodo,
                String registro,
                Map<String, Object> conteudo,
                LocalDateTime createdAt
) {
    public EfdRegistrosResponseDTO(EfdRegistros efdRegistros) {
        this
                (
                        efdRegistros.getId(),
                        efdRegistros.getPeriodo(),
                        efdRegistros.getRegistro(),
                        efdRegistros.getConteudo(),
                        efdRegistros.getCreatedAt()
                );
    }
}
