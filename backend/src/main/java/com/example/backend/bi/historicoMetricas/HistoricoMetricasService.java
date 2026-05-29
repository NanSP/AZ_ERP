package com.example.backend.bi.historicoMetricas;

import com.example.backend.bi.metricas.Metricas;
import com.example.backend.bi.metricas.MetricasRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class HistoricoMetricasService {

    private final HistoricoMetricasRepository repository;
    private final MetricasRepository metricasRepository;

    public HistoricoMetricasService(
            HistoricoMetricasRepository repository,
            MetricasRepository metricasRepository
    ) {
        this.repository = repository;
        this.metricasRepository = metricasRepository;
    }

    @Transactional
    public HistoricoMetricas criar(HistoricoMetricasRequestDTO data) {
        validar(data);
        validarDuplicidade(data.metrica(), data.periodo(), null);

        Metricas metrica = buscarMetrica(data.metrica());
        validarRelacionamentoComMetrica(metrica);

        HistoricoMetricas entity = new HistoricoMetricas();
        preencher(entity, data, metrica, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public HistoricoMetricas atualizar(Long id, HistoricoMetricasRequestDTO data) {
        validar(data);

        HistoricoMetricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Historico de metrica nao encontrado"));

        validarAlteracoesSensiveis(entity, data);
        validarDuplicidade(data.metrica(), data.periodo(), id);

        Metricas metrica = buscarMetrica(data.metrica());
        validarRelacionamentoComMetrica(metrica);
        preencher(entity, data, metrica, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        HistoricoMetricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Historico de metrica nao encontrado"));

        throw new ValidacaoException("Historico de metrica nao pode ser excluido");
    }

    private void preencher(
            HistoricoMetricas entity,
            HistoricoMetricasRequestDTO data,
            Metricas metrica,
            LocalDateTime createdAt
    ) {
        entity.setMetrica(metrica);
        entity.setPeriodo(data.periodo());
        entity.setValorApurado(data.valorApurado());
        entity.setCreatedAt(createdAt);
    }

    private void validar(HistoricoMetricasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do historico da metrica sao obrigatorios");
        }

        if (data.metrica() == null) {
            throw new ValidacaoException("Metrica e obrigatoria");
        }

        if (data.periodo() == null) {
            throw new ValidacaoException("Periodo e obrigatorio");
        }

        validarPeriodo(data.periodo());
        validarValorApurado(data.valorApurado());
    }

    private void validarAlteracoesSensiveis(HistoricoMetricas entity, HistoricoMetricasRequestDTO data) {
        Integer metricaAtual = entity.getMetrica() != null ? entity.getMetrica().getId() : null;
        Integer novaMetrica = data.metrica();
        LocalDate novoPeriodo = data.periodo();

        if (!metricaAtual.equals(novaMetrica)) {
            throw new ValidacaoException("Nao e permitido alterar a metrica do historico");
        }

        if (!entity.getPeriodo().equals(novoPeriodo)) {
            throw new ValidacaoException("Nao e permitido alterar o periodo do historico");
        }

        if (entity.getValorApurado() == null
                ? data.valorApurado() != null
                : data.valorApurado() == null || entity.getValorApurado().compareTo(data.valorApurado()) != 0) {
            throw new ValidacaoException("Nao e permitido alterar o valor apurado do historico");
        }
    }

    private void validarDuplicidade(Integer metricaId, LocalDate periodo, Long idAtual) {
        boolean duplicado = idAtual == null
                ? repository.existsByMetricaIdAndPeriodo(metricaId, periodo)
                : repository.existsByMetricaIdAndPeriodoAndIdNot(metricaId, periodo, idAtual);

        if (duplicado) {
            throw new ValidacaoException("Ja existe historico para a metrica no periodo informado");
        }
    }

    private void validarPeriodo(LocalDate periodo) {
        if (periodo.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Periodo nao pode estar no futuro");
        }
    }

    private void validarValorApurado(BigDecimal valorApurado) {
        if (valorApurado == null) {
            throw new ValidacaoException("Valor apurado e obrigatorio");
        }

        if (valorApurado.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Valor apurado nao pode ser negativo");
        }
    }

    private Metricas buscarMetrica(Integer metricaId) {
        return metricasRepository.findById(metricaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Metrica nao encontrada"));
    }

    private void validarRelacionamentoComMetrica(Metricas metrica) {
        if (metrica == null) {
            return;
        }

        String categoria = metrica.getCategoria();

        if (("financeira".equals(categoria) || "operacional".equals(categoria))
                && (metrica.getFormula() == null || metrica.getFormula().isBlank())) {
            throw new ValidacaoException("Nao e permitido registrar historico para metrica sem formula na categoria informada");
        }

        if ("estrategica".equals(categoria) && metrica.getMeta() == null) {
            throw new ValidacaoException("Nao e permitido registrar historico para metrica estrategica sem meta definida");
        }
    }
}
