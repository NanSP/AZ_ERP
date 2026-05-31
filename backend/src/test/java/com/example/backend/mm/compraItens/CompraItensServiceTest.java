package com.example.backend.mm.compraItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.compras.Compras;
import com.example.backend.mm.compras.ComprasRepository;
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
class CompraItensServiceTest {

    @Mock
    private CompraItensRepository repository;
    @Mock
    private ComprasRepository comprasRepository;
    @Mock
    private ProdutosRepository produtosRepository;

    @InjectMocks
    private CompraItensService service;

    @Test
    void deveCriarItemDaCompraCalculandoValorTotal() {
        CompraItensRequestDTO request = new CompraItensRequestDTO(
                1,
                2,
                new BigDecimal("4"),
                new BigDecimal("12.50"),
                new BigDecimal("1")
        );

        when(comprasRepository.findById(1)).thenReturn(Optional.of(criarCompra("aberto")));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProduto("produto")));
        when(repository.save(any(CompraItens.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.sumValorTotalByCompraId(1)).thenReturn(new BigDecimal("50.00"));
        when(repository.existsByComprasId(1)).thenReturn(true);
        when(repository.existsByComprasIdAndQuantidadeRecebidaGreaterThan(1, BigDecimal.ZERO)).thenReturn(true);
        when(repository.existsByComprasIdAndQuantidadeRecebidaLessThanQuantidade(1)).thenReturn(true);

        CompraItens saved = service.criar(request);

        assertEquals(new BigDecimal("50.00"), saved.getValorTotal());

        ArgumentCaptor<CompraItens> captor = ArgumentCaptor.forClass(CompraItens.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("1"), captor.getValue().getQuantidadeRecebida());
    }

    @Test
    void deveBloquearQuantidadeRecebidaMaiorQueComprada() {
        CompraItensRequestDTO request = new CompraItensRequestDTO(
                1,
                2,
                new BigDecimal("4"),
                new BigDecimal("12.50"),
                new BigDecimal("5")
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Quantidade recebida nao pode ser maior que a quantidade comprada", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeItemDeCompraRecebida() {
        CompraItens entity = new CompraItens();
        entity.setId(10);
        entity.setCompras(criarCompra("recebido"));

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido alterar itens de compra recebida", exception.getMessage());
        verify(repository, never()).delete(any(CompraItens.class));
    }

    private Compras criarCompra(String status) {
        Compras compra = new Compras();
        compra.setId(1);
        compra.setStatus(status);
        return compra;
    }

    private Produtos criarProduto(String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(2);
        produto.setSituacao("ativo");
        produto.setTipoItem(tipoItem);
        return produto;
    }
}
