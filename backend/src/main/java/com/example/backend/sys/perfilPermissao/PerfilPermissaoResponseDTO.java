package com.example.backend.sys.perfilPermissao;

import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.permissoes.Permissoes;

public record PerfilPermissaoResponseDTO
        (
                Integer id,
                Perfis perfilId,
                Permissoes permissaoId
        )
    {
        public PerfilPermissaoResponseDTO(PerfilPermissao perfilPermissao) {
            this(
                    perfilPermissao.getId(),
                    perfilPermissao.getPerfilId(),
                    perfilPermissao.getPermissaoId()
            );
        }
}
