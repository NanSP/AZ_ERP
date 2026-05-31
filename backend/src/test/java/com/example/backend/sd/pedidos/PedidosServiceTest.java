package com.example.backend.sd.pedidos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.sd.faturas.FaturasRepository;
import com.example.backend.sd.pedidoItens.PedidoItensRepository;
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
class PedidosServiceTest {

    @Mock
    private PedidosRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ClientesRepository clientesRepository;
    @Mock
    private PedidoItensRepository pedidoItensRepository;
    @Mock
    private FaturasRepository faturasRepository;

    @InjectMocks
    private PedidosService service;

    @Test
    void deveCriarPedidoComStatusPadraoETotaisZerados() {
        PedidosRequestDTO request = new PedidosRequestDTO(
                1,
                " PED-001 ",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                new BigDecimal("999.99"),
                new BigDecimal("50.00"),
                " 30 dias ",
                null,
                " observacao "
        );

        when(clientesRepository.existsByParceiroId(1)).thenReturn(true);
        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarClienteAtivo()));
        when(repository.save(any(Pedidos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedidos saved = service.criar(request);

        assertEquals("PED-001", saved.getNumeroPedido());
        assertEquals("aberto", saved.getStatus());
        assertEquals(BigDecimal.ZERO, saved.getValorTotal());
        assertEquals(BigDecimal.ZERO, saved.getDescontoTotal());

        ArgumentCaptor<Pedidos> captor = ArgumentCaptor.forClass(Pedidos.class);
        verify(repository).save(captor.capture());
        assertEquals("30 dias", captor.getValue().getCondicoesPagamento());
    }

    @Test
    void deveBloquearPedidoFaturadoSemFaturas() {
        Pedidos entity = new Pedidos();
        entity.setId(10);
        entity.setStatus("aberto");

        PedidosRequestDTO request = new PedidosRequestDTO(
                1,
                "PED-001",
                LocalDate.of(2026, 6, 1),
                null,
                null,
                null,
                null,
                "faturado",
                null
        );

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(clientesRepository.existsByParceiroId(1)).thenReturn(true);
        when(faturasRepository.existsByPedidoId(10)).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10, request));

        assertEquals("Pedido so pode ser faturado quando possuir faturas", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDePedidoComItens() {
        Pedidos entity = new Pedidos();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(pedidoItensRepository.existsByPedidoId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir pedido que possui itens", exception.getMessage());
        verify(repository, never()).delete(any(Pedidos.class));
    }

    private Parceiros criarClienteAtivo() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(1);
        parceiro.setSituacao("ativo");
        parceiro.setTipoParceiro("cliente");
        return parceiro;
    }
}
