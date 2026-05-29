package com.example.backend.bi.metricas;

import com.example.backend.bi.historicoMetricas.HistoricoMetricasRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MetricasService {

    private final MetricasRepository repository;
    private final HistoricoMetricasRepository historicoMetricasRepository;

    public MetricasService(
            MetricasRepository repository,
            HistoricoMetricasRepository historicoMetricasRepository
    ) {
        this.repository = repository;
        this.historicoMetricasRepository = historicoMetricasRepository;
    }

    @Transactional
    public Metricas criar(MetricasRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaCriacao(normalizarObrigatorio(data.nome(), "Nome da metrica e obrigatorio"));

        Metricas entity = new Metricas();
        preencher(entity, data, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Metricas atualizar(Integer id, MetricasRequestDTO data) {
        validar(data);
        validarNomeDuplicadoParaAtualizacao(normalizarObrigatorio(data.nome(), "Nome da metrica e obrigatorio"), id);

        Metricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Metrica nao encontrada"));

        validarAlteracoesSensiveis(entity, data);
        preencher(entity, data, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Metricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Metrica nao encontrada"));

        if (historicoMetricasRepository.existsByMetricaId(id)) {
            throw new ValidacaoException("Nao e permitido excluir metrica com historico registrado");
        }

        repository.delete(entity);
    }

    private void preencher(
            Metricas entity,
            MetricasRequestDTO data,
            LocalDateTime createdAt
    ) {
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome da metrica e obrigatorio"));
        entity.setDescricao(normalizarDescricao(data.descricao()));
        entity.setCategoria(normalizarCategoria(data.categoria()));
        entity.setFormula(normalizarFormula(data.formula()));
        entity.setUnidadeMedida(normalizarUnidadeMedida(data.unidadeMedida()));
        entity.setMeta(data.meta());
        entity.setCreatedAt(createdAt);
    }

    private void validar(MetricasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da metrica sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome da metrica e obrigatorio");
        String categoria = normalizarCategoria(data.categoria());
        String formula = normalizarFormula(data.formula());
        validarDescricao(normalizarDescricao(data.descricao()));
        validarCategoria(categoria);
        validarFormula(formula);
        validarFormulaObrigatoriaPorCategoria(categoria, formula);
        validarUnidadeMedida(normalizarUnidadeMedida(data.unidadeMedida()));
        validarMeta(data.meta());
    }

    private void validarAlteracoesSensiveis(Metricas entity, MetricasRequestDTO data) {
        if (!historicoMetricasRepository.existsByMetricaId(entity.getId())) {
            return;
        }

        String novoNome = normalizarObrigatorio(data.nome(), "Nome da metrica e obrigatorio");
        String novaCategoria = normalizarCategoria(data.categoria());
        String novaFormula = normalizarFormula(data.formula());
        String novaUnidade = normalizarUnidadeMedida(data.unidadeMedida());
        BigDecimal novaMeta = data.meta();

        if (!novoNome.equals(entity.getNome())) {
            throw new ValidacaoException("Nao e permitido alterar o nome de metrica com historico");
        }

        if (!novaCategoria.equals(entity.getCategoria())) {
            throw new ValidacaoException("Nao e permitido alterar a categoria de metrica com historico");
        }

        if (entity.getFormula() == null ? novaFormula != null : !entity.getFormula().equals(novaFormula)) {
            throw new ValidacaoException("Nao e permitido alterar a formula de metrica com historico");
        }

        if (entity.getUnidadeMedida() == null ? novaUnidade != null : !entity.getUnidadeMedida().equals(novaUnidade)) {
            throw new ValidacaoException("Nao e permitido alterar a unidade de medida de metrica com historico");
        }

        if (entity.getMeta() == null ? novaMeta != null : novaMeta == null || entity.getMeta().compareTo(novaMeta) != 0) {
            throw new ValidacaoException("Nao e permitido alterar a meta de metrica com historico");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao != null && descricao.length() > 500) {
            throw new ValidacaoException("Descricao da metrica nao pode exceder 500 caracteres");
        }
    }

    private void validarCategoria(String categoria) {
        if (!categoria.equals("financeira")
                && !categoria.equals("operacional")
                && !categoria.equals("comercial")
                && !categoria.equals("qualidade")
                && !categoria.equals("estrategica")) {
            throw new ValidacaoException("Categoria de metrica invalida");
        }
    }

    private void validarFormula(String formula) {
        if (formula != null && formula.length() > 1000) {
            throw new ValidacaoException("Formula da metrica nao pode exceder 1000 caracteres");
        }
    }

    private void validarUnidadeMedida(String unidadeMedida) {
        if (unidadeMedida != null && unidadeMedida.length() > 20) {
            throw new ValidacaoException("Unidade de medida nao pode exceder 20 caracteres");
        }
    }

    private void validarMeta(BigDecimal meta) {
        if (meta != null && meta.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Meta da metrica nao pode ser negativa");
        }
    }

    private void validarFormulaObrigatoriaPorCategoria(String categoria, String formula) {
        if ((categoria.equals("financeira") || categoria.equals("operacional"))
                && (formula == null || formula.isBlank())) {
            throw new ValidacaoException("Formula da metrica e obrigatoria para a categoria informada");
        }
    }

    private void validarNomeDuplicadoParaCriacao(String nome) {
        if (repository.existsByNome(nome)) {
            throw new ValidacaoException("Ja existe metrica com o nome informado");
        }
    }

    private void validarNomeDuplicadoParaAtualizacao(String nome, Integer id) {
        if (repository.existsByNomeAndIdNot(nome, id)) {
            throw new ValidacaoException("Ja existe metrica com o nome informado");
        }
    }

    private String normalizarCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new ValidacaoException("Categoria da metrica e obrigatoria");
        }

        return categoria.trim().toLowerCase();
    }

    private String normalizarFormula(String formula) {
        return normalizarOpcional(formula);
    }

    private String normalizarUnidadeMedida(String unidadeMedida) {
        return normalizarOpcional(unidadeMedida);
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
