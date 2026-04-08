package com.example.backend.master.auth;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MasterAuthService {

    private final SystemUsersRepository systemUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public MasterAuthService(
            SystemUsersRepository systemUsersRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.systemUsersRepository = systemUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(AuthRequestDTO data) {
        SystemUsers user = systemUsersRepository.findByLogin(data.login())
                .orElseThrow(() -> new RuntimeException("Login ou senha invalidos"));

        if (!"ATIVO".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Usuario inativo");
        }

        if (!passwordEncoder.matches(data.senha(), user.getSenha())) {
            throw new RuntimeException("Login ou senha invalidos");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getLogin(),
                user.getRole(),
                "master"
        );

        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getLogin(),
                user.getRole(),
                "master"
        );
    }
}
