package com.example.backend.grc.governancaPrivacidade;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GovernancaPrivacidadeService {

    private final GovernancaPrivacidadeRepository repository;

    public GovernancaPrivacidadeService(GovernancaPrivacidadeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GovernancaPrivacidade criar(GovernancaPrivacidadeRequestDTO data) {
        validar(data);
        validarNomeCriacao(data.nomeReferencia());
        validarAtivoCriacao(data.ativo());

        GovernancaPrivacidade entity = new GovernancaPrivacidade();
        preencher(entity, data, LocalDateTime.now());
        return repository.save(entity);
    }

    @Transactional
    public GovernancaPrivacidade atualizar(Integer id, GovernancaPrivacidadeRequestDTO data) {
        validar(data);
        validarNomeAtualizacao(data.nomeReferencia(), id);
        validarAtivoAtualizacao(data.ativo(), id);

        GovernancaPrivacidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Governanca de privacidade nao encontrada"));

        preencher(entity, data, entity.getCreatedAt());
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        GovernancaPrivacidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Governanca de privacidade nao encontrada"));

        if (Boolean.TRUE.equals(entity.getAtivo())) {
            throw new ValidacaoException("Nao e permitido excluir configuracao de governanca ativa");
        }

        repository.delete(entity);
    }

    private void preencher(GovernancaPrivacidade entity, GovernancaPrivacidadeRequestDTO data, LocalDateTime createdAt) {
        entity.setNomeReferencia(normalizarObrigatorio(data.nomeReferencia(), "Nome de referencia e obrigatorio"));
        entity.setPapelPrivacidade(normalizarPapel(data.papelPrivacidade()));
        entity.setEncarregadoNome(normalizarObrigatorio(data.encarregadoNome(), "Nome do encarregado e obrigatorio"));
        entity.setEncarregadoEmail(normalizarEmail(data.encarregadoEmail()));
        entity.setEncarregadoCanal(normalizarObrigatorio(data.encarregadoCanal(), "Canal do encarregado e obrigatorio"));
        entity.setBaseContratual(normalizarObrigatorio(data.baseContratual(), "Base contratual e obrigatoria"));
        entity.setClausulasContratuais(normalizarObrigatorio(data.clausulasContratuais(), "Clausulas contratuais sao obrigatorias"));
        entity.setSuboperadoresDeclarados(Boolean.TRUE.equals(data.suboperadoresDeclarados()));
        entity.setTransferenciaInternacional(Boolean.TRUE.equals(data.transferenciaInternacional()));
        entity.setProcedimentoIncidente(normalizarObrigatorio(data.procedimentoIncidente(), "Procedimento de incidente e obrigatorio"));
        entity.setAtivo(data.ativo() == null || data.ativo());
        entity.setVigenteDesde(data.vigenteDesde());
        entity.setRevisaoProgramadaEm(data.revisaoProgramadaEm());
        entity.setObservacoes(normalizarOpcional(data.observacoes()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(GovernancaPrivacidadeRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados de governanca de privacidade sao obrigatorios");
        }

        normalizarObrigatorio(data.nomeReferencia(), "Nome de referencia e obrigatorio");
        normalizarPapel(data.papelPrivacidade());
        normalizarObrigatorio(data.encarregadoNome(), "Nome do encarregado e obrigatorio");
        normalizarEmail(data.encarregadoEmail());
        normalizarObrigatorio(data.encarregadoCanal(), "Canal do encarregado e obrigatorio");
        normalizarObrigatorio(data.baseContratual(), "Base contratual e obrigatoria");
        normalizarObrigatorio(data.clausulasContratuais(), "Clausulas contratuais sao obrigatorias");
        normalizarObrigatorio(data.procedimentoIncidente(), "Procedimento de incidente e obrigatorio");

        if (data.revisaoProgramadaEm() != null
                && data.vigenteDesde() != null
                && data.revisaoProgramadaEm().isBefore(data.vigenteDesde())) {
            throw new ValidacaoException("Data de revisao programada nao pode ser anterior a vigencia");
        }
    }

    private void validarNomeCriacao(String nomeReferencia) {
        if (repository.existsByNomeReferenciaIgnoreCase(normalizarObrigatorio(nomeReferencia, "Nome de referencia e obrigatorio"))) {
            throw new ValidacaoException("Ja existe governanca de privacidade com o nome informado");
        }
    }

    private void validarNomeAtualizacao(String nomeReferencia, Integer id) {
        if (repository.existsByNomeReferenciaIgnoreCaseAndIdNot(normalizarObrigatorio(nomeReferencia, "Nome de referencia e obrigatorio"), id)) {
            throw new ValidacaoException("Ja existe governanca de privacidade com o nome informado");
        }
    }

    private void validarAtivoCriacao(Boolean ativo) {
        if ((ativo == null || ativo) && repository.existsByAtivoTrue()) {
            throw new ValidacaoException("Ja existe uma configuracao de governanca ativa");
        }
    }

    private void validarAtivoAtualizacao(Boolean ativo, Integer id) {
        if ((ativo == null || ativo) && repository.existsByAtivoTrueAndIdNot(id)) {
            throw new ValidacaoException("Ja existe outra configuracao de governanca ativa");
        }
    }

    private String normalizarPapel(String value) {
        String normalized = normalizarObrigatorio(value, "Papel de privacidade e obrigatorio").toLowerCase();
        if (!normalized.equals("controlador")
                && !normalized.equals("operador")
                && !normalized.equals("controlador_operador")) {
            throw new ValidacaoException("Papel de privacidade invalido");
        }
        return normalized;
    }

    private String normalizarEmail(String value) {
        String normalized = normalizarObrigatorio(value, "Email do encarregado e obrigatorio");
        if (!normalized.contains("@") || normalized.startsWith("@") || normalized.endsWith("@")) {
            throw new ValidacaoException("Email do encarregado invalido");
        }
        return normalized;
    }

    private String normalizarObrigatorio(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValidacaoException(message);
        }
        return value.trim();
    }

    private String normalizarOpcional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
