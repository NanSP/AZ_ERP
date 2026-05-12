package com.example.backend.sys.perfilPermissao;

public record PerfilPermissaoResponseDTO
        (
                Integer id,
                Integer perfil,
                Integer permissao
        )
    {
        public PerfilPermissaoResponseDTO(PerfilPermissao perfilPermissao) {
            this(
                    perfilPermissao.getId(),
                    perfilPermissao.getPerfil() != null ? perfilPermissao.getPerfil().getId() : null,
                    perfilPermissao.getPermissao() != null ? perfilPermissao.getPermissao().getId() : null
            );
        }
}
