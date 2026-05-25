package com.example.backend.qm.naoConformidade;

import com.example.backend.qm.inspecoes.Inspecoes;
import com.example.backend.qm.inspecoes.InspecoesRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NaoConformidadeService {

    private final NaoConformidadeRepository repository;
    private final InspecoesRepository inspecoesRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public NaoConformidadeService(
            NaoConformidadeRepository repository,
            InspecoesRepository inspecoesRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.inspecoesRepository = inspecoesRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public NaoConformidade criar(NaoConformidadeRequestDTO data) {
        validar(data);

        Inspecoes inspecao = buscarInspecao(data.inspecao());
        Colaboradores responsavel = buscarResponsavel(data.responsavel());

        NaoConformidade entity = new NaoConformidade();
        preencher(entity, data, inspecao, responsavel, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public NaoConformidade atualizar(Integer id, NaoConformidadeRequestDTO data) {
        validar(data);

        NaoConformidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nao conformidade nao encontrada"));

        Inspecoes inspecao = buscarInspecao(data.inspecao());
        Colaboradores responsavel = buscarResponsavel(data.responsavel());

        preencher(entity, data, inspecao, responsavel, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        NaoConformidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nao conformidade nao encontrada"));

        repository.delete(entity);
    }

    private void preencher(
            NaoConformidade entity,
            NaoConformidadeRequestDTO data,
            Inspecoes inspecao,
            Colaboradores responsavel,
            LocalDateTime createdAt
    ) {
        entity.setInspecao(inspecao);
        entity.setTipoNaoConformidade(normalizarOpcional(data.tipoNaoConformidade()));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setCausaRaiz(normalizarOpcional(data.causaRaiz()));
        entity.setAcaoImediata(normalizarOpcional(data.acaoImediata()));
        entity.setAcaoCorretiva(normalizarOpcional(data.acaoCorretiva()));
        entity.setResponsavel(responsavel);
        entity.setDataIdentificacao(data.dataIdentificacao());
        entity.setDataResolucao(data.dataResolucao());
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(NaoConformidadeRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da nao conformidade sao obrigatorios");
        }

        String status = normalizarStatus(data.status());

        if (data.dataIdentificacao() != null
                && data.dataResolucao() != null
                && data.dataResolucao().isBefore(data.dataIdentificacao())) {
            throw new ValidacaoException("Data de resolucao nao pode ser anterior a data de identificacao");
        }

        if (status.equals("resolvida") && data.dataResolucao() == null) {
            throw new ValidacaoException("Data de resolucao e obrigatoria quando a nao conformidade estiver resolvida");
        }

        if (!status.equals("resolvida") && data.dataResolucao() != null) {
            throw new ValidacaoException("Data de resolucao so deve ser informada quando a nao conformidade estiver resolvida");
        }
    }

    private Inspecoes buscarInspecao(Integer inspecaoId) {
        if (inspecaoId == null) {
            return null;
        }

        return inspecoesRepository.findById(inspecaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inspecao nao encontrada"));
    }

    private Colaboradores buscarResponsavel(Integer responsavelId) {
        if (responsavelId == null) {
            return null;
        }

        return colaboradoresRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "aberta" : valor.toLowerCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}