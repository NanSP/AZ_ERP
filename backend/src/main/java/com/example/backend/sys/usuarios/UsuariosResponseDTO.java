package com.example.backend.sys.usuarios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UsuariosResponseDTO
        (
                Integer id,
                String nome,
                String email,
                String login,
                String documento,
                String tipoUsuario,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDate expiracaoSenha,
                Integer tentativasLogin,

                LocalDateTime createdAt
        )
    {
        public UsuariosResponseDTO(Usuarios usuarios) {
            this(
                    usuarios.getId(),
                    usuarios.getNome(),
                    usuarios.getEmail(),
                    usuarios.getLogin(),
                    usuarios.getDocumento(),
                    usuarios.getTipoUsuario(),
                    usuarios.getStatus(),
                    usuarios.getUltimoAcesso(),
                    usuarios.getExpiracaoSenha(),
                    usuarios.getTentativasLogin(),
                    usuarios.getCreatedAt()
            );
        }
}
