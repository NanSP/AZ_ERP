package com.example.backend.sys.usuarioPerfil;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.perfis.Perfis;

import java.time.LocalDateTime;

public record UsuarioPerfilRequestDTO
        (
                Integer usuarioId,
                Integer perfilId,
                LocalDateTime dataAtribuicao
        ) {
}
