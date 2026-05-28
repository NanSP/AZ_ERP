package com.example.backend.portal.dispositivos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DispositivosService {

    private final DispositivosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public DispositivosService(
            DispositivosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Dispositivos criar(DispositivosRequestDTO data) {
        validar(data);
        validarDeviceIdDuplicadoParaCriacao(normalizarObrigatorio(data.deviceId(), "Device ID e obrigatorio"));
        validarPushTokenDuplicadoParaCriacao(data);

        Usuarios usuario = buscarUsuario(data.usuario());

        Dispositivos entity = new Dispositivos();
        preencher(entity, data, usuario, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Dispositivos atualizar(Integer id, DispositivosRequestDTO data) {
        validar(data);
        validarDeviceIdDuplicadoParaAtualizacao(normalizarObrigatorio(data.deviceId(), "Device ID e obrigatorio"), id);
        validarPushTokenDuplicadoParaAtualizacao(data, id);

        Dispositivos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dispositivo nao encontrado"));

        Usuarios usuario = buscarUsuario(data.usuario());

        preencher(entity, data, usuario, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Dispositivos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dispositivo nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Dispositivos entity,
            DispositivosRequestDTO data,
            Usuarios usuario,
            LocalDateTime createdAt
    ) {
        entity.setUsuario(usuario);
        entity.setDeviceId(normalizarObrigatorio(data.deviceId(), "Device ID e obrigatorio"));
        entity.setDeviceModel(normalizarOpcional(data.deviceModel()));
        entity.setDevicePlatform(normalizarPlatform(data.devicePlatform()));
        entity.setPushToken(normalizarOpcional(data.pushToken()));
        entity.setUltimoAcesso(data.ultimoAcesso());
        entity.setAtivo(normalizarAtivo(data.ativo()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(DispositivosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do dispositivo sao obrigatorios");
        }

        if (data.usuario() == null) {
            throw new ValidacaoException("Usuario e obrigatorio");
        }

        normalizarObrigatorio(data.deviceId(), "Device ID e obrigatorio");
        if (normalizarPlatform(data.devicePlatform()) == null) {
            throw new ValidacaoException("Platform e obrigatoria");
        }

        validarPlatform(normalizarPlatform(data.devicePlatform()));
    }

    private void validarPlatform(String platform) {
        if (platform == null) {
            return;
        }

        if (!platform.equals("android")
                && !platform.equals("ios")
                && !platform.equals("web")) {
            throw new ValidacaoException("Platform invalida");
        }
    }

    private void validarDeviceIdDuplicadoParaCriacao(String deviceId) {
        if (repository.existsByDeviceId(deviceId)) {
            throw new ValidacaoException("Ja existe dispositivo com o device ID informado");
        }
    }

    private void validarDeviceIdDuplicadoParaAtualizacao(String deviceId, Integer id) {
        if (repository.existsByDeviceIdAndIdNot(deviceId, id)) {
            throw new ValidacaoException("Ja existe dispositivo com o device ID informado");
        }
    }

    private void validarPushTokenDuplicadoParaCriacao(DispositivosRequestDTO data) {
        String pushToken = normalizarOpcional(data.pushToken());
        if (pushToken != null && Boolean.TRUE.equals(normalizarAtivo(data.ativo()))
                && repository.existsByPushTokenAndAtivoTrue(pushToken)) {
            throw new ValidacaoException("Ja existe dispositivo ativo com o push token informado");
        }
    }

    private void validarPushTokenDuplicadoParaAtualizacao(DispositivosRequestDTO data, Integer id) {
        String pushToken = normalizarOpcional(data.pushToken());
        if (pushToken != null && Boolean.TRUE.equals(normalizarAtivo(data.ativo()))
                && repository.existsByPushTokenAndAtivoTrueAndIdNot(pushToken, id)) {
            throw new ValidacaoException("Ja existe dispositivo ativo com o push token informado");
        }
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private Boolean normalizarAtivo(Boolean ativo) {
        return ativo == null || ativo;
    }

    private String normalizarPlatform(String platform) {
        String valor = normalizarOpcional(platform);
        return valor == null ? null : valor.toLowerCase();
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }

    private void validarExclusao(Dispositivos entity) {
        if (Boolean.TRUE.equals(entity.getAtivo())) {
            throw new ValidacaoException("Nao e permitido excluir dispositivo ativo");
        }

        if (entity.getUltimoAcesso() != null) {
            throw new ValidacaoException("Nao e permitido excluir dispositivo com historico de acesso");
        }
    }
}
