package com.example.backend.master.auth;

import com.example.backend.auth.AuthSessionResponseDTO;
import com.example.backend.auth.ChangePasswordRequestDTO;
import com.example.backend.auth.PasswordChangeResponseDTO;
import com.example.backend.master.platform.systemUsers.SystemUsers;
import com.example.backend.master.platform.systemUsers.SystemUsersRepository;
import com.example.backend.security.JwtService;
import com.example.backend.security.SecurityUserPrincipal;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
                "master",
                user.isPasswordChangeRequired()
        );
    }

    @Transactional
    public PasswordChangeResponseDTO changePassword(SecurityUserPrincipal principal, ChangePasswordRequestDTO data) {
        validarTrocaSenha(data);

        SystemUsers user = systemUsersRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ValidacaoException("Usuario autenticado nao encontrado"));

        if (!passwordEncoder.matches(data.senhaAtual(), user.getSenha())) {
            throw new ValidacaoException("Senha atual invalida");
        }

        if (passwordEncoder.matches(data.novaSenha(), user.getSenha())) {
            throw new ValidacaoException("Nova senha deve ser diferente da senha atual");
        }

        user.setSenha(passwordEncoder.encode(data.novaSenha()));
        user.setPasswordChangeRequired(false);
        user.setPasswordChangedAt(LocalDateTime.now());
        systemUsersRepository.save(user);

        return new PasswordChangeResponseDTO("Senha alterada com sucesso");
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

    private void validarTrocaSenha(ChangePasswordRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados de troca de senha sao obrigatorios");
        }

        if (data.senhaAtual() == null || data.senhaAtual().isBlank()) {
            throw new ValidacaoException("Senha atual e obrigatoria");
        }

        if (data.novaSenha() == null || data.novaSenha().isBlank()) {
            throw new ValidacaoException("Nova senha e obrigatoria");
        }

        if (data.novaSenha().trim().length() < 8) {
            throw new ValidacaoException("Nova senha deve ter pelo menos 8 caracteres");
        }
    }

    public AuthSessionResponseDTO me(SecurityUserPrincipal principal) {
        SystemUsers user = systemUsersRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ValidacaoException("Usuario autenticado nao encontrado"));

        return new AuthSessionResponseDTO(
                "master",
                user.getLogin(),
                user.getId(),
                user.getRole(),
                null,
                null,
                List.of(),
                List.of(),
                user.isPasswordChangeRequired()
        );
    }

}
