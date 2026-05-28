package com.example.backend.grc.auditorias;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuditoriasService {

    private final AuditoriasRepository repository;
    private final UsuariosRepository usuariosRepository;

    public AuditoriasService(
            AuditoriasRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Auditorias criar(AuditoriasRequestDTO data) {
        validar(data);
        validarRelacionamentosParaCriacao(data);

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        Auditorias entity = new Auditorias();
        preencher(entity, data, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Auditorias atualizar(Integer id, AuditoriasRequestDTO data) {
        validar(data);
        validarRelacionamentosParaAtualizacao(data, id);

        Auditorias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria nao encontrada"));

        Usuarios responsavel = buscarResponsavelOpcional(data.responsavel());

        preencher(entity, data, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Auditorias entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Auditoria nao encontrada"));

        validarExclusao(entity);
        repository.delete(entity);
    }

    private void preencher(
            Auditorias entity,
            AuditoriasRequestDTO data,
            Usuarios responsavel,
            LocalDateTime createdAt
    ) {
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo da auditoria e obrigatorio"));
        entity.setTipoAuditoria(normalizarTipoAuditoria(data.tipoAuditoria()));
        entity.setEscopo(normalizarOpcional(data.escopo()));
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setResponsavel(responsavel);
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(AuditoriasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da auditoria sao obrigatorios");
        }

        normalizarObrigatorio(data.titulo(), "Titulo da auditoria e obrigatorio");

        String tipoAuditoria = normalizarTipoAuditoria(data.tipoAuditoria());
        validarTipoAuditoria(tipoAuditoria);

        if (data.dataInicio() != null
                && data.dataFim() != null
                && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComDatas(status, data.dataInicio(), data.dataFim());
        validarResponsabilizacao(status, data.responsavel());
    }

    private void validarTipoAuditoria(String tipoAuditoria) {
        if (!tipoAuditoria.equals("interna")
                && !tipoAuditoria.equals("externa")
                && !tipoAuditoria.equals("regulatoria")) {
            throw new ValidacaoException("Tipo de auditoria invalido");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("planejada")
                && !status.equals("em_andamento")
                && !status.equals("concluida")
                && !status.equals("cancelada")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComDatas(String status, LocalDate dataInicio, LocalDate dataFim) {
        if (status.equals("concluida") && dataFim == null) {
            throw new ValidacaoException("Data fim e obrigatoria para auditoria concluida");
        }

        if (status.equals("em_andamento") && dataInicio == null) {
            throw new ValidacaoException("Data inicio e obrigatoria para auditoria em andamento");
        }

        if (status.equals("planejada") && dataFim != null) {
            throw new ValidacaoException("Auditoria planejada nao deve ter data fim informada");
        }
    }

    private Usuarios buscarResponsavelOpcional(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String normalizarTipoAuditoria(String tipoAuditoria) {
        if (tipoAuditoria == null || tipoAuditoria.isBlank()) {
            throw new ValidacaoException("Tipo de auditoria e obrigatorio");
        }

        return tipoAuditoria.trim().toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "planejada" : valor.toLowerCase();
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

    private void validarRelacionamentosParaCriacao(AuditoriasRequestDTO data) {
        validarSobreposicaoDeAuditoriaAtiva(data, null);
    }

    private void validarRelacionamentosParaAtualizacao(AuditoriasRequestDTO data, Integer id) {
        validarSobreposicaoDeAuditoriaAtiva(data, id);
    }

    private void validarSobreposicaoDeAuditoriaAtiva(AuditoriasRequestDTO data, Integer id) {
        if (data.responsavel() == null || data.dataInicio() == null) {
            return;
        }

        String status = normalizarStatus(data.status());
        if (!status.equals("planejada") && !status.equals("em_andamento")) {
            return;
        }

        LocalDate dataFimComparacao = data.dataFim() != null ? data.dataFim() : data.dataInicio();
        List<String> statusesAtivos = List.of("planejada", "em_andamento");

        boolean existeSobreposicao = id == null
                ? repository.existsByResponsavelIdAndStatusInAndPeriodoSobreposto(
                        data.responsavel(),
                        statusesAtivos,
                        dataFimComparacao,
                        data.dataInicio()
                )
                : repository.existsByResponsavelIdAndStatusInAndPeriodoSobrepostoAndIdNot(
                        data.responsavel(),
                        statusesAtivos,
                        dataFimComparacao,
                        data.dataInicio(),
                        id
                );

        if (existeSobreposicao) {
            throw new ValidacaoException("Ja existe auditoria ativa com periodo sobreposto para este responsavel");
        }
    }

    private void validarExclusao(Auditorias entity) {
        String status = entity.getStatus() == null ? null : entity.getStatus().trim().toLowerCase();

        if ("em_andamento".equals(status) || "concluida".equals(status) || "cancelada".equals(status)) {
            throw new ValidacaoException("Nao e permitido excluir auditoria que ja entrou em ciclo de execucao");
        }
    }

    private void validarResponsabilizacao(String status, Integer responsavelId) {
        if (("em_andamento".equals(status) || "concluida".equals(status)) && responsavelId == null) {
            throw new ValidacaoException("Auditoria em execucao deve possuir responsavel");
        }
    }
}
