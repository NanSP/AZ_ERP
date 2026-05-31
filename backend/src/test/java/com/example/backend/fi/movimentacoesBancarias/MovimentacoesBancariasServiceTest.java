package com.example.backend.fi.movimentacoesBancarias;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacoesBancariasServiceTest {

    @Mock
    private MovimentacoesBancariasRepository repository;

    @InjectMocks
    private MovimentacoesBancariasService service;

    @Test
    void deveCriarMovimentacaoConciliada() {
        MovimentacoesBancariasRequestDTO request = new MovimentacoesBancariasRequestDTO(
                1,
                "credito",
                new BigDecimal("250.00"),
                LocalDate.of(2026, 5, 31),
                "Recebimento",
                "DOC-001",
                true,
                LocalDate.of(2026, 6, 1)
        );

        when(repository.save(any(MovimentacoesBancarias.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimentacoesBancarias saved = service.criar(request);

        assertEquals("credito", saved.getTipoMovimento());
        assertEquals(true, saved.getConciliado());
        assertEquals(LocalDate.of(2026, 6, 1), saved.getDataConciliacao());

        ArgumentCaptor<MovimentacoesBancarias> captor = ArgumentCaptor.forClass(MovimentacoesBancarias.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("250.00"), captor.getValue().getValor());
    }

    @Test
    void deveBloquearMovimentacaoConciliadaSemDataConciliacao() {
        MovimentacoesBancariasRequestDTO request = new MovimentacoesBancariasRequestDTO(
                1,
                "credito",
                new BigDecimal("250.00"),
                LocalDate.of(2026, 5, 31),
                "Recebimento",
                null,
                true,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de conciliacao e obrigatoria quando a movimentacao estiver conciliada", exception.getMessage());
    }

    @Test
    void deveLimparDataConciliacaoQuandoMovimentacaoNaoForConciliada() {
        MovimentacoesBancarias entity = new MovimentacoesBancarias();
        entity.setId(1);

        MovimentacoesBancariasRequestDTO request = new MovimentacoesBancariasRequestDTO(
                1,
                "debito",
                new BigDecimal("100.00"),
                LocalDate.of(2026, 5, 31),
                "Pagamento",
                null,
                false,
                null
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(MovimentacoesBancarias.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MovimentacoesBancarias updated = service.atualizar(1, request);

        assertEquals(false, updated.getConciliado());
        assertNull(updated.getDataConciliacao());
    }
}
