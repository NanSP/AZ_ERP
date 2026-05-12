package com.example.backend.sys.usuarioPerfil;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.perfis.Perfis;

import java.time.LocalDateTime;

public record UsuarioPerfilResponseDTO
        (
                Integer id,
                Integer usuarioId,
                Integer perfilId,
                LocalDateTime dataAtribuicao
        )
    {
        public UsuarioPerfilResponseDTO(UsuarioPerfil usuarioPerfil) {
            this(
                    usuarioPerfil.getId(),
                    usuarioPerfil.getUsuarioId() != null ? usuarioPerfil.getUsuarioId().getId() : null,
                    usuarioPerfil.getPerfilId() != null ? usuarioPerfil.getPerfilId().getId() : null,
                    usuarioPerfil.getDataAtribuicao()
            );
        }
}
