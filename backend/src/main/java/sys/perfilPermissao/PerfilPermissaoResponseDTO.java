package sys.perfilPermissao;

import sys.perfis.Perfis;
import sys.permissoes.Permissoes;

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
