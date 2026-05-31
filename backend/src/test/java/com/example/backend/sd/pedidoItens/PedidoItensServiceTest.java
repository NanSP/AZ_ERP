package com.example.backend.sd.pedidoItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoItensServiceTest {

    @Mock
    private PedidoItensRepository repository;
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private ProdutosRepository produtosRepository;

    @InjectMocks
    private PedidoItensService service;

    @Test
    void deveCriarItemCalculandoValorTotal() {
        PedidoItensRequestDTO request = new PedidoItensRequestDTO(
                1,
                2,
                new BigDecimal("3"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00")
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(criarPedido("aberto")));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProdutoValido("produto")));
        when(repository.save(any(PedidoItens.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.sumValorTotalByPedidoId(1)).thenReturn(new BigDecimal("25.00"));
        when(repository.sumDescontoByPedidoId(1)).thenReturn(new BigDecimal("5.00"));

        PedidoItens saved = service.criar(request);

        assertEquals(new BigDecimal("25.00"), saved.getValorTotal());

        ArgumentCaptor<PedidoItens> captor = ArgumentCaptor.forClass(PedidoItens.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("10.00"), captor.getValue().getValorUnitario());
    }

    @Test
    void deveBloquearProdutoNaoPermitidoEmItemDePedido() {
        PedidoItensRequestDTO request = new PedidoItensRequestDTO(
                1,
                2,
                new BigDecimal("3"),
                new BigDecimal("10.00"),
                BigDecimal.ZERO
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(criarPedido("aberto")));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProdutoValido("insumo")));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Produto do tipo insumo ou embalagem nao pode ser usado em item de pedido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeItemDePedidoFaturado() {
        PedidoItens entity = new PedidoItens();
        entity.setId(10);
        entity.setPedido(criarPedido("faturado"));

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido alterar itens de pedido faturado", exception.getMessage());
        verify(repository, never()).delete(any(PedidoItens.class));
    }

    private Pedidos criarPedido(String status) {
        Pedidos pedido = new Pedidos();
        pedido.setId(1);
        pedido.setStatus(status);
        return pedido;
    }

    private Produtos criarProdutoValido(String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(2);
        produto.setSituacao("ativo");
        produto.setTipoItem(tipoItem);
        return produto;
    }
}
