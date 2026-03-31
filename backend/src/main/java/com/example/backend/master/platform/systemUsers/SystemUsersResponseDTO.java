package com.example.backend.master.platform.systemUsers;

import java.time.LocalDateTime;

public record SystemUsersResponseDTO
        (
                Integer id,
                String nome,
                String email,
                String login,
                String senhaHash,
                String role,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        )
    {
        public SystemUsersResponseDTO(SystemUsers systemUsers) {
            this
                    (
                            systemUsers.getId(),
                            systemUsers.getNome(),
                            systemUsers.getEmail(),
                            systemUsers.getLogin(),
                            systemUsers.getSenhaHash(),
                            systemUsers.getRole(),
                            systemUsers.getStatus(),
                            systemUsers.getUltimoAcesso(),
                            systemUsers.getCreatedAt(),
                            systemUsers.getUpdatedAt()
                    );
        }
}
