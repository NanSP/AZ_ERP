package com.example.backend.pp.bom;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BomServiceTest {

    @Mock
    private BomRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;

    @InjectMocks
    private BomService service;

    @Test
    void deveCriarBomComUnidadeMedidaNormalizada() {
        BomRequestDTO request = new BomRequestDTO(
                1,
                2,
                new BigDecimal("2.50"),
                " kg ",
                1,
                new BigDecimal("1.25"),
                new BigDecimal("4.00"),
                10
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1)));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProduto(2)));
        when(repository.existsByProdutoPaiIdAndComponenteId(1, 2)).thenReturn(false);
        when(repository.findByProdutoPaiId(2)).thenReturn(List.of());
        when(repository.save(any(Bom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bom saved = service.criar(request);

        assertEquals("kg", saved.getUnidadeMedida());
        assertEquals(new BigDecimal("2.50"), saved.getQuantidade());

        ArgumentCaptor<Bom> captor = ArgumentCaptor.forClass(Bom.class);
        verify(repository).save(captor.capture());
        assertEquals(1, captor.getValue().getProdutoPai().getId());
        assertEquals(2, captor.getValue().getComponente().getId());
    }

    @Test
    void deveBloquearBomDuplicadaParaMesmoProdutoPaiEComponente() {
        BomRequestDTO request = new BomRequestDTO(
                1,
                2,
                BigDecimal.ONE,
                null,
                null,
                null,
                null,
                null
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1)));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProduto(2)));
        when(repository.existsByProdutoPaiIdAndComponenteId(1, 2)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Ja existe composicao BOM para o produto pai e componente informados", exception.getMessage());
    }

    @Test
    void deveBloquearCriacaoDeCicloNaEstruturaBom() {
        BomRequestDTO request = new BomRequestDTO(
                1,
                2,
                BigDecimal.ONE,
                null,
                null,
                null,
                null,
                null
        );

        Bom estruturaFilha = new Bom();
        estruturaFilha.setComponente(criarProduto(1));

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1)));
        when(produtosRepository.findById(2)).thenReturn(Optional.of(criarProduto(2)));
        when(repository.existsByProdutoPaiIdAndComponenteId(1, 2)).thenReturn(false);
        when(repository.findByProdutoPaiId(2)).thenReturn(List.of(estruturaFilha));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido criar ciclo na estrutura da BOM", exception.getMessage());
    }

    private Produtos criarProduto(Integer id) {
        Produtos produto = new Produtos();
        produto.setId(id);
        return produto;
    }
}
