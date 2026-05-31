package com.example.backend.pp.ordemProducao;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.pp.apontamentos.ApontamentosRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemProducaoServiceTest {

    @Mock
    private OrdemProducaoRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private ApontamentosRepository apontamentosRepository;

    @InjectMocks
    private OrdemProducaoService service;

    @Test
    void deveCriarOrdemComStatusEPrioridadePadrao() {
        OrdemProducaoRequestDTO request = new OrdemProducaoRequestDTO(
                " OP-001 ",
                1,
                new BigDecimal("10"),
                null,
                LocalDate.of(2026, 6, 1),
                null,
                null,
                LocalDate.of(2026, 6, 10),
                null,
                null,
                " observacao "
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto(1)));
        when(repository.save(any(OrdemProducao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemProducao saved = service.criar(request);

        assertEquals("planejada", saved.getStatus());
        assertEquals(1, saved.getPrioridade());
        assertEquals(BigDecimal.ZERO, saved.getQuantidadeProduzida());

        ArgumentCaptor<OrdemProducao> captor = ArgumentCaptor.forClass(OrdemProducao.class);
        verify(repository).save(captor.capture());
        assertEquals("OP-001", captor.getValue().getNumeroOp());
        assertEquals("observacao", captor.getValue().getObservacoes());
    }

    @Test
    void deveBloquearStatusEmProducaoSemQuantidadeProduzida() {
        OrdemProducaoRequestDTO request = new OrdemProducaoRequestDTO(
                null,
                1,
                new BigDecimal("10"),
                BigDecimal.ZERO,
                LocalDate.of(2026, 6, 1),
                null,
                null,
                null,
                "em_producao",
                2,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Ordem em producao deve ter quantidade produzida maior que zero", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeOrdemComApontamentos() {
        OrdemProducao entity = new OrdemProducao();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(apontamentosRepository.existsByOpId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir ordem de producao que possui apontamentos", exception.getMessage());
        verify(repository, never()).delete(any(OrdemProducao.class));
    }

    private Produtos criarProduto(Integer id) {
        Produtos produto = new Produtos();
        produto.setId(id);
        return produto;
    }
}
