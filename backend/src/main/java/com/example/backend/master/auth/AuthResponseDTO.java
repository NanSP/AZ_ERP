package com.example.backend.master.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthResponseDTO(
        @JsonIgnore
        String token,
        Long userId,
        String login,
        String role,
        String scope,
        boolean passwordChangeRequired
) {
}
