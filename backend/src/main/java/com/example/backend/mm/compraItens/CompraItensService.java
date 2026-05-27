package com.example.backend.mm.compraItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.compras.Compras;
import com.example.backend.mm.compras.ComprasRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CompraItensService {

    private final CompraItensRepository repository;
    private final ComprasRepository comprasRepository;
    private final ProdutosRepository produtosRepository;

    public CompraItensService(
            CompraItensRepository repository,
            ComprasRepository comprasRepository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.comprasRepository = comprasRepository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public CompraItens criar(CompraItensRequestDTO data) {
        validar(data);

        Compras compra = buscarCompra(data.compras());
        Produtos produto = buscarProduto(data.produtos());

        validarCompraParaItens(compra);

        CompraItens entity = new CompraItens();
        preencher(entity, data, compra, produto, LocalDateTime.now());

        CompraItens saved = repository.save(entity);
        recalcularCompra(compra);

        return saved;
    }

    @Transactional
    public CompraItens atualizar(Integer id, CompraItensRequestDTO data) {
        validar(data);

        CompraItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da compra nao encontrado"));

        Compras compraAnterior = entity.getCompras();
        Compras compra = buscarCompra(data.compras());
        Produtos produto = buscarProduto(data.produtos());

        if (compraAnterior != null && compraAnterior.getId() != null
                && !compraAnterior.getId().equals(compra.getId())) {
            validarCompraParaItens(compraAnterior);
        }

        validarCompraParaItens(compra);

        preencher(entity, data, compra, produto, entity.getCreatedAt());

        CompraItens updated = repository.save(entity);
        recalcularCompra(compra);

        if (compraAnterior != null && compraAnterior.getId() != null
                && !compraAnterior.getId().equals(compra.getId())) {
            recalcularCompra(compraAnterior);
        }

        return updated;
    }

    @Transactional
    public void excluir(Integer id) {
        CompraItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da compra nao encontrado"));

        Compras compra = entity.getCompras();
        validarCompraParaItens(compra);

        repository.delete(entity);
        recalcularCompra(compra);
    }

    private void preencher(
        CompraItens entity,
        CompraItensRequestDTO data,
        Compras compra,
        Produtos produto,
        LocalDateTime createdAt
    ) {
        BigDecimal quantidade = data.quantidade();
        BigDecimal valorUnitario = data.valorUnitario();
        BigDecimal quantidadeRecebida = zeroSeNulo(data.quantidadeRecebida());

        entity.setCompras(compra);
        entity.setProdutos(produto);
        entity.setQuantidade(quantidade);
        entity.setValorUnitario(valorUnitario);
        entity.setQuantidadeRecebida(quantidadeRecebida);
        entity.setValorTotal(calcularValorTotal(quantidade, valorUnitario));
        entity.setCreatedAt(createdAt);
    }

    private void validar(CompraItensRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do item da compra sao obrigatorios");
        }

        if (data.compras() == null) {
            throw new ValidacaoException("Compra e obrigatoria");
        }

        if (data.produtos() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.quantidade() == null || data.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Quantidade deve ser maior que zero");
        }

        validarNaoNegativo(data.valorUnitario(), "Valor unitario nao pode ser negativo");
        validarNaoNegativo(data.quantidadeRecebida(), "Quantidade recebida nao pode ser negativa");

        if (data.quantidadeRecebida() != null
                && data.quantidadeRecebida().compareTo(data.quantidade()) > 0) {
            throw new ValidacaoException("Quantidade recebida nao pode ser maior que a quantidade comprada");
        }
    }

    private void validarCompraParaItens(Compras compra) {
        String status = compra.getStatus() == null ? null : compra.getStatus().trim().toLowerCase();

        if ("recebido".equals(status)) {
            throw new ValidacaoException("Nao e permitido alterar itens de compra recebida");
        }

        if ("cancelado".equals(status)) {
            throw new ValidacaoException("Nao e permitido alterar itens de compra cancelada");
        }
    }

    private Compras buscarCompra(Integer compraId) {
        return comprasRepository.findById(compraId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra nao encontrada"));
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

    private BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal calcularValorTotal(BigDecimal quantidade, BigDecimal valorUnitario) {
        if (valorUnitario == null) {
            return null;
        }

        return quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_UP);
    }

    private void recalcularCompra(Compras compra) {
        if (compra == null || compra.getId() == null) {
            return;
        }

        BigDecimal valorTotal = repository.sumValorTotalByCompraId(compra.getId());
        compra.setValorTotal(valorTotal);

        if (!repository.existsByComprasId(compra.getId())) {
            compra.setStatus("aberto");
            compra.setDataEntrega(null);
            return;
        }

        if (!repository.existsByComprasIdAndQuantidadeRecebidaGreaterThan(compra.getId(), BigDecimal.ZERO)) {
            compra.setStatus("aberto");
            compra.setDataEntrega(null);
            return;
        }

        if (repository.existsByComprasIdAndQuantidadeRecebidaLessThanQuantidade(compra.getId())) {
            compra.setStatus("parcial");
            return;
        }

        compra.setStatus("recebido");
        if (compra.getDataEntrega() == null) {
            compra.setDataEntrega(java.time.LocalDate.now());
        }
    }
}
