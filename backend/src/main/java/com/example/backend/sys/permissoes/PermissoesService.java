package com.example.backend.sys.permissoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfilPermissao.PerfilPermissaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PermissoesService {

    private final PermissoesRepository repository;
    private final PerfilPermissaoRepository perfilPermissaoRepository;

    public PermissoesService(
            PermissoesRepository repository,
            PerfilPermissaoRepository perfilPermissaoRepository
    ) {
        this.repository = repository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    @Transactional
    public Permissoes criar(PermissoesRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaCriacao(normalizarObrigatorio(data.nome(), "Nome da permissao e obrigatorio"));

        Permissoes entity = new Permissoes();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Permissoes atualizar(Integer id, PermissoesRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaAtualizacao(normalizarObrigatorio(data.nome(), "Nome da permissao e obrigatorio"), id);

        Permissoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Permissao nao encontrada"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Permissoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Permissao nao encontrada"));

        if (perfilPermissaoRepository.existsByPermissaoId(id)) {
            throw new ValidacaoException("Nao e permitido excluir permissao com vinculos ativos");
        }

        repository.delete(entity);
    }

    private void preencher(
            Permissoes entity,
            PermissoesRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome da permissao e obrigatorio"));
        entity.setDescricao(normalizarDescricao(data.descricao()));
        entity.setModulo(normalizarModulo(data.modulo()));
        entity.setRecurso(normalizarRecurso(data.recurso()));
        entity.setAcao(normalizarAcao(data.acao()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(PermissoesRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da permissao sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome da permissao e obrigatorio");
        normalizarModulo(data.modulo());
        normalizarRecurso(data.recurso());
        validarAcao(normalizarAcao(data.acao()));
        validarDescricao(normalizarDescricao(data.descricao()));
    }

    private void validarAlteracoesSensiveis(Permissoes entity, PermissoesRequestDTO data) {
        if (!perfilPermissaoRepository.existsByPermissaoId(entity.getId())) {
            return;
        }

        String novoNome = normalizarObrigatorio(data.nome(), "Nome da permissao e obrigatorio");
        String novoModulo = normalizarModulo(data.modulo());
        String novoRecurso = normalizarRecurso(data.recurso());
        String novaAcao = normalizarAcao(data.acao());
        String novaDescricao = normalizarDescricao(data.descricao());
        String descricaoAtual = normalizarDescricao(entity.getDescricao());

        if (!novoNome.equals(entity.getNome())) {
            throw new ValidacaoException("Nao e permitido alterar o nome de permissao ja vinculada");
        }

        if (!novoModulo.equals(entity.getModulo())) {
            throw new ValidacaoException("Nao e permitido alterar o modulo de permissao ja vinculada");
        }

        if (!novoRecurso.equals(entity.getRecurso())) {
            throw new ValidacaoException("Nao e permitido alterar o recurso de permissao ja vinculada");
        }

        if (!novaAcao.equals(entity.getAcao())) {
            throw new ValidacaoException("Nao e permitido alterar a acao de permissao ja vinculada");
        }

        if (descricaoAtual == null ? novaDescricao != null : !descricaoAtual.equals(novaDescricao)) {
            throw new ValidacaoException("Nao e permitido alterar a descricao de permissao ja vinculada");
        }
    }

    private void validarAcao(String acao) {
        if (!acao.equals("create")
                && !acao.equals("read")
                && !acao.equals("update")
                && !acao.equals("delete")
                && !acao.equals("execute")) {
            throw new ValidacaoException("Acao de permissao invalida");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 255) {
            throw new ValidacaoException("Descricao da permissao nao pode exceder 255 caracteres");
        }
    }

    private void validarNomeDuplicadoParaCriacao(String nome) {
        if (repository.existsByNome(nome)) {
            throw new ValidacaoException("Ja existe permissao com o nome informado");
        }
    }

    private void validarNomeDuplicadoParaAtualizacao(String nome, Integer id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new ValidacaoException("Ja existe permissao com o nome informado");
        }
    }

    private String normalizarAcao(String acao) {
        if (acao == null || acao.isBlank()) {
            throw new ValidacaoException("Acao e obrigatoria");
        }

        return acao.trim().toLowerCase();
    }

    private String normalizarModulo(String modulo) {
        String valor = normalizarObrigatorio(modulo, "Modulo e obrigatorio");
        return valor.toLowerCase();
    }

    private String normalizarRecurso(String recurso) {
        String valor = normalizarObrigatorio(recurso, "Recurso e obrigatorio");
        return valor.toLowerCase();
    }

    private String normalizarDescricao(String descricao) {
        return normalizarOpcional(descricao);
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
}
