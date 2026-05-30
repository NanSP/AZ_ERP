package com.example.backend.master.platform.systemUsers;

public record SystemUsersRequestDTO
        (
                String nome,
                String email,
                String login,
                String senha,
                String role,
                String status
        ) {
}
