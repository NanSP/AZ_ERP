package com.example.backend.grc.consentimentos;

import com.example.backend.grc.registrosTratamento.RegistrosTratamento;
import com.example.backend.grc.registrosTratamento.RegistrosTratamentoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsentimentosService {

    private final ConsentimentosRepository repository;
    private final RegistrosTratamentoRepository registrosTratamentoRepository;

    public ConsentimentosService(
            ConsentimentosRepository repository,
            RegistrosTratamentoRepository registrosTratamentoRepository
    ) {
        this.repository = repository;
        this.registrosTratamentoRepository = registrosTratamentoRepository;
    }

    @Transactional
    public Consentimentos criar(ConsentimentosRequestDTO data) {
        validar(data);
        validarDuplicidadeAtivaParaCriacao(data);

        Consentimentos entity = new Consentimentos();
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public Consentimentos atualizar(Integer id, ConsentimentosRequestDTO data) {
        validar(data);
        validarDuplicidadeAtivaParaAtualizacao(data, id);

        Consentimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consentimento nao encontrado"));

        validarAtualizacao(entity, data);
        preencher(entity, data);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Consentimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consentimento nao encontrado"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(Consentimentos entity, ConsentimentosRequestDTO data) {
        entity.setTitular(data.titular());
        entity.setTipoTitular(normalizarTipoTitular(data.tipoTitular()));
        entity.setFinalidade(normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria"));
        entity.setDataConsentimento(resolverDataConsentimento(data.dataConsentimento(), entity.getDataConsentimento()));
        entity.setDataRevogacao(data.dataRevogacao());
        entity.setIpAddress(data.ipAddress());
        entity.setUserAgent(normalizarOpcional(data.userAgent()));
        entity.setRegistroTratamento(buscarRegistroTratamentoOpcional(data.registroTratamentoId()));
    }

    private void validar(ConsentimentosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do consentimento sao obrigatorios");
        }

        if (data.titular() == null) {
            throw new ValidacaoException("Titular e obrigatorio");
        }

        validarTipoTitular(normalizarTipoTitular(data.tipoTitular()));
        normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria");

        if (data.dataConsentimento() != null
                && data.dataRevogacao() != null
                && data.dataRevogacao().isBefore(data.dataConsentimento())) {
            throw new ValidacaoException("Data de revogacao nao pode ser anterior a data de consentimento");
        }

        validarRegistroTratamento(data.registroTratamentoId(), data.finalidade());
    }

    private void validarAtualizacao(Consentimentos entity, ConsentimentosRequestDTO data) {
        if (entity.getDataRevogacao() != null && data.dataRevogacao() == null) {
            throw new ValidacaoException("Consentimento revogado nao pode voltar a ficar ativo");
        }

        LocalDateTime dataConsentimentoBase = entity.getDataConsentimento() != null
                ? entity.getDataConsentimento()
                : data.dataConsentimento();

        if (dataConsentimentoBase != null
                && data.dataRevogacao() != null
                && data.dataRevogacao().isBefore(dataConsentimentoBase)) {
            throw new ValidacaoException("Data de revogacao nao pode ser anterior a data de consentimento");
        }
    }

    private void validarExclusao(Consentimentos entity) {
        if (entity.getDataRevogacao() != null) {
            throw new ValidacaoException("Nao e permitido excluir consentimento que ja possui revogacao registrada");
        }

        String tipoTitular = normalizarTipoTitular(entity.getTipoTitular());
        String finalidade = normalizarObrigatorio(entity.getFinalidade(), "Finalidade e obrigatoria");
        LocalDateTime dataConsentimento = entity.getDataConsentimento() != null
                ? entity.getDataConsentimento()
                : LocalDateTime.MAX;

        if (repository.existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNullAndDataConsentimentoLessThan(
                entity.getTitular(),
                tipoTitular,
                finalidade,
                dataConsentimento
        )) {
            throw new ValidacaoException("Nao e permitido excluir consentimento ativo quando ja houver historico anterior da mesma finalidade");
        }
    }

    private LocalDateTime resolverDataConsentimento(LocalDateTime dataRequest, LocalDateTime dataAtual) {
        if (dataAtual != null) {
            return dataAtual;
        }

        return dataRequest != null ? dataRequest : LocalDateTime.now();
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

    private RegistrosTratamento buscarRegistroTratamentoOpcional(Integer registroTratamentoId) {
        if (registroTratamentoId == null) {
            return null;
        }

        return registrosTratamentoRepository.findById(registroTratamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de tratamento nao encontrado"));
    }

    private void validarRegistroTratamento(Integer registroTratamentoId, String finalidade) {
        if (registroTratamentoId == null) {
            return;
        }

        RegistrosTratamento registro = buscarRegistroTratamentoOpcional(registroTratamentoId);

        if (!Boolean.TRUE.equals(registro.getRequerConsentimento())
                && !"consentimento".equalsIgnoreCase(registro.getBaseLegal())) {
            throw new ValidacaoException("Registro de tratamento informado nao exige consentimento");
        }

        String finalidadeNormalizada = normalizarObrigatorio(finalidade, "Finalidade e obrigatoria");

        if (!registro.getFinalidade().equalsIgnoreCase(finalidadeNormalizada)) {
            throw new ValidacaoException("Finalidade do consentimento deve ser coerente com o registro de tratamento vinculado");
        }
    }

    private String normalizarTipoTitular(String tipoTitular) {
        String valor = normalizarObrigatorio(tipoTitular, "Tipo do titular e obrigatorio");
        return valor.toLowerCase();
    }

    private void validarTipoTitular(String tipoTitular) {
        if (!tipoTitular.equals("cliente")
                && !tipoTitular.equals("colaborador")
                && !tipoTitular.equals("fornecedor")
                && !tipoTitular.equals("usuario")
                && !tipoTitular.equals("outro")) {
            throw new ValidacaoException("Tipo do titular invalido");
        }
    }

    private void validarDuplicidadeAtivaParaCriacao(ConsentimentosRequestDTO data) {
        if (data.dataRevogacao() != null) {
            return;
        }

        String tipoTitular = normalizarTipoTitular(data.tipoTitular());
        String finalidade = normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria");

        if (repository.existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNull(
                data.titular(),
                tipoTitular,
                finalidade
        )) {
            throw new ValidacaoException("Ja existe consentimento ativo para este titular e finalidade");
        }
    }

    private void validarDuplicidadeAtivaParaAtualizacao(ConsentimentosRequestDTO data, Integer id) {
        if (data.dataRevogacao() != null) {
            return;
        }

        String tipoTitular = normalizarTipoTitular(data.tipoTitular());
        String finalidade = normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria");

        if (repository.existsByTitularAndTipoTitularAndFinalidadeAndDataRevogacaoIsNullAndIdNot(
                data.titular(),
                tipoTitular,
                finalidade,
                id
        )) {
            throw new ValidacaoException("Ja existe consentimento ativo para este titular e finalidade");
        }
    }
}
