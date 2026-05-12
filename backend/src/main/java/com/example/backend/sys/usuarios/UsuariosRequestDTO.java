package com.example.backend.sys.usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UsuariosRequestDTO
        (
                String nome,
                String email,
                String login,
                String senhaHash,
                String documento,
                String tipoUsuario,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDate expiracaoSenha,
                Integer tentativasLogin,
                LocalDateTime createdAt
        ) {
}
