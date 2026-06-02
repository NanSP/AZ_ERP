package com.example.backend.auth;

public record ChangePasswordRequestDTO(
        String senhaAtual,
        String novaSenha
) {
}
