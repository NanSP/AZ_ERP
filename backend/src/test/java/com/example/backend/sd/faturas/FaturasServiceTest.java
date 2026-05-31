package com.example.backend.sd.faturas;

import com.example.backend.sd.pedidoItens.PedidoItensRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
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
class FaturasServiceTest {

    @Mock
    private FaturasRepository repository;
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private PedidoItensRepository pedidoItensRepository;

    @InjectMocks
    private FaturasService service;

    @Test
    void deveCriarFaturaEAtualizarPedidoParaFaturado() {
        Pedidos pedido = criarPedido("em_andamento");
        FaturasRequestDTO request = new FaturasRequestDTO(
                1,
                " FAT-001 ",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1500.00"),
                LocalDate.of(2026, 6, 10),
                null
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(repository.save(any(Faturas.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.existsByPedidoIdAndStatusNot(1, "cancelada")).thenReturn(true);

        Faturas saved = service.criar(request);

        assertEquals("emitida", saved.getStatus());
        assertEquals("faturado", pedido.getStatus());

        ArgumentCaptor<Faturas> captor = ArgumentCaptor.forClass(Faturas.class);
        verify(repository).save(captor.capture());
        assertEquals("FAT-001", captor.getValue().getNumeroFatura());
    }

    @Test
    void deveBloquearFaturaParaPedidoCancelado() {
        FaturasRequestDTO request = new FaturasRequestDTO(
                1,
                "FAT-001",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1500.00"),
                LocalDate.of(2026, 6, 10),
                "emitida"
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(criarPedido("cancelado")));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido faturar pedido cancelado", exception.getMessage());
    }

    @Test
    void deveRecalcularStatusDoPedidoAoExcluirFatura() {
        Pedidos pedido = criarPedido("faturado");
        Faturas entity = new Faturas();
        entity.setId(10);
        entity.setPedido(pedido);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(repository.existsByPedidoIdAndStatusNot(1, "cancelada")).thenReturn(false);
        when(pedidoItensRepository.existsByPedidoId(1)).thenReturn(true);

        service.excluir(10);

        verify(repository).delete(entity);
        assertEquals("em_andamento", pedido.getStatus());
    }

    private Pedidos criarPedido(String status) {
        Pedidos pedido = new Pedidos();
        pedido.setId(1);
        pedido.setStatus(status);
        return pedido;
    }
}
