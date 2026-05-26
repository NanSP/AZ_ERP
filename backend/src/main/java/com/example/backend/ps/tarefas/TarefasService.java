package com.example.backend.ps.tarefas;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class TarefasService {

    private final TarefasRepository repository;
    private final ProjetosRepository projetosRepository;
    private final UsuariosRepository usuariosRepository;

    public TarefasService(
            TarefasRepository repository,
            ProjetosRepository projetosRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.projetosRepository = projetosRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Tarefas criar(TarefasRequestDTO data) {
        validar(data);

        Projetos projeto = buscarProjeto(data.projeto());
        Tarefas tarefaPai = buscarTarefaPai(data.tarefaPai(), null);
        Usuarios responsavel = buscarResponsavel(data.responsavel());

        validarHierarquia(projeto, tarefaPai, null);

        Tarefas entity = new Tarefas();
        preencher(entity, data, projeto, tarefaPai, responsavel, LocalDateTime.now());

        Tarefas saved = repository.save(entity);
        recalcularProgressoProjeto(projeto);

        return saved;
    }

    @Transactional
    public Tarefas atualizar(Integer id, TarefasRequestDTO data) {
        validar(data);

        Tarefas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada"));

        Projetos projeto = buscarProjeto(data.projeto());
        Tarefas tarefaPai = buscarTarefaPai(data.tarefaPai(), id);
        Usuarios responsavel = buscarResponsavel(data.responsavel());

        validarHierarquia(projeto, tarefaPai, id);

        Projetos projetoAnterior = entity.getProjeto();
        preencher(entity, data, projeto, tarefaPai, responsavel, entity.getCreatedAt());

        Tarefas updated = repository.save(entity);

        if (projetoAnterior != null && !projetoAnterior.getId().equals(projeto.getId())) {
            recalcularProgressoProjeto(projetoAnterior);
        }
        recalcularProgressoProjeto(projeto);

        return updated;
    }

    @Transactional
    public void excluir(Integer id) {
        Tarefas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada"));

        if (entity.getSubtarefas() != null && !entity.getSubtarefas().isEmpty()) {
            throw new ValidacaoException("Nao e permitido excluir tarefa que possui subtarefas");
        }

        Projetos projeto = entity.getProjeto();
        repository.delete(entity);
        recalcularProgressoProjeto(projeto);
    }

    private void preencher(
            Tarefas entity,
            TarefasRequestDTO data,
            Projetos projeto,
            Tarefas tarefaPai,
            Usuarios responsavel,
            LocalDateTime createdAt
    ) {
        entity.setProjeto(projeto);
        entity.setTarefaPai(tarefaPai);
        entity.setTitulo(normalizarObrigatorio(data.titulo(), "Titulo da tarefa e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setResponsavel(responsavel);
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setHorasEstimadas(zeroSeNulo(data.horasEstimadas()));
        entity.setHorasRealizadas(zeroSeNulo(data.horasRealizadas()));
        entity.setPercentualConcluido(normalizarPercentual(data.percentualConcluido()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setPrioridade(normalizarPrioridade(data.prioridade()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(TarefasRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados da tarefa sao obrigatorios");
        }

        if (data.projeto() == null) {
            throw new ValidacaoException("Projeto e obrigatorio");
        }

        if (data.responsavel() == null) {
            throw new ValidacaoException("Responsavel e obrigatorio");
        }

        normalizarObrigatorio(data.titulo(), "Titulo da tarefa e obrigatorio");

        validarNaoNegativo(data.horasEstimadas(), "Horas estimadas nao podem ser negativas");
        validarNaoNegativo(data.horasRealizadas(), "Horas realizadas nao podem ser negativas");

        Integer percentual = normalizarPercentual(data.percentualConcluido());
        if (percentual < 0 || percentual > 100) {
            throw new ValidacaoException("Percentual concluido deve estar entre 0 e 100");
        }

        if (data.dataInicio() != null
                && data.dataFim() != null
                && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComPercentual(status, percentual);
        validarStatusComDatas(status, data.dataFim());

        validarPrioridade(normalizarPrioridade(data.prioridade()));
    }

    private void validarStatus(String status) {
        if (!status.equals("pendente")
                && !status.equals("em_andamento")
                && !status.equals("concluida")
                && !status.equals("cancelada")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarStatusComPercentual(String status, Integer percentual) {
        if (status.equals("concluida") && percentual < 100) {
            throw new ValidacaoException("Tarefa concluida deve ter percentual concluido igual a 100");
        }

        if (!status.equals("concluida") && percentual == 100) {
            throw new ValidacaoException("Percentual 100 so deve ser usado para tarefa concluida");
        }
    }

    private void validarStatusComDatas(String status, java.time.LocalDate dataFim) {
        if (status.equals("concluida") && dataFim == null) {
            throw new ValidacaoException("Data fim e obrigatoria quando a tarefa estiver concluida");
        }
    }

    private void validarPrioridade(Integer prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new ValidacaoException("Prioridade invalida");
        }
    }

    private void validarHierarquia(Projetos projeto, Tarefas tarefaPai, Integer tarefaAtualId) {
        if (tarefaPai == null) {
            return;
        }

        if (projeto == null) {
            throw new ValidacaoException("Projeto e obrigatorio quando houver tarefa pai");
        }

        if (tarefaPai.getProjeto() == null || !tarefaPai.getProjeto().getId().equals(projeto.getId())) {
            throw new ValidacaoException("Tarefa pai deve pertencer ao mesmo projeto");
        }

        if (tarefaAtualId != null && formaCiclo(tarefaPai, tarefaAtualId, new HashSet<>())) {
            throw new ValidacaoException("Nao e permitido criar ciclo na hierarquia de tarefas");
        }
    }

    private boolean formaCiclo(Tarefas tarefaPai, Integer tarefaAtualId, Set<Integer> visitados) {
        if (tarefaPai == null || tarefaPai.getId() == null || !visitados.add(tarefaPai.getId())) {
            return false;
        }

        if (tarefaPai.getId().equals(tarefaAtualId)) {
            return true;
        }

        return formaCiclo(tarefaPai.getTarefaPai(), tarefaAtualId, visitados);
    }

    private Projetos buscarProjeto(Integer projetoId) {
        if (projetoId == null) {
            return null;
        }

        return projetosRepository.findById(projetoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado"));
    }

    private Tarefas buscarTarefaPai(Integer tarefaPaiId, Integer tarefaAtualId) {
        if (tarefaPaiId == null) {
            return null;
        }

        if (tarefaAtualId != null && tarefaPaiId.equals(tarefaAtualId)) {
            throw new ValidacaoException("Tarefa nao pode ser pai dela mesma");
        }

        return repository.findById(tarefaPaiId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa pai nao encontrada"));
    }

    private Usuarios buscarResponsavel(Integer responsavelId) {
        return usuariosRepository.findById(responsavelId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsavel nao encontrado"));
    }

    private void recalcularProgressoProjeto(Projetos projeto) {
        if (projeto == null || projeto.getId() == null) {
            return;
        }

        long totalTarefas = repository.countByProjetoId(projeto.getId());
        if (totalTarefas == 0) {
            projeto.setStatus("planejado");
            return;
        }

        boolean possuiEmAndamento = repository.existsByProjetoIdAndStatus(projeto.getId(), "em_andamento");
        boolean possuiPendente = repository.existsByProjetoIdAndStatus(projeto.getId(), "pendente");
        boolean possuiConcluida = repository.existsByProjetoIdAndStatus(projeto.getId(), "concluida");
        boolean todasConcluidas = !repository.existsByProjetoIdAndStatusNot(projeto.getId(), "concluida");
        boolean todasCanceladas = !repository.existsByProjetoIdAndStatusNot(projeto.getId(), "cancelada");

        if (todasConcluidas) {
            projeto.setStatus("concluido");
            if (projeto.getDataFim() == null) {
                projeto.setDataFim(LocalDate.now());
            }
            return;
        }

        if (todasCanceladas) {
            projeto.setStatus("cancelado");
            if (projeto.getDataFim() == null) {
                projeto.setDataFim(LocalDate.now());
            }
            return;
        }

        if (possuiEmAndamento || possuiConcluida || possuiPendente) {
            projeto.setStatus("em_andamento");
        }
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private Integer normalizarPercentual(Integer percentual) {
        return percentual == null ? 0 : percentual;
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

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "pendente" : valor.toLowerCase();
    }

    private Integer normalizarPrioridade(Integer prioridade) {
        return prioridade == null ? 1 : prioridade;
    }
}
