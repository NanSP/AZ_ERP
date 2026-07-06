package com.example.backend.grc.incidentesSeguranca;

import com.example.backend.grc.shared.PrivacyRiskCatalog;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IncidentesSegurancaService {

    private final IncidentesSegurancaRepository repository;
    private final UsuariosRepository usuariosRepository;

    public IncidentesSegurancaService(
            IncidentesSegurancaRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public IncidentesSeguranca criar(IncidentesSegurancaRequestDTO data) {
        validar(data);
        validarCodigoCriacao(normalizarOpcional(data.codigo()));

        IncidentesSeguranca entity = new IncidentesSeguranca();
        preencher(entity, data, LocalDateTime.now(), null);
        entity.setDataRegistro(LocalDateTime.now());
        if (entity.getCodigo() == null) {
            entity.setCodigo(gerarCodigo());
        }
        return repository.save(entity);
    }

    @Transactional
    public IncidentesSeguranca atualizar(Integer id, IncidentesSegurancaRequestDTO data) {
        validar(data);
        validarCodigoAtualizacao(normalizarOpcional(data.codigo()), id);

        IncidentesSeguranca entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Incidente de seguranca nao encontrado"));

        preencher(entity, data, entity.getCreatedAt(), entity.getDataRegistro());
        if (entity.getCodigo() == null) {
            entity.setCodigo(gerarCodigo());
        }
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        IncidentesSeguranca entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Incidente de seguranca nao encontrado"));

        if (!"detectado".equalsIgnoreCase(entity.getEtapaAtual())
                && !"registrado".equalsIgnoreCase(entity.getEtapaAtual())) {
            throw new ValidacaoException("Nao e permitido excluir incidente que ja entrou em avaliacao ou resposta");
        }

        repository.delete(entity);
    }

    private void preencher(
            IncidentesSeguranca entity,
            IncidentesSegurancaRequestDTO data,
            LocalDateTime createdAt,
            LocalDateTime dataRegistroAtual
    ) {
        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo e obrigatorio"));
        entity.setEscopoCritico(PrivacyRiskCatalog.normalizeScope(data.escopoCritico()));
        entity.setSeveridade(normalizarSeveridade(data.severidade()));
        entity.setEtapaAtual(normalizarEtapa(data.etapaAtual()));
        entity.setOrigemDeteccao(normalizarOrigem(data.origemDeteccao()));
        entity.setResumoIncidente(normalizarObrigatorio(data.resumoIncidente(), "Resumo do incidente e obrigatorio"));
        entity.setDadosAfetados(normalizarOpcional(data.dadosAfetados()));
        entity.setTitularesEstimados(validarTitulares(data.titularesEstimados()));
        entity.setSegredoTecnicoExposto(Boolean.TRUE.equals(data.segredoTecnicoExposto()));
        entity.setRequerComunicacaoAnpd(Boolean.TRUE.equals(data.requerComunicacaoAnpd()));
        entity.setRequerComunicacaoTitulares(Boolean.TRUE.equals(data.requerComunicacaoTitulares()));
        entity.setDataDeteccao(data.dataDeteccao());
        entity.setDataRegistro(dataRegistroAtual != null ? dataRegistroAtual : LocalDateTime.now());
        entity.setDataAvaliacao(data.dataAvaliacao());
        entity.setDataResposta(data.dataResposta());
        entity.setDataComunicacao(data.dataComunicacao());
        entity.setDataEncerramento(data.dataEncerramento());
        entity.setCausaRaiz(normalizarOpcional(data.causaRaiz()));
        entity.setAcoesContencao(normalizarOpcional(data.acoesContencao()));
        entity.setAcoesCorretivas(normalizarOpcional(data.acoesCorretivas()));
        entity.setResponsavel(buscarResponsavelOpcional(data.responsavel()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(IncidentesSegurancaRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do incidente de seguranca sao obrigatorios");
        }

        String escopo = PrivacyRiskCatalog.normalizeScope(data.escopoCritico());
        String severidade = normalizarSeveridade(data.severidade());
        String etapa = normalizarEtapa(data.etapaAtual());
        normalizarObrigatorio(data.titulo(), "Titulo e obrigatorio");
        normalizarOrigem(data.origemDeteccao());
        normalizarObrigatorio(data.resumoIncidente(), "Resumo do incidente e obrigatorio");

        if (data.dataDeteccao() == null) {
            throw new ValidacaoException("Data de deteccao e obrigatoria");
        }

        validarTitulares(data.titularesEstimados());

        if (("critica".equals(severidade) || Boolean.TRUE.equals(data.segredoTecnicoExposto()))
                && !Boolean.TRUE.equals(data.requerComunicacaoAnpd())) {
            throw new ValidacaoException("Incidente critico ou com exposicao de segredo tecnico exige avaliacao de comunicacao a ANPD");
        }

        if ("comunicacao".equals(etapa) && data.dataComunicacao() == null) {
            throw new ValidacaoException("Etapa de comunicacao exige data de comunicacao");
        }

        if ("encerrado".equals(etapa) && (data.dataEncerramento() == null || normalizarOpcional(data.acoesCorretivas()) == null)) {
            throw new ValidacaoException("Encerramento exige data e acoes corretivas registradas");
        }

        if ("alta".equals(PrivacyRiskCatalog.derivePriority(escopo))
                && !"alta".equals(severidade)
                && !"critica".equals(severidade)) {
            throw new ValidacaoException("Incidentes em escopo critico alto devem possuir severidade alta ou critica");
        }
    }

    private void validarCodigoCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe incidente com o codigo informado");
        }
    }

    private void validarCodigoAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe incidente com o codigo informado");
        }
    }

    private String gerarCodigo() {
        String base = "INC-" + System.currentTimeMillis();
        String candidate = base;
        int suffix = 1;
        while (repository.existsByCodigo(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private Usuarios buscarResponsavelOpcional(Integer id) {
        if (id == null) {
            return null;
        }

        return usuariosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String normalizarSeveridade(String value) {
        String normalized = normalizarObrigatorio(value, "Severidade e obrigatoria").toLowerCase();
        if (!normalized.equals("baixa")
                && !normalized.equals("media")
                && !normalized.equals("alta")
                && !normalized.equals("critica")) {
            throw new ValidacaoException("Severidade invalida");
        }
        return normalized;
    }

    private String normalizarEtapa(String value) {
        String normalized = normalizarObrigatorio(value, "Etapa atual e obrigatoria").toLowerCase();
        if (!normalized.equals("detectado")
                && !normalized.equals("registrado")
                && !normalized.equals("avaliacao")
                && !normalized.equals("resposta")
                && !normalized.equals("comunicacao")
                && !normalized.equals("encerrado")) {
            throw new ValidacaoException("Etapa atual invalida");
        }
        return normalized;
    }

    private String normalizarOrigem(String value) {
        String normalized = normalizarObrigatorio(value, "Origem de deteccao e obrigatoria").toLowerCase();
        if (!normalized.equals("monitoramento")
                && !normalized.equals("usuario")
                && !normalized.equals("fornecedor")
                && !normalized.equals("auditoria")
                && !normalized.equals("suporte")) {
            throw new ValidacaoException("Origem de deteccao invalida");
        }
        return normalized;
    }

    private Integer validarTitulares(Integer value) {
        if (value == null) {
            return null;
        }
        if (value < 0) {
            throw new ValidacaoException("Quantidade estimada de titulares invalida");
        }
        return value;
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
