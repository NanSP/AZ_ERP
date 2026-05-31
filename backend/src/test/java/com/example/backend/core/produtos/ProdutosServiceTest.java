package com.example.backend.core.produtos;

import com.example.backend.mm.compraItens.CompraItensRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.mm.materiais.MateriaisRepository;
import com.example.backend.pp.bom.BomRepository;
import com.example.backend.qm.inspecoes.InspecoesRepository;
import com.example.backend.sd.pedidoItens.PedidoItensRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
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
class ProdutosServiceTest {

    @Mock
    private ProdutosRepository repository;
    @Mock
    private EstoquesRepository estoquesRepository;
    @Mock
    private CompraItensRepository compraItensRepository;
    @Mock
    private PedidoItensRepository pedidoItensRepository;
    @Mock
    private OrdensServicoRepository ordensServicoRepository;
    @Mock
    private InspecoesRepository inspecoesRepository;
    @Mock
    private MateriaisRepository materiaisRepository;
    @Mock
    private BomRepository bomRepository;

    @InjectMocks
    private ProdutosService service;

    @Test
    void deveCriarProdutoComDefaultsENormalizacao() {
        ProdutosRequestDTO request = new ProdutosRequestDTO(
                " PROD-01 ",
                null,
                " Produto Teste ",
                " descricao ",
                null,
                " UN ",
                "12345678",
                "1234567",
                new BigDecimal("2.50"),
                new BigDecimal("2.00"),
                null,
                null
        );

        when(repository.save(any(Produtos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produtos saved = service.criar(request);

        assertEquals("PROD-01", saved.getCodigo());
        assertEquals("Produto Teste", saved.getNome());
        assertEquals("produto", saved.getTipoItem());
        assertEquals(0, saved.getOrigem());
        assertEquals("ativo", saved.getSituacao());

        ArgumentCaptor<Produtos> captor = ArgumentCaptor.forClass(Produtos.class);
        verify(repository).save(captor.capture());
        assertEquals("UN", captor.getValue().getUnidadeMedida());
    }

    @Test
    void deveBloquearAlteracaoDeTipoQuandoProdutoEstiverEmUso() {
        Produtos entity = new Produtos();
        entity.setId(1);
        entity.setCodigo("PROD-01");
        entity.setNome("Produto Teste");
        entity.setTipoItem("produto");
        entity.setUnidadeMedida("UN");
        entity.setOrigem(0);

        ProdutosRequestDTO request = new ProdutosRequestDTO(
                "PROD-01",
                null,
                "Produto Teste",
                null,
                "servico",
                "UN",
                null,
                null,
                null,
                null,
                0,
                "ativo"
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(estoquesRepository.existsByProdutoId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(1, request));

        assertEquals("Nao e permitido alterar o tipo do produto que ja possui uso operacional", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoProdutoEstiverEmUso() {
        Produtos entity = new Produtos();
        entity.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(pedidoItensRepository.existsByProdutoId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir produto com uso operacional", exception.getMessage());
        verify(repository, never()).delete(any(Produtos.class));
    }
}
