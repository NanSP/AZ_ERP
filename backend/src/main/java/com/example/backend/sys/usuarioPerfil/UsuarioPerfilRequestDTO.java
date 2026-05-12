package com.example.backend.sys.usuarioPerfil;

import java.time.LocalDateTime;

public record UsuarioPerfilRequestDTO
        (
                Integer usuario,
                Integer perfil,
                LocalDateTime dataAtribuicao
        ) {
}
