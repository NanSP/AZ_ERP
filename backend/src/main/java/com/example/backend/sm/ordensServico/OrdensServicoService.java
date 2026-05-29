package com.example.backend.sm.ordensServico;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.sm.atendimentos.AtendimentosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrdensServicoService {

    private final OrdensServicoRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ProdutosRepository produtosRepository;
    private final ColaboradoresRepository colaboradoresRepository;
    private final AtendimentosRepository atendimentosRepository;

    public OrdensServicoService(
            OrdensServicoRepository repository,
            ParceirosRepository parceirosRepository,
            ProdutosRepository produtosRepository,
            ColaboradoresRepository colaboradoresRepository,
            AtendimentosRepository atendimentosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.produtosRepository = produtosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
        this.atendimentosRepository = atendimentosRepository;
    }

    @Transactional
    public OrdensServico criar(OrdensServicoRequestDTO data) {
        validar(data, null);
        validarNumeroOsDuplicadoParaCriacao(normalizarOpcional(data.numeroOs()));

        Parceiros cliente = buscarCliente(data.cliente());
        Produtos produto = buscarProduto(data.produto());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        OrdensServico entity = new OrdensServico();
        preencher(entity, data, cliente, produto, tecnico, null, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public OrdensServico atualizar(Integer id, OrdensServicoRequestDTO data) {
        OrdensServico entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada"));

        validar(data, entity.getDataAbertura());
        validarNumeroOsDuplicadoParaAtualizacao(normalizarOpcional(data.numeroOs()), id);

        Parceiros cliente = buscarCliente(data.cliente());
        Produtos produto = buscarProduto(data.produto());
        Colaboradores tecnico = buscarTecnico(data.tecnico());

        preencher(entity, data, cliente, produto, tecnico, entity.getDataAbertura(), entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        OrdensServico entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada"));

        if (atendimentosRepository.existsByOsId(id)) {
            throw new ValidacaoException("Nao e permitido excluir ordem de servico que possui atendimentos");
        }

        repository.delete(entity);
    }

    private void preencher(
            OrdensServico entity,
            OrdensServicoRequestDTO data,
            Parceiros cliente,
            Produtos produto,
            Colaboradores tecnico,
            LocalDateTime dataAberturaAtual,
            LocalDateTime createdAt
    ) {
        entity.setNumeroOs(normalizarOpcional(data.numeroOs()));
        entity.setCliente(cliente);
        entity.setProduto(produto);
        entity.setTipoServico(normalizarOpcional(data.tipoServico()));
        entity.setDescricaoProblema(normalizarOpcional(data.descricaoProblema()));
        entity.setPrioridade(normalizarPrioridade(data.prioridade()));
        entity.setDataAbertura(definirDataAbertura(data.dataAbertura(), dataAberturaAtual));
        entity.setDataAgendamento(data.dataAgendamento());
        entity.setDataInicio(data.dataInicio());
        entity.setDataFim(data.dataFim());
        entity.setTecnico(tecnico);
        entity.setStatus(normalizarStatus(data.status()));
        entity.setCreatedAt(createdAt);
    }

    private void validar(OrdensServicoRequestDTO data, LocalDateTime dataAberturaAtual) {
        if (data == null) {
            throw new ValidacaoException("Dados da ordem de servico sao obrigatorios");
        }

        if (data.cliente() == null) {
            throw new ValidacaoException("Cliente e obrigatorio");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.tecnico() == null) {
            throw new ValidacaoException("Tecnico e obrigatorio");
        }

        LocalDateTime dataAberturaEfetiva = definirDataAbertura(data.dataAbertura(), dataAberturaAtual);

        String prioridade = normalizarPrioridade(data.prioridade());
        validarPrioridade(prioridade);

        String status = normalizarStatus(data.status());
        validarStatus(status);

        if (data.dataInicio() != null && data.dataFim() != null && data.dataFim().isBefore(data.dataInicio())) {
            throw new ValidacaoException("Data fim nao pode ser anterior a data inicio");
        }

        if (data.dataAgendamento() != null
                && data.dataAgendamento().isBefore(dataAberturaEfetiva.toLocalDate())) {
            throw new ValidacaoException("Data de agendamento nao pode ser anterior a data de abertura");
        }

        if (status.equals("concluida") && data.dataFim() == null) {
            throw new ValidacaoException("Data fim e obrigatoria quando a ordem de servico estiver concluida");
        }

        if (!status.equals("concluida") && data.dataFim() != null) {
            throw new ValidacaoException("Data fim so deve ser informada quando a ordem de servico estiver concluida");
        }
    }

    private void validarPrioridade(String prioridade) {
        if (!prioridade.equals("baixa")
                && !prioridade.equals("normal")
                && !prioridade.equals("alta")
                && !prioridade.equals("critica")) {
            throw new ValidacaoException("Prioridade invalida");
        }
    }

    private void validarStatus(String status) {
        if (!status.equals("aberta")
                && !status.equals("em_andamento")
                && !status.equals("concluida")
                && !status.equals("cancelada")) {
            throw new ValidacaoException("Status invalido");
        }
    }

    private Parceiros buscarCliente(Integer clienteId) {
        Parceiros cliente = parceirosRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        String situacao = cliente.getSituacao() == null ? null : cliente.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Cliente precisa estar ativo para ordem de servico");
        }

        String tipoParceiro = cliente.getTipoParceiro() == null ? null : cliente.getTipoParceiro().trim().toLowerCase();
        if (!"cliente".equals(tipoParceiro)) {
            throw new ValidacaoException("Parceiro informado precisa ser do tipo cliente");
        }

        return cliente;
    }

    private Produtos buscarProduto(Integer produtoId) {
        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        String situacao = produto.getSituacao() == null ? null : produto.getSituacao().trim().toLowerCase();
        if (!"ativo".equals(situacao)) {
            throw new ValidacaoException("Produto precisa estar ativo para ordem de servico");
        }

        String tipoItem = produto.getTipoItem() == null ? null : produto.getTipoItem().trim().toLowerCase();
        if ("insumo".equals(tipoItem) || "embalagem".equals(tipoItem)) {
            throw new ValidacaoException("Produto do tipo insumo ou embalagem nao pode ser usado em ordem de servico");
        }

        return produto;
    }

    private Colaboradores buscarTecnico(Integer tecnicoId) {
        return colaboradoresRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tecnico nao encontrado"));
    }

    private void validarNumeroOsDuplicadoParaCriacao(String numeroOs) {
        if (numeroOs != null && repository.existsByNumeroOs(numeroOs)) {
            throw new ValidacaoException("Ja existe uma ordem de servico com o numero informado");
        }
    }

    private void validarNumeroOsDuplicadoParaAtualizacao(String numeroOs, Integer id) {
        if (numeroOs != null && repository.existsByNumeroOsAndIdNot(numeroOs, id)) {
            throw new ValidacaoException("Ja existe uma ordem de servico com o numero informado");
        }
    }

    private String normalizarPrioridade(String prioridade) {
        String valor = normalizarOpcional(prioridade);
        return valor == null ? "normal" : valor.toLowerCase();
    }

    private String normalizarStatus(String status) {
        String valor = normalizarOpcional(status);
        return valor == null ? "aberta" : valor.toLowerCase();
    }

    private LocalDateTime definirDataAbertura(LocalDateTime dataAberturaInformada, LocalDateTime dataAberturaAtual) {
        if (dataAberturaInformada != null) {
            return dataAberturaInformada;
        }

        if (dataAberturaAtual != null) {
            return dataAberturaAtual;
        }

        return LocalDateTime.now();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isBlank() ? null : normalizado;
    }
}
