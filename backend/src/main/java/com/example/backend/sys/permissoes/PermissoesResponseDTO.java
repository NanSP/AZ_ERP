package com.example.backend.sys.permissoes;

import java.time.LocalDateTime;

public record PermissoesResponseDTO
        (
                Integer id,
                String nome,
                String descricao,
                String modulo,
                String recurso,
                String acao,
                LocalDateTime createdAt
        )
    {
        public PermissoesResponseDTO(Permissoes permissoes) {
            this(
                    permissoes.getId(),
                    permissoes.getNome(),
                    permissoes.getDescricao(),
                    permissoes.getModulo(),
                    permissoes.getRecurso(),
                    permissoes.getAcao(),
                    permissoes.getCreatedAt()
            );
        }
}
