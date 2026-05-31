package com.example.backend.mm.estoques;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.movimentacoes.MovimentacoesRepository;
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
class EstoquesServiceTest {

    @Mock
    private EstoquesRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private EmpresasRepository empresasRepository;
    @Mock
    private MovimentacoesRepository movimentacoesRepository;

    @InjectMocks
    private EstoquesService service;

    @Test
    void deveCriarEstoqueComCamposNormalizados() {
        EstoquesRequestDTO request = new EstoquesRequestDTO(
                1,
                2,
                " A1 ",
                " LOTE-01 ",
                new BigDecimal("10.00"),
                new BigDecimal("2.00"),
                new BigDecimal("20.00"),
                new BigDecimal("15.50"),
                LocalDate.of(2026, 12, 31)
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProdutoValido("produto")));
        when(empresasRepository.findById(2)).thenReturn(Optional.of(criarEmpresaAtiva()));
        when(repository.save(any(Estoques.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Estoques saved = service.criar(request);

        assertEquals("A1", saved.getLocalizacao());
        assertEquals("LOTE-01", saved.getLote());
        assertEquals(new BigDecimal("10.00"), saved.getQuantidade());

        ArgumentCaptor<Estoques> captor = ArgumentCaptor.forClass(Estoques.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("15.50"), captor.getValue().getValorUnitario());
    }

    @Test
    void deveBloquearEstoqueParaProdutoServico() {
        EstoquesRequestDTO request = new EstoquesRequestDTO(
                1,
                2,
                null,
                null,
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("15.50"),
                null
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProdutoValido("servico")));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Produto do tipo servico nao pode gerar estoque", exception.getMessage());
    }

    @Test
    void deveBloquearAlteracaoDeIdentificacaoAposMovimentacoes() {
        Estoques entity = new Estoques();
        entity.setId(10);
        entity.setProduto(criarProdutoValido("produto"));
        entity.getProduto().setId(1);
        entity.setEmpresa(criarEmpresaAtiva());
        entity.getEmpresa().setId(2);
        entity.setLocalizacao("A1");
        entity.setLote("LOTE-01");
        entity.setQuantidade(new BigDecimal("10.00"));
        entity.setValorUnitario(new BigDecimal("15.50"));

        EstoquesRequestDTO request = new EstoquesRequestDTO(
                1,
                2,
                "B2",
                "LOTE-01",
                new BigDecimal("10.00"),
                new BigDecimal("2.00"),
                new BigDecimal("20.00"),
                new BigDecimal("15.50"),
                null
        );

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(movimentacoesRepository.existsByEstoqueId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10, request));

        assertEquals("Nao e permitido alterar identificacao do estoque apos existir movimentacao", exception.getMessage());
        verify(repository, never()).save(any(Estoques.class));
    }

    private Produtos criarProdutoValido(String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(1);
        produto.setSituacao("ativo");
        produto.setTipoItem(tipoItem);
        return produto;
    }

    private Empresas criarEmpresaAtiva() {
        Empresas empresa = new Empresas();
        empresa.setId(2);
        empresa.setSituacao("ativo");
        return empresa;
    }
}
