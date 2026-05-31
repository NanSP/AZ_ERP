package com.example.backend.qm.inspecoes;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InspecoesServiceTest {

    @Mock
    private InspecoesRepository repository;
    @Mock
    private ProdutosRepository produtosRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private InspecoesService service;

    @Test
    void deveCriarInspecaoComQuantidadesValidas() {
        InspecoesRequestDTO request = new InspecoesRequestDTO(
                " RECEBIMENTO ",
                1,
                " LOTE-01 ",
                new BigDecimal("10"),
                new BigDecimal("8"),
                new BigDecimal("2"),
                LocalDate.of(2026, 6, 1),
                2,
                " APROVADO ",
                " inspecao inicial "
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProdutoValido("produto")));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarInspetor()));
        when(repository.save(any(Inspecoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inspecoes saved = service.criar(request);

        assertEquals("recebimento", saved.getTipoInspecao());
        assertEquals("aprovado", saved.getResultado());
        assertEquals("LOTE-01", saved.getLote());

        ArgumentCaptor<Inspecoes> captor = ArgumentCaptor.forClass(Inspecoes.class);
        verify(repository).save(captor.capture());
        assertEquals("inspecao inicial", captor.getValue().getObservacoes());
    }

    @Test
    void deveBloquearInspecaoQuandoSomaNaoBateComQuantidade() {
        InspecoesRequestDTO request = new InspecoesRequestDTO(
                "recebimento",
                1,
                null,
                new BigDecimal("10"),
                new BigDecimal("7"),
                new BigDecimal("2"),
                LocalDate.of(2026, 6, 1),
                2,
                "aprovado",
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("A soma de aprovadas e reprovadas deve ser igual a quantidade inspecionada", exception.getMessage());
    }

    @Test
    void deveBloquearInspecaoDeProdutoServico() {
        InspecoesRequestDTO request = new InspecoesRequestDTO(
                "recebimento",
                1,
                null,
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("5"),
                LocalDate.of(2026, 6, 1),
                2,
                "aprovado",
                null
        );

        when(produtosRepository.findById(1)).thenReturn(Optional.of(criarProdutoValido("servico")));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Produto do tipo servico nao pode ser usado em inspecao", exception.getMessage());
    }

    private Produtos criarProdutoValido(String tipoItem) {
        Produtos produto = new Produtos();
        produto.setId(1);
        produto.setSituacao("ativo");
        produto.setTipoItem(tipoItem);
        return produto;
    }

    private Colaboradores criarInspetor() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(2);
        colaborador.setNome("Inspetor");
        return colaborador;
    }
}
