package com.example.backend.sm.atendimentos;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.sm.ordensServico.OrdensServico;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AtendimentosService {

    private final AtendimentosRepository repository;
    private final OrdensServicoRepository ordensServicoRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public AtendimentosService(
            AtendimentosRepository repository,
            OrdensServicoRepository ordensServicoRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.ordensServicoRepository = ordensServicoRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @Transactional
    public Atendimentos criar(AtendimentosRequestDTO data) {
        validar(data);

        OrdensServico os = buscarOs(data.os());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        validarOsParaAtendimento(os);
        validarDataHoraAtendimento(data.dataHora(), os);

        Atendimentos entity = new Atendimentos();
        preencher(entity, data, os, tecnico, LocalDateTime.now());

        promoverOsParaEmAndamentoSeNecessario(os);

        return repository.save(entity);
    }

    @Transactional
    public Atendimentos atualizar(Integer id, AtendimentosRequestDTO data) {
        validar(data);

        Atendimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento nao encontrado"));

        OrdensServico os = buscarOs(data.os());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        validarOsParaAtendimento(os);
        validarDataHoraAtendimento(data.dataHora(), os);

        preencher(entity, data, os, tecnico, entity.getCreatedAt());
        promoverOsParaEmAndamentoSeNecessario(os);

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Atendimentos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Atendimento nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            Atendimentos entity,
            AtendimentosRequestDTO data,
            OrdensServico os,
            Colaboradores tecnico,
            LocalDateTime createdAt
    ) {
        entity.setOs(os);
        entity.setTecnico(tecnico);
        entity.setDataHora(data.dataHora() != null ? data.dataHora() : LocalDateTime.now());
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setHorasGastas(zeroSeNulo(data.horasGastas()));
        entity.setMateriaisUtilizados(normalizarMateriais(data.materiaisUtilizados()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(AtendimentosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do atendimento sao obrigatorios");
        }

        if (data.os() == null) {
            throw new ValidacaoException("Ordem de servico e obrigatoria");
        }

        if (data.tecnico() == null) {
            throw new ValidacaoException("Tecnico e obrigatorio");
        }

        if (data.horasGastas() != null && data.horasGastas().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException("Horas gastas nao podem ser negativas");
        }
    }

    private OrdensServico buscarOs(Integer osId) {
        return ordensServicoRepository.findById(osId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada"));
    }

    private Colaboradores buscarTecnico(Integer tecnicoId) {
        return colaboradoresRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tecnico nao encontrado"));
    }

    private void validarOsParaAtendimento(OrdensServico os) {
        String status = normalizarOpcional(os.getStatus());

        if (status == null) {
            return;
        }

        if (status.equals("cancelada")) {
            throw new ValidacaoException("Nao e permitido registrar atendimento para ordem de servico cancelada");
        }

        if (status.equals("concluida")) {
            throw new ValidacaoException("Nao e permitido registrar atendimento para ordem de servico concluida");
        }
    }

    private void promoverOsParaEmAndamentoSeNecessario(OrdensServico os) {
        String status = normalizarOpcional(os.getStatus());

        if (status == null || status.equals("aberta")) {
            os.setStatus("em_andamento");
        }
    }

    private void validarDataHoraAtendimento(LocalDateTime dataHora, OrdensServico os) {
        if (dataHora == null || os.getDataAbertura() == null) {
            return;
        }

        if (dataHora.isBefore(os.getDataAbertura())) {
            throw new ValidacaoException(
                    "Data e hora do atendimento nao podem ser anteriores a data de abertura da ordem de servico"
            );
        }
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private Map<String, Object> normalizarMateriais(Map<String, Object> materiais) {
        return materiais == null || materiais.isEmpty() ? null : materiais;
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim().toLowerCase();
        return normalizado.isBlank() ? null : normalizado;
    }
}
