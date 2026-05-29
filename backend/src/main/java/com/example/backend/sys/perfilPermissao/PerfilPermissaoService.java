package com.example.backend.sys.perfilPermissao;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
import com.example.backend.sys.permissoes.Permissoes;
import com.example.backend.sys.permissoes.PermissoesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PerfilPermissaoService {

    private final PerfilPermissaoRepository repository;
    private final PerfisRepository perfisRepository;
    private final PermissoesRepository permissoesRepository;

    public PerfilPermissaoService(
            PerfilPermissaoRepository repository,
            PerfisRepository perfisRepository,
            PermissoesRepository permissoesRepository
    ) {
        this.repository = repository;
        this.perfisRepository = perfisRepository;
        this.permissoesRepository = permissoesRepository;
    }

    @Transactional
    public PerfilPermissao criar(PerfilPermissaoRequestDTO data) {
        validar(data);
        validarDuplicidade(data.perfil(), data.permissao(), null);

        Perfis perfil = buscarPerfil(data.perfil());
        Permissoes permissao = buscarPermissao(data.permissao());

        PerfilPermissao entity = new PerfilPermissao();
        entity.setPerfil(perfil);
        entity.setPermissao(permissao);

        return repository.save(entity);
    }

    @Transactional
    public PerfilPermissao atualizar(Integer id, PerfilPermissaoRequestDTO data) {
        validar(data);

        PerfilPermissao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo perfil-permissao nao encontrado"));

        validarAlteracaoVinculo(entity, data);
        validarDuplicidade(data.perfil(), data.permissao(), id);

        Perfis perfil = buscarPerfil(data.perfil());
        Permissoes permissao = buscarPermissao(data.permissao());

        entity.setPerfil(perfil);
        entity.setPermissao(permissao);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        PerfilPermissao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo perfil-permissao nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void validar(PerfilPermissaoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do vinculo perfil-permissao sao obrigatorios");
        }

        if (data.perfil() == null) {
            throw new ValidacaoException("Perfil e obrigatorio");
        }

        if (data.permissao() == null) {
            throw new ValidacaoException("Permissao e obrigatoria");
        }
    }

    private void validarDuplicidade(Integer perfilId, Integer permissaoId, Integer idAtual) {
        boolean duplicado = idAtual == null
                ? repository.existsByPerfilIdAndPermissaoId(perfilId, permissaoId)
                : repository.existsByPerfilIdAndPermissaoIdAndIdNot(perfilId, permissaoId, idAtual);

        if (duplicado) {
            throw new ValidacaoException("Perfil ja possui a permissao informada");
        }
    }

    private void validarAlteracaoVinculo(PerfilPermissao entity, PerfilPermissaoRequestDTO data) {
        Integer perfilAtual = entity.getPerfil() != null ? entity.getPerfil().getId() : null;
        Integer permissaoAtual = entity.getPermissao() != null ? entity.getPermissao().getId() : null;

        if (!perfilAtual.equals(data.perfil()) || !permissaoAtual.equals(data.permissao())) {
            throw new ValidacaoException("Nao e permitido alterar o vinculo perfil-permissao apos a atribuicao");
        }
    }

    private void validarExclusao(PerfilPermissao entity) {
        Integer perfilId = entity.getPerfil() != null ? entity.getPerfil().getId() : null;

        if (perfilId != null && !repository.existsByPerfilIdAndIdNot(perfilId, entity.getId())) {
            throw new ValidacaoException("Nao e permitido remover a ultima permissao do perfil");
        }
    }

    private Perfis buscarPerfil(Integer perfilId) {
        return perfisRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil nao encontrado"));
    }

    private Permissoes buscarPermissao(Integer permissaoId) {
        return permissoesRepository.findById(permissaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Permissao nao encontrada"));
    }
}