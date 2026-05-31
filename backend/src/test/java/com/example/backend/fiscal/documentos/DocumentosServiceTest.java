package com.example.backend.fiscal.documentos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentosServiceTest {

    @Mock
    private DocumentosRepository repository;
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private ParceirosRepository parceirosRepository;

    @InjectMocks
    private DocumentosService service;

    @Test
    void deveCriarDocumentoAPartirDoPedido() {
        Pedidos pedido = criarPedidoAtivo();
        DocumentosRequestDTO request = new DocumentosRequestDTO(
                " NFE ",
                " 123 ",
                " 1 ",
                "12345678901234567890123456789012345678901234",
                LocalDate.of(2026, 6, 1),
                1,
                null,
                null,
                "emitido",
                "<xml>ok</xml>"
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(repository.save(any(Documentos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Documentos saved = service.criar(request);

        assertEquals("nfe", saved.getTipoDocumento());
        assertEquals("123", saved.getNumero());
        assertEquals(new BigDecimal("1500.00"), saved.getValorTotal());
        assertEquals(10, saved.getCliente().getId());

        ArgumentCaptor<Documentos> captor = ArgumentCaptor.forClass(Documentos.class);
        verify(repository).save(captor.capture());
        assertEquals("1", captor.getValue().getSerie());
    }

    @Test
    void deveBloquearDocumentoComValorDivergenteDoPedido() {
        DocumentosRequestDTO request = new DocumentosRequestDTO(
                "nfe",
                "123",
                null,
                "12345678901234567890123456789012345678901234",
                LocalDate.of(2026, 6, 1),
                1,
                null,
                new BigDecimal("1200.00"),
                "digitado",
                null
        );

        when(pedidosRepository.findById(1)).thenReturn(Optional.of(criarPedidoAtivo()));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Valor total do documento diverge do valor total do pedido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeDocumentoEmStatusFinal() {
        Documentos entity = new Documentos();
        entity.setId(10);
        entity.setStatus("emitido");

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir documento em status final", exception.getMessage());
        verify(repository, never()).delete(any(Documentos.class));
    }

    private Pedidos criarPedidoAtivo() {
        Parceiros cliente = new Parceiros();
        cliente.setId(10);

        Pedidos pedido = new Pedidos();
        pedido.setId(1);
        pedido.setStatus("faturado");
        pedido.setValorTotal(new BigDecimal("1500.00"));
        pedido.setCliente(cliente);
        return pedido;
    }
}
