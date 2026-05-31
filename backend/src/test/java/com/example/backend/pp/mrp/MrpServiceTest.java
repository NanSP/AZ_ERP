package com.example.backend.pp.mrp;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MrpServiceTest {

    @Mock
    private MrpRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;

    @InjectMocks
    private MrpService service;

    @Test
    void deveCalcularNecessidadeProducaoParaProduto() {
        MrpRequestDTO request = new MrpRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("100"),
                new BigDecimal("30"),
                new BigDecimal("10"),
                LocalDate.of(2026, 6, 10)
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1, "produto")));
        when(repository.save(any(Mrp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mrp saved = service.criar(request);

        assertEquals(new BigDecimal("80"), saved.getNecessidadeProducao());
        assertEquals(BigDecimal.ZERO, saved.getNecessidadeCompra());
    }

    @Test
    void deveCalcularNecessidadeCompraParaInsumo() {
        MrpRequestDTO request = new MrpRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("50"),
                new BigDecimal("15"),
                new BigDecimal("5"),
                LocalDate.of(2026, 6, 5)
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1, "insumo")));
        when(repository.save(any(Mrp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mrp saved = service.criar(request);

        assertEquals(BigDecimal.ZERO, saved.getNecessidadeProducao());
        assertEquals(new BigDecimal("40"), saved.getNecessidadeCompra());
    }

    @Test
    void deveBloquearDataNecessidadeAnteriorAoPeriodo() {
        MrpRequestDTO request = new MrpRequestDTO(
                1,
                LocalDate.of(2026, 6, 10),
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                LocalDate.of(2026, 6, 9)
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de necessidade nao pode ser anterior ao periodo", exception.getMessage());
    }

    private Produtos criarProduto(Integer id, String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(id);
        produto.setTipoItem(tipoItem);
        return produto;
    }
}
