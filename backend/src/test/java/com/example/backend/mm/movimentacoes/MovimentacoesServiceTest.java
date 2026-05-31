package com.example.backend.mm.movimentacoes;

import com.example.backend.mm.estoques.Estoques;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.shared.exception.RegraNegocioException;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacoesServiceTest {

    @Mock
    private MovimentacoesRepository repository;
    @Mock
    private EstoquesRepository estoquesRepository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private MovimentacoesService service;

    @Test
    void deveCriarMovimentacaoDeEntradaAtualizandoSaldo() {
        Estoques estoque = criarEstoque(new BigDecimal("10"));
        MovimentacoesRequestDTO request = new MovimentacoesRequestDTO(
                1,
                " entrada ",
                new BigDecimal("5"),
                new BigDecimal("2.00"),
                "DOC-1",
                "Reposicao",
                2
        );

        when(estoquesRepository.findById(1)).thenReturn(Optional.of(estoque));
        when(usuariosRepository.findById(2)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Movimentacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movimentacoes saved = service.criar(request);

        assertEquals(new BigDecimal("15"), estoque.getQuantidade());
        assertEquals(new BigDecimal("10.00"), saved.getValorTotal());

        ArgumentCaptor<Movimentacoes> captor = ArgumentCaptor.forClass(Movimentacoes.class);
        verify(repository).save(captor.capture());
        assertEquals("entrada", captor.getValue().getTipoMovimento());
    }

    @Test
    void deveBloquearMovimentacaoComTipoInvalido() {
        MovimentacoesRequestDTO request = new MovimentacoesRequestDTO(
                1,
                "xpto",
                new BigDecimal("5"),
                new BigDecimal("2.00"),
                null,
                null,
                2
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tipo de movimento invalido", exception.getMessage());
    }

    @Test
    void deveBloquearSaidaComSaldoInsuficiente() {
        Estoques estoque = criarEstoque(new BigDecimal("3"));
        MovimentacoesRequestDTO request = new MovimentacoesRequestDTO(
                1,
                "saida",
                new BigDecimal("5"),
                new BigDecimal("2.00"),
                null,
                null,
                2
        );

        when(estoquesRepository.findById(1)).thenReturn(Optional.of(estoque));
        when(usuariosRepository.findById(2)).thenReturn(Optional.of(criarUsuario()));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.criar(request));

        assertEquals("Saldo insuficiente em estoque", exception.getMessage());
    }

    private Estoques criarEstoque(BigDecimal quantidade) {
        Estoques estoque = new Estoques();
        estoque.setId(1);
        estoque.setQuantidade(quantidade);
        return estoque;
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(2);
        usuario.setNome("Usuario");
        return usuario;
    }
}
