package com.example.backend.sd.pedidoItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import com.example.backend.shared.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PedidoItensService {

    private final PedidoItensRepository repository;
    private final PedidosRepository pedidosRepository;
    private final ProdutosRepository produtosRepository;

    public PedidoItensService(
            PedidoItensRepository repository,
            PedidosRepository pedidosRepository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public PedidoItens criar(PedidoItensRequestDTO data) {
        validar(data);

        Pedidos pedido = buscarPedido(data.pedido());
        Produtos produto = buscarProduto(data.produto());

        validarPedidoParaItens(pedido);

        PedidoItens entity = new PedidoItens();
        preencher(entity, data, pedido, produto, LocalDateTime.now());

        PedidoItens saved = repository.save(entity);
        recalcularValorTotalPedido(pedido);

        return saved;
    }

    @Transactional
    public PedidoItens atualizar(Integer id, PedidoItensRequestDTO data) {
        validar(data);

        PedidoItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do pedido nao encontrado"));

        Pedidos pedidoAnterior = entity.getPedido();
        Pedidos pedido = buscarPedido(data.pedido());
        Produtos produto = buscarProduto(data.produto());

        if (pedidoAnterior != null && pedidoAnterior.getId() != null
                && !pedidoAnterior.getId().equals(pedido.getId())) {
            validarPedidoParaItens(pedidoAnterior);
        }

        validarPedidoParaItens(pedido);

        preencher(entity, data, pedido, produto, entity.getCreatedAt());

        PedidoItens updated = repository.save(entity);
        recalcularValorTotalPedido(pedido);

        if (pedidoAnterior != null && pedidoAnterior.getId() != null
                && !pedidoAnterior.getId().equals(pedido.getId())) {
            recalcularValorTotalPedido(pedidoAnterior);
        }

        return updated;
    }

    @Transactional
    public void excluir(Integer id) {
        PedidoItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do pedido nao encontrado"));

        Pedidos pedido = entity.getPedido();
        validarPedidoParaItens(pedido);

        repository.delete(entity);
        recalcularValorTotalPedido(pedido);
    }

    private void preencher(
            PedidoItens entity,
            PedidoItensRequestDTO data,
            Pedidos pedido,
            Produtos produto,
            LocalDateTime createdAt
    ) {
        BigDecimal quantidade = data.quantidade();
        BigDecimal valorUnitario = zeroSeNulo(data.valorUnitario());
        BigDecimal desconto = zeroSeNulo(data.desconto());

        entity.setPedido(pedido);
        entity.setProduto(produto);
        entity.setQuantidade(quantidade);
        entity.setValorUnitario(valorUnitario);
        entity.setDesconto(desconto);
        entity.setValorTotal(calcularValorTotal(quantidade, valorUnitario, desconto));
        entity.setCreatedAt(createdAt);
    }

    private void validar(PedidoItensRequestDTO data) {
        if (data == null) {
            throw new ValidacaoException("Dados do item do pedido sao obrigatorios");
        }

        if (data.pedido() == null) {
            throw new ValidacaoException("Pedido e obrigatorio");
        }

        if (data.produto() == null) {
            throw new ValidacaoException("Produto e obrigatorio");
        }

        if (data.quantidade() == null || data.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Quantidade deve ser maior que zero");
        }

        validarNaoNegativo(data.valorUnitario(), "Valor unitario nao pode ser negativo");
        validarNaoNegativo(data.desconto(), "Desconto nao pode ser negativo");

        if (data.valorUnitario() != null && data.desconto() != null) {
            BigDecimal subtotal = data.quantidade().multiply(data.valorUnitario());
            if (data.desconto().compareTo(subtotal) > 0) {
                throw new ValidacaoException("Desconto nao pode ser maior que o subtotal do item");
            }
        }
    }

    private void validarPedidoParaItens(Pedidos pedido) {
        String status = pedido.getStatus() == null ? null : pedido.getStatus().trim().toLowerCase();

        if ("faturado".equals(status)) {
            throw new ValidacaoException("Nao e permitido alterar itens de pedido faturado");
        }

        if ("cancelado".equals(status)) {
            throw new ValidacaoException("Nao e permitido alterar itens de pedido cancelado");
        }
    }

    private Pedidos buscarPedido(Integer pedidoId) {
        return pedidosRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));
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

    private BigDecimal calcularValorTotal(
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal desconto
    ) {
        return quantidade.multiply(valorUnitario)
                .subtract(desconto)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void recalcularValorTotalPedido(Pedidos pedido) {
        if (pedido == null || pedido.getId() == null) {
            return;
        }

        pedido.setValorTotal(repository.sumValorTotalByPedidoId(pedido.getId()));
    }
}
