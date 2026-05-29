package com.example.backend.sys.usuarioPerfil;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioPerfilService {

    private final UsuarioPerfilRepository repository;
    private final UsuariosRepository usuariosRepository;
    private final PerfisRepository perfisRepository;

    public UsuarioPerfilService(
            UsuarioPerfilRepository repository,
            UsuariosRepository usuariosRepository,
            PerfisRepository perfisRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
        this.perfisRepository = perfisRepository;
    }

    @Transactional
    public UsuarioPerfil criar(UsuarioPerfilRequestDTO data) {
        validar(data);
        validarDuplicidade(data.usuario(), data.perfil(), null);

        Usuarios usuario = buscarUsuario(data.usuario());
        Perfis perfil = buscarPerfil(data.perfil());

        UsuarioPerfil entity = new UsuarioPerfil();
        entity.setUsuario(usuario);
        entity.setPerfil(perfil);
        entity.setDataAtribuicao(LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public UsuarioPerfil atualizar(Integer id, UsuarioPerfilRequestDTO data) {
        validar(data);

        UsuarioPerfil entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo usuario-perfil nao encontrado"));

        validarAlteracaoVinculo(entity, data);
        validarDuplicidade(data.usuario(), data.perfil(), id);

        Usuarios usuario = buscarUsuario(data.usuario());
        Perfis perfil = buscarPerfil(data.perfil());

        entity.setUsuario(usuario);
        entity.setPerfil(perfil);
        entity.setDataAtribuicao(entity.getDataAtribuicao());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        UsuarioPerfil entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo usuario-perfil nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void validar(UsuarioPerfilRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do vinculo usuario-perfil sao obrigatorios");
        }

        if (data.usuario() == null) {
            throw new ValidacaoException("Usuario e obrigatorio");
        }

        if (data.perfil() == null) {
            throw new ValidacaoException("Perfil e obrigatorio");
        }
    }

    private void validarDuplicidade(Integer usuarioId, Integer perfilId, Integer idAtual) {
        boolean duplicado = idAtual == null
                ? repository.existsByUsuarioIdAndPerfilId(usuarioId, perfilId)
                : repository.existsByUsuarioIdAndPerfilIdAndIdNot(usuarioId, perfilId, idAtual);

        if (duplicado) {
            throw new ValidacaoException("Usuario ja possui o perfil informado");
        }
    }

    private void validarAlteracaoVinculo(UsuarioPerfil entity, UsuarioPerfilRequestDTO data) {
        Integer usuarioAtual = entity.getUsuario() != null ? entity.getUsuario().getId() : null;
        Integer perfilAtual = entity.getPerfil() != null ? entity.getPerfil().getId() : null;

        if (!usuarioAtual.equals(data.usuario()) || !perfilAtual.equals(data.perfil())) {
            throw new ValidacaoException("Nao e permitido alterar o vinculo usuario-perfil apos a atribuicao");
        }
    }

    private void validarExclusao(UsuarioPerfil entity) {
        Integer usuarioId = entity.getUsuario() != null ? entity.getUsuario().getId() : null;

        if (usuarioId != null && !repository.existsByUsuarioIdAndIdNot(usuarioId, entity.getId())) {
            throw new ValidacaoException("Nao e permitido remover o ultimo perfil do usuario");
        }
    }

    private Usuarios buscarUsuario(Integer usuarioId) {
        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }

    private Perfis buscarPerfil(Integer perfilId) {
        return perfisRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));
    }
}
