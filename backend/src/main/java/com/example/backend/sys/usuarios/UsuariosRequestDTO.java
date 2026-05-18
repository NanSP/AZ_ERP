package com.example.backend.sys.usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuariosRequestDTO
        (
                String nome,
                String email,
                String login,
                String senha,
                String documento,
                String tipoUsuario,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDate expiracaoSenha,
                Integer tentativasLogin
        ) {
}
