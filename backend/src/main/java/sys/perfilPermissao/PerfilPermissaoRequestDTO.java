package sys.perfilPermissao;

import sys.perfis.Perfis;
import sys.permissoes.Permissoes;

public record PerfilPermissaoRequestDTO
        (
                Perfis perfilId,
                Permissoes permissaoId
        ) {
}
