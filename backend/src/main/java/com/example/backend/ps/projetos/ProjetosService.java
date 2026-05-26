package com.example.backend.ps.projetos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ProjetosService {

    private final ProjetosRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final UsuariosRepository usuariosRepository;

    public ProjetosService(
            ProjetosRepository repository,
            ParceirosRepository parceirosRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @Transactional
    public Projetos criar(ProjetosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaCriacao(normalizarOpcional(data.codigo()));

        Parceiros cliente = buscarCliente(data.cliente());
        Usuarios gerente = buscarGerente(data.gerente());

        Projetos entity = new Projetos();
        preencher(entity, data, cliente, gerente, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Projetos atualizar(Integer id, ProjetosRequestDTO data) {
        validar(data);
        validarCodigoDuplicadoParaAtualizacao(normalizarOpcional(data.codigo()), id);

        Projetos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado"));

        Parceiros cliente = buscarCliente(data.cliente());
        Usuarios gerente = buscarGerente(data.gerente());

        preencher(entity, data, cliente, gerente, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Projetos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado"));

        if (entity.getTarefas() != null && !entity.getTarefas().isEmpty()) {
            throw new ValidacaoException("Nao e permitido excluir projeto que possui tarefas");
        }

        repository.delete(entity);
    }

    private void preencher(
            Projetos entity,
            ProjetosRequestDTO data,
            Parceiros cliente,
            Usuarios gerente,
            LocalDateTime createdAt
    ) {
        entity.setCodigo(normalizarOpcional(data.codigo()));
        entity.setNome(normalizarObrigatorio(data.nome(), "Nome do projeto e obrigatorio"));
        entity.setDescricao(normalizarOpcional(data.descricao()));
        entity.setCliente(cliente);
        entity.setGerente(gerente);
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setDataPrevistaInicio(data.dataPrevistaInicio());
        entity.setDataPrevistaFim(data.dataPrevistaFim());
        entity.setOrcamentoTotal(zeroSeNulo(data.orcamentoTotal()));
        entity.setOrcamentoGasto(zeroSeNulo(data.orcamentoGasto()));
        entity.setStatus(normalizarStatus(data.status()));
        entity.setPrioridade(normalizarPrioridade(data.prioridade()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(ProjetosRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do projeto sao obrigatorios");
        }

        normalizarObrigatorio(data.nome(), "Nome do projeto e obrigatorio");

        if (data.gerente() == null) {
            throw new ValidacaoException("Gerente e obrigatorio");
        }

        validarNaoNegativo(data.orcamentoTotal(), "Orcamento total nao pode ser negativo");
        validarNaoNegativo(data.orcamentoGasto(), "Orcamento gasto nao pode ser negativo");

        if (data.orcamentoTotal() != null
                && data.orcamentoGasto() != null
                && data.orcamentoGasto().compareTo(data.orcamentoTotal()) > 0) {
            throw new ValidacaoException("Orcamento gasto nao pode ser maior que o orcamento total");
        }

        if (data.dataInicio() != null
                && data.dataFim() != null
                && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        if (data.dataPrevistaInicio() != null
                && data.dataPrevistaFim() != null
                && data.dataPrevistaFim().isBefore(data.dataPrevistaInicio())) {
            throw new ValidacaoException("Data prevista fim nao pode ser anterior a data prevista inicio");
        }

        String status = normalizarStatus(data.status());
        validarStatus(status);
        validarStatusComDatas(status, data.dataFim());
        validarPrioridade(normalizarPrioridade(data.prioridade()));
    }

    private void validarStatus(String status) {
        if (!status.equals("planejado")
                && !status.equals("em_andamento")
                && !status.equals("concluido")
                && !status.equals("cancelado")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private void validarPrioridade(Integer prioridade) {
        if (prioridade < 1 || prioridade > 5) {
            throw new ValidacaoException("Prioridade invalida");
        }
    }

    private void validarStatusComDatas(String status, java.time.LocalDate dataFim) {
        if ((status.equals("concluido") || status.equals("cancelado")) && dataFim == null) {
            throw new ValidacaoException("Data fim e obrigatoria quando o projeto estiver concluido ou cancelado");
        }
    }

    private void validarCodigoDuplicadoParaCriacao(String codigo) {
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new ValidacaoException("Ja existe um projeto com o codigo informado");
        }
    }

    private void validarCodigoDuplicadoParaAtualizacao(String codigo, Integer id) {
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ValidacaoException("Ja existe um projeto com o codigo informado");
        }
    }

    private Parceiros buscarCliente(Integer clienteId) {
        if (clienteId == null) {
            return null;
        }

        return parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private Usuarios buscarGerente(Integer gerenteId) {
        if (gerenteId == null) {
            return null;
        }

        return usuariosRepository.findById(gerenteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Gerente nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
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
        return valor == null ? "planejado" : valor.toLowerCase();
    }

    private Integer normalizarPrioridade(Integer prioridade) {
        return prioridade == null ? 1 : prioridade;
    }
}
