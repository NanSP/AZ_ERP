package sys.perfis;

import java.time.LocalDateTime;

public record PerfisResponseDTO
        (
                Integer id,
                String nome,
                String descricao,
                Integer nivelAcesso,
                LocalDateTime createdAt
        )
    {
        public PerfisResponseDTO(Perfis perfis) {
            this(
                    perfis.getId(),
                    perfis.getNome(),
                    perfis.getDescricao(),
                    perfis.getNivelAcesso(),
                    perfis.getCreatedAt()
            );
        }
}
