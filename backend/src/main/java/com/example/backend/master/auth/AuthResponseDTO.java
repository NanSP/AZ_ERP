package com.example.backend.master.auth;

public record AuthResponseDTO(
        String token,
        Long userId,
        String login,
        String role,
        String scope
) {
}
