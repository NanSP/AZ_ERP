package com.example.backend.sys.perfilPermissao;

import com.example.backend.sys.permissoes.Permissoes;
import com.example.backend.sys.perfis.Perfis;

public record PerfilPermissaoRequestDTO
        (
                Perfis perfilId,
                Permissoes permissaoId
        ) {
}
