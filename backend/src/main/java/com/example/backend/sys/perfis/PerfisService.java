package com.example.backend.sys.perfis;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfilPermissao.PerfilPermissaoRepository;
import com.example.backend.sys.usuarioPerfil.UsuarioPerfilRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PerfisService {

    private final PerfisRepository repository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;
    private final PerfilPermissaoRepository perfilPermissaoRepository;

    public PerfisService(
            PerfisRepository repository,
            UsuarioPerfilRepository usuarioPerfilRepository,
            PerfilPermissaoRepository perfilPermissaoRepository
    ) {
        this.repository = repository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    @Transactional
    public Perfis criar(PerfisRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaCriacao(normalizarObrigatorio(data.nome(), "Nome do perfil e obrigatorio"));

        Perfis entity = new Perfis();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Perfis atualizar(Integer id, PerfisRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaAtualizacao(normalizarObrigatorio(data.nome(), "Nome do perfil e obrigatorio"), id);

        Perfis entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Perfis entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));

        if (usuarioPerfilRepository.existsByPerfilId(id)
                || perfilPermissaoRepository.existsByPerfilId(id)) {
            throw new ValidacaoException("Nao e permitido excluir perfil com vinculos ativos");
        }

        repository.delete(entity);
    }

    private void preencher(
            Perfis entity,
            PerfisRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do perfil e obrigatorio"));
        entity.setDescricao(normalizarDescricao(data.descricao()));
        entity.setNivelAcesso(normalizarNivelAcesso(data.nivelAcesso()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(PerfisRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do perfil sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome do perfil e obrigatorio");
        validarDescricao(normalizarDescricao(data.descricao()));

        if (normalizarNivelAcesso(data.nivelAcesso()) < 0) {
            throw new ValidacaoException("Nivel de acesso nao pode ser negativo");
        }
    }

    private void validarAlteracoesSensiveis(Perfis entity, PerfisRequestDTO data) {
        if (!perfilEmUso(entity.getId())) {
            return;
        }

        String novoNome = normalizarObrigatorio(data.nome(), "Nome do perfil e obrigatorio");
        Integer novoNivelAcesso = normalizarNivelAcesso(data.nivelAcesso());

        if (!novoNome.equals(entity.getNome())) {
            throw new ValidacaoException("Nao e permitido alterar o nome de perfil ja vinculado a usuarios");
        }

        if (!novoNivelAcesso.equals(entity.getNivelAcesso())) {
            throw new ValidacaoException("Nao e permitido alterar o nivel de acesso de perfil ja vinculado");
        }
    }

    private boolean perfilEmUso(Integer perfilId) {
        return usuarioPerfilRepository.existsByPerfilId(perfilId)
                || perfilPermissaoRepository.existsByPerfilId(perfilId);
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new ValidacaoException("Descricao do perfil nao pode exceder 255 caracteres");
        }
    }

    private void validarNomeDuplicadoParaCriacao(String nome) {
        if (repository.existsByNome(nome)) {
            throw new ValidacaoException("Ja existe perfil com o nome informado");
        }
    }

    private void validarNomeDuplicadoParaAtualizacao(String nome, Integer id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new ValidacaoException("Ja existe perfil com o nome informado");
        }
    }

    private Integer normalizarNivelAcesso(Integer nivelAcesso) {
        return nivelAcesso == null ? 1 : nivelAcesso;
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

    private String normalizarDescricao(String descricao) {
        return normalizarOpcional(descricao);
    }
}
