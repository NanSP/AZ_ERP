package com.example.backend.master.platform.systemUsers;

import java.time.LocalDateTime;

public record SystemUsersRequestDTO
        (
                String nome,
                String email,
                String login,
                String senhaHash,
                String role,
                String status,
                LocalDateTime ultimoAcesso,
                LocalDateTime createdAt,
                LocalDateTime updatedAt
        ) {
}
