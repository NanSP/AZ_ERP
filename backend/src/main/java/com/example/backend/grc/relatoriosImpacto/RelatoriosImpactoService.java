package com.example.backend.grc.relatoriosImpacto;

import com.example.backend.grc.shared.PrivacyRiskCatalog;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RelatoriosImpactoService {

    private final RelatoriosImpactoRepository repository;
    private final UsuariosRepository usuariosRepository;

    public RelatoriosImpactoService(
            RelatoriosImpactoRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public RelatoriosImpacto criar(RelatoriosImpactoRequestDTO data) {
        validar(data);
        validarTituloCriacao(data.titulo());

        RelatoriosImpacto entity = new RelatoriosImpacto();
        preencher(entity, data, LocalDateTime.now());
        return repository.save(entity);
    }

    @Transactional
    public RelatoriosImpacto atualizar(Integer id, RelatoriosImpactoRequestDTO data) {
        validar(data);
        validarTituloAtualizacao(data.titulo(), id);

        RelatoriosImpacto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio de impacto nao encontrado"));

        preencher(entity, data, entity.getCreatedAt());
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        RelatoriosImpacto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio de impacto nao encontrado"));

        if ("aprovado".equalsIgnoreCase(entity.getDecisao())) {
            throw new ValidacaoException("Nao e permitido excluir relatorio de impacto aprovado");
        }

        repository.delete(entity);
    }

    private void preencher(RelatoriosImpacto entity, RelatoriosImpactoRequestDTO data, LocalDateTime createdAt) {
        String escopoCritico = PrivacyRiskCatalog.normalizeScope(data.escopoCritico());

        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo e obrigatorio"));
        entity.setEscopoCritico(escopoCritico);
        entity.setPrioridadeRisco(PrivacyRiskCatalog.derivePriority(escopoCritico));
        entity.setModulo(normalizarOpcional(data.modulo()));
        entity.setRecurso(normalizarOpcional(data.recurso()));
        entity.setFinalidade(normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria"));
        entity.setDadosPessoaisEnvolvidos(normalizarObrigatorio(data.dadosPessoaisEnvolvidos(), "Dados pessoais envolvidos sao obrigatorios"));
        entity.setDadosSensiveis(Boolean.TRUE.equals(data.dadosSensiveis()));
        entity.setBaseLegal(normalizarBaseLegal(data.baseLegal()));
        entity.setVolumeTitulares(validarVolume(data.volumeTitulares()));
        entity.setCompartilhamentoExterno(Boolean.TRUE.equals(data.compartilhamentoExterno()));
        entity.setMedidasTecnicas(normalizarObrigatorio(data.medidasTecnicas(), "Medidas tecnicas sao obrigatorias"));
        entity.setMedidasOrganizacionais(normalizarObrigatorio(data.medidasOrganizacionais(), "Medidas organizacionais sao obrigatorias"));
        entity.setRiscoResidual(normalizarRiscoResidual(data.riscoResidual()));
        entity.setDecisao(normalizarDecisao(data.decisao()));
        entity.setAprovadoPor(buscarUsuarioOpcional(data.aprovadoPor()));
        entity.setRevisadoEm(data.revisadoEm());
        entity.setCreatedAt(createdAt);
    }

    private void validar(RelatoriosImpactoRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do relatorio de impacto sao obrigatorios");
        }

        String escopo = PrivacyRiskCatalog.normalizeScope(data.escopoCritico());
        normalizarObrigatorio(data.titulo(), "Titulo e obrigatorio");
        normalizarObrigatorio(data.finalidade(), "Finalidade e obrigatoria");
        normalizarObrigatorio(data.dadosPessoaisEnvolvidos(), "Dados pessoais envolvidos sao obrigatorios");
        normalizarBaseLegal(data.baseLegal());
        normalizarObrigatorio(data.medidasTecnicas(), "Medidas tecnicas sao obrigatorias");
        normalizarObrigatorio(data.medidasOrganizacionais(), "Medidas organizacionais sao obrigatorias");
        String riscoResidual = normalizarRiscoResidual(data.riscoResidual());
        String decisao = normalizarDecisao(data.decisao());
        validarVolume(data.volumeTitulares());

        if ("alta".equals(PrivacyRiskCatalog.derivePriority(escopo))
                && !"alto".equals(riscoResidual)
                && data.aprovadoPor() == null) {
            throw new ValidacaoException("Relatorio de alto risco deve possuir aprovacao formal");
        }

        if ("aprovado".equals(decisao) && data.aprovadoPor() == null) {
            throw new ValidacaoException("Decisao aprovada exige usuario aprovador");
        }

        if ("alto".equals(riscoResidual) && "aceito".equals(decisao)) {
            throw new ValidacaoException("Risco residual alto nao pode ser apenas aceito sem replanejamento");
        }
    }

    private void validarTituloCriacao(String titulo) {
        if (repository.existsByTituloIgnoreCase(normalizarObrigatorio(titulo, "Titulo e obrigatorio"))) {
            throw new ValidacaoException("Ja existe relatorio de impacto com o titulo informado");
        }
    }

    private void validarTituloAtualizacao(String titulo, Integer id) {
        if (repository.existsByTituloIgnoreCaseAndIdNot(normalizarObrigatorio(titulo, "Titulo e obrigatorio"), id)) {
            throw new ValidacaoException("Ja existe relatorio de impacto com o titulo informado");
        }
    }

    private Usuarios buscarUsuarioOpcional(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }

        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario aprovador nao encontrado"));
    }

    private String normalizarBaseLegal(String value) {
        String normalized = normalizarObrigatorio(value, "Base legal e obrigatoria").toLowerCase();
        if (!normalized.equals("consentimento")
                && !normalized.equals("execucao_contrato")
                && !normalized.equals("obrigacao_legal")
                && !normalized.equals("legitimo_interesse")
                && !normalized.equals("exercicio_regular_direitos")
                && !normalized.equals("protecao_credito")) {
            throw new ValidacaoException("Base legal invalida");
        }

        return normalized;
    }

    private Integer validarVolume(Integer value) {
        if (value == null) {
            return null;
        }

        if (value < 0) {
            throw new ValidacaoException("Volume de titulares invalido");
        }

        return value;
    }

    private String normalizarRiscoResidual(String value) {
        String normalized = normalizarObrigatorio(value, "Risco residual e obrigatorio").toLowerCase();
        if (!normalized.equals("baixo") && !normalized.equals("medio") && !normalized.equals("alto")) {
            throw new ValidacaoException("Risco residual invalido");
        }

        return normalized;
    }

    private String normalizarDecisao(String value) {
        String normalized = normalizarObrigatorio(value, "Decisao e obrigatoria").toLowerCase();
        if (!normalized.equals("aprovado")
                && !normalized.equals("replanejar")
                && !normalized.equals("aceito")) {
            throw new ValidacaoException("Decisao invalida");
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
