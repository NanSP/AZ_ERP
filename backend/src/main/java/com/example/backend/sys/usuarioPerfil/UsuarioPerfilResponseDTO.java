package com.example.backend.sys.usuarioPerfil;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.perfis.Perfis;

import java.time.LocalDateTime;

public record UsuarioPerfilResponseDTO
        (
                Integer id,
                Integer usuario,
                Integer perfil,
                LocalDateTime dataAtribuicao
        )
    {
        public UsuarioPerfilResponseDTO(UsuarioPerfil usuarioPerfil) {
            this(
                    usuarioPerfil.getId(),
                    usuarioPerfil.getUsuario() != null ? usuarioPerfil.getUsuario().getId() : null,
                    usuarioPerfil.getPerfil() != null ? usuarioPerfil.getPerfil().getId() : null,
                    usuarioPerfil.getDataAtribuicao()
            );
        }
}
