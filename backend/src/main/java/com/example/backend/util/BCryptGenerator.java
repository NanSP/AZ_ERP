package com.example.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String senha = "user123";
        String hash = encoder.encode(senha);

        System.out.println("Senha: " + senha);
        System.out.println("Hash: " + hash);
    }
}
