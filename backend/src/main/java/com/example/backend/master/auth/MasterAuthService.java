package com.example.backend.master.auth;

import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.security.JwtService;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO data) {
        validar(data);

        SystemUsers user = systemUsersRepository.findByLogin(normalizarLogin(data.login()))
                .orElseThrow(() -> new ValidacaoException("Login ou senha invalidos"));

        validarStatus(user);

        if (!passwordEncoder.matches(data.senha(), user.getSenha())) {
            throw new ValidacaoException("Login ou senha invalidos");
        }

        user.setUltimoAcesso(LocalDateTime.now());
        systemUsersRepository.save(user);

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

    private void validar(AuthRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados de autenticacao sao obrigatorios");
        }

        if (data.login() == null || data.login().isBlank()) {
            throw new ValidacaoException("Login e obrigatorio");
        }

        if (data.senha() == null || data.senha().isBlank()) {
            throw new ValidacaoException("Senha e obrigatoria");
        }
    }

    private void validarStatus(SystemUsers user) {
        if ("SUSPENSO".equalsIgnoreCase(user.getStatus())) {
            throw new ValidacaoException("Usuario suspenso");
        }

        if (!"ATIVO".equalsIgnoreCase(user.getStatus())) {
            throw new ValidacaoException("Usuario inativo");
        }
    }

    private String normalizarLogin(String login) {
        return login.trim().toLowerCase();
    }
}
