package com.example.backend.sm.slaConfig;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SlaConfigService {

    private final SlaConfigRepository repository;

    public SlaConfigService(SlaConfigRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SlaConfig criar(SlaConfigRequestDTO data) {
        validar(data);
        validarDuplicidadeParaCriacao(
                normalizarObrigatorio(data.tipoServico(), "Tipo de servico e obrigatorio"),
                normalizarPrioridade(data.prioridade())
        );

        SlaConfig entity = new SlaConfig();
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public SlaConfig atualizar(Integer id, SlaConfigRequestDTO data) {
        validar(data);
        validarDuplicidadeParaAtualizacao(
                normalizarObrigatorio(data.tipoServico(), "Tipo de servico e obrigatorio"),
                normalizarPrioridade(data.prioridade()),
                id
        );

        SlaConfig entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configuracao de SLA nao encontrada"));

        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        SlaConfig entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Configuracao de SLA nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(SlaConfig entity, SlaConfigRequestDTO data) {
        entity.setTipoServico(normalizarObrigatorio(data.tipoServico(), "Tipo de servico e obrigatorio"));
        entity.setPrioridade(normalizarPrioridade(data.prioridade()));
        entity.setTempoAtendimentoHoras(data.tempoAtendimentoHoras());
        entity.setTempoResolucaoHoras(data.tempoResolucaoHoras());
    }

    private void validar(SlaConfigRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da configuracao de SLA sao obrigatorios");
        }

        normalizarObrigatorio(data.tipoServico(), "Tipo de servico e obrigatorio");

        if (data.tempoAtendimentoHoras() != null && data.tempoAtendimentoHoras() < 0) {
            throw new ValidacaoException("Tempo de atendimento nao pode ser negativo");
        }

        if (data.tempoResolucaoHoras() != null && data.tempoResolucaoHoras() < 0) {
            throw new ValidacaoException("Tempo de resolucao nao pode ser negativo");
        }

        validarPrioridade(normalizarPrioridade(data.prioridade()));
    }

    private void validarPrioridade(String prioridade) {
        if (!prioridade.equals("baixa")
                && !prioridade.equals("normal")
                && !prioridade.equals("alta")
                && !prioridade.equals("critica")) {
            throw new ValidacaoException("Prioridade invalida");
        }
    }

    private void validarDuplicidadeParaCriacao(String tipoServico, String prioridade) {
        if (repository.existsByTipoServicoAndPrioridade(tipoServico, prioridade)) {
            throw new ValidacaoException("Ja existe configuracao de SLA para o tipo de servico e prioridade informados");
        }
    }

    private void validarDuplicidadeParaAtualizacao(String tipoServico, String prioridade, Integer id) {
        if (repository.existsByTipoServicoAndPrioridadeAndIdNot(tipoServico, prioridade, id)) {
            throw new ValidacaoException("Ja existe configuracao de SLA para o tipo de servico e prioridade informados");
        }
    }

    private String normalizarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException(mensagem);
        }

        return valor.trim().toLowerCase();
    }

    private String normalizarPrioridade(String prioridade) {
        if (prioridade == null || prioridade.isBlank()) {
            return "normal";
        }

        return prioridade.trim().toLowerCase();
    }
}