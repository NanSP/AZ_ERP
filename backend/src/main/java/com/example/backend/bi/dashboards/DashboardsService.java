package com.example.backend.bi.dashboards;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DashboardsService {

    private final DashboardsRepository repository;

    public DashboardsService(DashboardsRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Dashboards criar(DashboardsRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaCriacao(normalizarObrigatorio(data.nome(), "Nome do dashboard e obrigatorio"));

        Dashboards entity = new Dashboards();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Dashboards atualizar(Integer id, DashboardsRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaAtualizacao(normalizarObrigatorio(data.nome(), "Nome do dashboard e obrigatorio"), id);

        Dashboards entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dashboard nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Dashboards entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dashboard nao encontrado"));

        throw new ValidacaoException("Dashboard nao pode ser excluido");
    }

    private void preencher(
            Dashboards entity,
            DashboardsRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do dashboard e obrigatorio"));
        entity.setDescricao(normalizarDescricao(data.descricao()));
        entity.setLayout(normalizarMapa(data.layout()));
        entity.setConfiguracoes(normalizarMapa(data.configuracoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(DashboardsRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do dashboard sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome do dashboard e obrigatorio");
        validarDescricao(normalizarDescricao(data.descricao()));
        validarMapa("layout", data.layout());
        validarMapa("configuracoes", data.configuracoes());
    }

    private void validarMapa(String campo, Map<String, Object> valor) {
        if (valor == null) {
            return;
        }

        if (valor.size() > 100) {
            throw new ValidacaoException("Campo " + campo + " excede o limite permitido");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 500) {
            throw new ValidacaoException("Descricao do dashboard nao pode exceder 500 caracteres");
        }
    }

    private void validarNomeDuplicadoParaCriacao(String nome) {
        if (repository.existsByNome(nome)) {
            throw new ValidacaoException("Ja existe dashboard com o nome informado");
        }
    }

    private void validarNomeDuplicadoParaAtualizacao(String nome, Integer id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new ValidacaoException("Ja existe dashboard com o nome informado");
        }
    }

    private Map<String, Object> normalizarMapa(Map<String, Object> valor) {
        return valor == null || valor.isEmpty() ? null : valor;
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
