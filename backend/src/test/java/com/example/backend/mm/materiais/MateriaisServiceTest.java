package com.example.backend.mm.materiais;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MateriaisServiceTest {

    @Mock
    private MateriaisRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private EstoquesRepository estoquesRepository;

    @InjectMocks
    private MateriaisService service;

    @Test
    void deveCriarMaterialComCamposNormalizados() {
        MateriaisRequestDTO request = new MateriaisRequestDTO(
                1,
                " MATERIA_PRIMA ",
                " Metais ",
                " Aco ",
                " Marca X ",
                " Modelo Y ",
                " Esp. ",
                " Ambiente seco ",
                " Classe 1 "
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProduto()));
        when(repository.save(any(Materiais.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Materiais saved = service.criar(request);

        assertEquals("materia_prima", saved.getTipoMaterial());
        assertEquals("Metais", saved.getCategoria());
        assertEquals("Aco", saved.getSubcategoria());

        ArgumentCaptor<Materiais> captor = ArgumentCaptor.forClass(Materiais.class);
        verify(repository).save(captor.capture());
        assertEquals("Marca X", captor.getValue().getMarca());
    }

    @Test
    void deveBloquearMaterialSemTipoValido() {
        MateriaisRequestDTO request = new MateriaisRequestDTO(
                1,
                "inexistente",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tipo de material invalido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeMaterialComEstoqueVinculadoAoProduto() {
        Produtos produto = criarProduto();
        produto.setId(1);

        Materiais entity = new Materiais();
        entity.setId(10);
        entity.setProduto(produto);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(estoquesRepository.existsByProdutoId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir material com estoque vinculado ao produto", exception.getMessage());
        verify(repository, never()).delete(any(Materiais.class));
    }

    private Produtos criarProduto() {
        Produtos produto = new Produtos();
        produto.setId(1);
        produto.setNome("Produto Base");
        return produto;
    }
}
