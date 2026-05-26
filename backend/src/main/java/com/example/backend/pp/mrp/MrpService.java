package com.example.backend.pp.mrp;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MrpService {

    private final MrpRepository repository;
    private final ProdutosRepository produtosRepository;

    public MrpService(
            MrpRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public Mrp criar(MrpRequestDTO data) {
        validar(data);

        Produtos produto = buscarProduto(data.produto());

        Mrp entity = new Mrp();
        preencher(entity, data, produto, LocalDateTime.now());

        return repository.save(entity);
    }

    @Transactional
    public Mrp atualizar(Integer id, MrpRequestDTO data) {
        validar(data);

        Mrp entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("MRP nao encontrado"));

        Produtos produto = buscarProduto(data.produto());
        preencher(entity, data, produto, entity.getCreatedAt());

        return repository.save(entity);
    }

    @Transactional
    public void excluir(Integer id) {
        Mrp entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("MRP nao encontrado"));

        repository.delete(entity);
    }

    private void preencher(
            Mrp entity,
            MrpRequestDTO data,
            Produtos produto,
            LocalDateTime createdAt
    ) {
        BigDecimal demandaPrevista = zeroSeNulo(data.demandaPrevista());
        BigDecimal estoqueAtual = zeroSeNulo(data.estoqueAtual());
        BigDecimal estoqueSeguranca = zeroSeNulo(data.estoqueSeguranca());
        BigDecimal necessidadeLiquida = calcularNecessidadeLiquida(demandaPrevista, estoqueAtual, estoqueSeguranca);

        entity.setProduto(produto);
        entity.setPeriodo(data.periodo());
        entity.setDemandaPrevista(demandaPrevista);
        entity.setEstoqueAtual(estoqueAtual);
        entity.setEstoqueSeguranca(estoqueSeguranca);
        entity.setNecessidadeProducao(calcularNecessidadeProducao(produto, necessidadeLiquida));
        entity.setNecessidadeCompra(calcularNecessidadeCompra(produto, necessidadeLiquida));
        entity.setDataNecessidade(data.dataNecessidade());
        entity.setCreatedAt(createdAt);
    }

    private void validar(MrpRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do MRP sao obrigatorios");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.periodo() == null) {
            throw new ValidacaoException("Periodo e obrigatorio");
        }

        validarNaoNegativo(data.demandaPrevista(), "Demanda prevista nao pode ser negativa");
        validarNaoNegativo(data.estoqueAtual(), "Estoque atual nao pode ser negativo");
        validarNaoNegativo(data.estoqueSeguranca(), "Estoque de seguranca nao pode ser negativo");

        if (data.dataNecessidade() != null && data.dataNecessidade().isBefore(data.periodo())) {
            throw new ValidacaoException("Data de necessidade nao pode ser anterior ao periodo");
        }
    }

    private Produtos buscarProduto(Integer produtoId) {
        return produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    private void validarNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacaoException(mensagem);
        }
    }

    private BigDecimal calcularNecessidadeLiquida(
            BigDecimal demandaPrevista,
            BigDecimal estoqueAtual,
            BigDecimal estoqueSeguranca
    ) {
        return demandaPrevista.add(estoqueSeguranca).subtract(estoqueAtual).max(BigDecimal.ZERO);
    }

    private BigDecimal calcularNecessidadeProducao(
            Produtos produto,
            BigDecimal necessidadeLiquida
    ) {
        String tipoItem = normalizarTipoItem(produto);

        if (tipoItem.equals("produto")) {
            return necessidadeLiquida;
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calcularNecessidadeCompra(
            Produtos produto,
            BigDecimal necessidadeLiquida
    ) {
        String tipoItem = normalizarTipoItem(produto);

        if (tipoItem.equals("insumo") || tipoItem.equals("embalagem") || tipoItem.equals("servico")) {
            return necessidadeLiquida;
        }

        return BigDecimal.ZERO;
    }

    private String normalizarTipoItem(Produtos produto) {
        if (produto == null || produto.getTipoItem() == null) {
            return "";
        }

        return produto.getTipoItem().trim().toLowerCase();
    }

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
