package com.example.backend.master.auth;

public record AuthRequestDTO(
        String login,
        String senha
) {
}
