package com.example.backend.mm.compras;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.mm.compraItens.CompraItensRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComprasServiceTest {

    @Mock
    private ComprasRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private CompraItensRepository compraItensRepository;

    @InjectMocks
    private ComprasService service;

    @Test
    void deveCriarCompraComStatusPadrao() {
        ComprasRequestDTO request = new ComprasRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 10),
                null,
                new BigDecimal("1000.00"),
                " 30 dias ",
                null,
                " observacao "
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarFornecedorAtivo()));
        when(repository.save(any(Compras.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Compras saved = service.criar(request);

        assertEquals("aberto", saved.getStatus());
        assertEquals("30 dias", saved.getCondicoesPagamento());
        assertEquals("observacao", saved.getObservacoes());

        ArgumentCaptor<Compras> captor = ArgumentCaptor.forClass(Compras.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("1000.00"), captor.getValue().getValorTotal());
    }

    @Test
    void deveBloquearCompraComParceiroNaoFornecedor() {
        Parceiros parceiro = criarFornecedorAtivo();
        parceiro.setTipoParceiro("cliente");

        ComprasRequestDTO request = new ComprasRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                null,
                null,
                new BigDecimal("1000.00"),
                null,
                null,
                null
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(parceiro));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Parceiro informado precisa ser do tipo fornecedor", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeCompraComItens() {
        Compras entity = new Compras();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(compraItensRepository.existsByComprasId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir compra que possui itens", exception.getMessage());
        verify(repository, never()).delete(any(Compras.class));
        assertNull(entity.getDataEntrega());
    }

    private Parceiros criarFornecedorAtivo() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(1);
        parceiro.setSituacao("ativo");
        parceiro.setTipoParceiro("fornecedor");
        return parceiro;
    }
}
