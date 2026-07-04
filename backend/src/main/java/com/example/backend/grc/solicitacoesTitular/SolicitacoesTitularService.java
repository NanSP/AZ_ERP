package com.example.backend.grc.solicitacoesTitular;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SolicitacoesTitularService {

    private final SolicitacoesTitularRepository repository;
    private final UsuariosRepository usuariosRepository;

    public SolicitacoesTitularService(
            SolicitacoesTitularRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public SolicitacoesTitular criar(SolicitacoesTitularRequestDTO data) {
        validar(data, false);

        SolicitacoesTitular entity = new SolicitacoesTitular();
        preencher(entity, data, LocalDateTime.now(), null, null);
        entity.setProtocolo(gerarProtocolo());

        return repository.save(entity);
    }

    @Transactional
    public SolicitacoesTitular atualizar(Integer id, SolicitacoesTitularRequestDTO data) {
        validar(data, true);

        SolicitacoesTitular entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao do titular nao encontrada"));

        preencher(entity, data, entity.getCreatedAt(), entity.getDataSolicitacao(), entity.getProtocolo());
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        SolicitacoesTitular entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao do titular nao encontrada"));

        if (!"aberta".equalsIgnoreCase(entity.getStatus())) {
            throw new ValidacaoException("Nao e permitido excluir solicitacao que ja entrou em tratamento");
        }

        repository.delete(entity);
    }

    private void preencher(
            SolicitacoesTitular entity,
            SolicitacoesTitularRequestDTO data,
            LocalDateTime createdAt,
            LocalDateTime dataSolicitacaoAtual,
            String protocoloAtual
    ) {
        String status = normalizarStatus(data.status());
        entity.setProtocolo(protocoloAtual);
        entity.setTitularNome(normalizarObrigatorio(data.titularNome(), "Nome do titular e obrigatorio"));
        entity.setTitularContato(normalizarObrigatorio(data.titularContato(), "Contato do titular e obrigatorio"));
        entity.setTipoTitular(normalizarTipoTitular(data.tipoTitular()));
        entity.setDireitoSolicitado(normalizarDireito(data.direitoSolicitado()));
        entity.setModulo(normalizarOpcional(data.modulo()));
        entity.setEntidade(normalizarOpcional(data.entidade()));
        entity.setStatus(status);
        entity.setCanalOrigem(normalizarCanalOrigem(data.canalOrigem()));
        entity.setDetalhes(normalizarOpcional(data.detalhes()));
        entity.setPrazoResposta(resolverPrazoResposta(data.prazoResposta(), dataSolicitacaoAtual));
        entity.setDataSolicitacao(dataSolicitacaoAtual != null ? dataSolicitacaoAtual : LocalDateTime.now());
        entity.setDataConclusao(resolverDataConclusao(status, data.dataConclusao()));
        entity.setRespostaResumo(normalizarOpcional(data.respostaResumo()));
        entity.setAtendidoPor(buscarUsuarioOpcional(data.atendidoPor()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(SolicitacoesTitularRequestDTO data, boolean updating) {
        if (data == null) {
            throw new ValidacaoException("Dados da solicitacao do titular sao obrigatorios");
        }

        String status = normalizarStatus(data.status());
        normalizarObrigatorio(data.titularNome(), "Nome do titular e obrigatorio");
        normalizarObrigatorio(data.titularContato(), "Contato do titular e obrigatorio");
        normalizarTipoTitular(data.tipoTitular());
        normalizarDireito(data.direitoSolicitado());
        normalizarCanalOrigem(data.canalOrigem());

        if ((status.equals("concluida") || status.equals("indeferida"))
                && normalizarOpcional(data.respostaResumo()) == null) {
            throw new ValidacaoException("Resposta resumo e obrigatoria para encerramento da solicitacao");
        }

        if (!(status.equals("concluida") || status.equals("indeferida"))
                && data.dataConclusao() != null) {
            throw new ValidacaoException("Data de conclusao so pode ser informada para solicitacoes encerradas");
        }

        if (!updating && status.equals("concluida")) {
            throw new ValidacaoException("Solicitacao nao pode nascer concluida");
        }
    }

    private Usuarios buscarUsuarioOpcional(Integer usuarioId) {
        if (usuarioId == null) {
            return null;
        }

        return usuariosRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario executor nao encontrado"));
    }

    private String gerarProtocolo() {
        String base = "LGPD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String protocolo = base;
        int suffix = 1;

        while (repository.existsByProtocolo(protocolo)) {
            protocolo = base + "-" + suffix++;
        }

        return protocolo;
    }

    private LocalDateTime resolverPrazoResposta(LocalDateTime prazoResposta, LocalDateTime dataSolicitacaoAtual) {
        if (prazoResposta != null) {
            return prazoResposta;
        }

        LocalDateTime referencia = dataSolicitacaoAtual != null ? dataSolicitacaoAtual : LocalDateTime.now();
        return referencia.plusDays(15);
    }

    private LocalDateTime resolverDataConclusao(String status, LocalDateTime dataConclusao) {
        if (status.equals("concluida") || status.equals("indeferida")) {
            return dataConclusao != null ? dataConclusao : LocalDateTime.now();
        }

        return dataConclusao;
    }

    private String normalizarTipoTitular(String value) {
        String normalized = normalizarObrigatorio(value, "Tipo do titular e obrigatorio").toLowerCase();

        if (!normalized.equals("cliente")
                && !normalized.equals("colaborador")
                && !normalized.equals("fornecedor")
                && !normalized.equals("usuario")
                && !normalized.equals("visitante")
                && !normalized.equals("outro")) {
            throw new ValidacaoException("Tipo do titular invalido");
        }

        return normalized;
    }

    private String normalizarDireito(String value) {
        String normalized = normalizarObrigatorio(value, "Direito solicitado e obrigatorio").toLowerCase();

        if (!normalized.equals("confirmacao")
                && !normalized.equals("acesso")
                && !normalized.equals("correcao")
                && !normalized.equals("anonimizacao")
                && !normalized.equals("eliminacao")
                && !normalized.equals("portabilidade")
                && !normalized.equals("oposicao")
                && !normalized.equals("revogacao_consentimento")
                && !normalized.equals("revisao")) {
            throw new ValidacaoException("Direito solicitado invalido");
        }

        return normalized;
    }

    private String normalizarStatus(String value) {
        String normalized = normalizarOpcional(value);
        if (normalized == null) {
            return "aberta";
        }

        normalized = normalized.toLowerCase();

        if (!normalized.equals("aberta")
                && !normalized.equals("em_analise")
                && !normalized.equals("aguardando_titular")
                && !normalized.equals("concluida")
                && !normalized.equals("indeferida")) {
            throw new ValidacaoException("Status da solicitacao invalido");
        }

        return normalized;
    }

    private String normalizarCanalOrigem(String value) {
        String normalized = normalizarOpcional(value);
        if (normalized == null) {
            return null;
        }

        normalized = normalized.toLowerCase();

        if (!normalized.equals("portal")
                && !normalized.equals("email")
                && !normalized.equals("telefone")
                && !normalized.equals("presencial")
                && !normalized.equals("suporte")) {
            throw new ValidacaoException("Canal de origem invalido");
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
