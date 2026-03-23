package com.example.backend.sys.usuarioPerfil;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.perfis.Perfis;

import java.time.LocalDateTime;

public record UsuarioPerfilResponseDTO
        (
                Integer id,
                Usuarios usuarioId,
                Perfis perfilId,
                LocalDateTime dataAtribuicao
        )
    {
        public UsuarioPerfilResponseDTO(UsuarioPerfil usuarioPerfil) {
            this(
                    usuarioPerfil.getId(),
                    usuarioPerfil.getUsuarioId(),
                    usuarioPerfil.getPerfilId(),
                    usuarioPerfil.getDataAtribuicao()
            );
        }
}
