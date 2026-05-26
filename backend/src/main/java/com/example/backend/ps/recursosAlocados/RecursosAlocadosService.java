package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.ps.tarefas.Tarefas;
import com.example.backend.ps.tarefas.TarefasRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RecursosAlocadosService {

    private final RecursosAlocadosRepository repository;
    private final ProjetosRepository projetosRepository;
    private final TarefasRepository tarefasRepository;

    public RecursosAlocadosService(
            RecursosAlocadosRepository repository,
            ProjetosRepository projetosRepository,
            TarefasRepository tarefasRepository
    ) {
        this.repository = repository;
        this.projetosRepository = projetosRepository;
        this.tarefasRepository = tarefasRepository;
    }

    @Transactional
    public RecursosAlocados criar(RecursosAlocadosRequestDTO data) {
        validar(data);

        Projetos projeto = buscarProjeto(data.projeto());
        Tarefas tarefa = buscarTarefa(data.tarefa());

        validarRelacionamento(projeto, tarefa);

        RecursosAlocados entity = new RecursosAlocados();
        preencher(entity, data, projeto, tarefa, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public RecursosAlocados atualizar(Integer id, RecursosAlocadosRequestDTO data) {
        validar(data);

        RecursosAlocados entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso alocado nao encontrado"));

        Projetos projeto = buscarProjeto(data.projeto());
        Tarefas tarefa = buscarTarefa(data.tarefa());

        validarRelacionamento(projeto, tarefa);

        preencher(entity, data, projeto, tarefa, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        RecursosAlocados entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso alocado nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            RecursosAlocados entity,
            RecursosAlocadosRequestDTO data,
            Projetos projeto,
            Tarefas tarefa,
            LocalDateTime createdAt
    ) {
        BigDecimal quantidade = zeroSeNulo(data.quantidade());
        BigDecimal valorUnitario = zeroSeNulo(data.valorUnitario());

        entity.setProjeto(projeto);
        entity.setTarefa(tarefa);
        entity.setTipoRecurso(normalizarTipoRecurso(data.tipoRecurso()));
        entity.setRecursoId(data.recursoId());
        entity.setQuantidade(quantidade);
        entity.setValorUnitario(valorUnitario);
        entity.setValorTotal(calcularValorTotal(quantidade, valorUnitario));
        entity.setDataAlocacao(data.dataAlocacao());
        entity.setCreatedAt(createdAt);
    }

    private void validar(RecursosAlocadosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do recurso alocado sao obrigatorios");
        }

        if (data.projeto() == null && data.tarefa() == null) {
            throw new ValidacaoException("Projeto ou tarefa deve ser informado");
        }

        if (data.recursoId() == null) {
            throw new ValidacaoException("Recurso e obrigatorio");
        }

        String tipoRecurso = normalizarTipoRecurso(data.tipoRecurso());
        validarTipoRecurso(tipoRecurso);

        validarNaoNegativo(data.quantidade(), "Quantidade nao pode ser negativa");
        validarNaoNegativo(data.valorUnitario(), "Valor unitario nao pode ser negativo");
    }

    private void validarTipoRecurso(String tipoRecurso) {
        if (!tipoRecurso.equals("humano")
                && !tipoRecurso.equals("material")
                && !tipoRecurso.equals("financeiro")) {
            throw new ValidacaoException("Tipo de recurso invalido");
        }
    }

    private void validarRelacionamento(Projetos projeto, Tarefas tarefa) {
        if (tarefa == null) {
            return;
        }

        if (tarefa.getProjeto() == null) {
            throw new ValidacaoException("Tarefa informada nao possui projeto associado");
        }

        if (projeto != null && !tarefa.getProjeto().getId().equals(projeto.getId())) {
            throw new ValidacaoException("Tarefa deve pertencer ao mesmo projeto informado");
        }
    }

    private Projetos buscarProjeto(Integer projetoId) {
        if (projetoId == null) {
            return null;
        }

        return projetosRepository.findById(projetoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado"));
    }

    private Tarefas buscarTarefa(Integer tarefaId) {
        if (tarefaId == null) {
            return null;
        }

        return tarefasRepository.findById(tarefaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal calcularValorTotal(BigDecimal quantidade, BigDecimal valorUnitario) {
        return quantidade.multiply(valorUnitario);
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String normalizarTipoRecurso(String tipoRecurso) {
        if (tipoRecurso == null || tipoRecurso.isBlank()) {
            return "";
        }

        return tipoRecurso.trim().toLowerCase();
    }
}