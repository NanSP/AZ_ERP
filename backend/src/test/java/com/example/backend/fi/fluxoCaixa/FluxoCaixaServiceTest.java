package com.example.backend.fi.fluxoCaixa;

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
class FluxoCaixaServiceTest {

    @Mock
    private FluxoCaixaRepository repository;

    @InjectMocks
    private FluxoCaixaService service;

    @Test
    void deveCriarFluxoDeCaixaCalculandoSaldos() {
        FluxoCaixaRequestDTO request = new FluxoCaixaRequestDTO(
                LocalDate.of(2026, 5, 31),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("200.00"),
                new BigDecimal("300.00"),
                new BigDecimal("100.00")
        );

        when(repository.save(any(FluxoCaixa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FluxoCaixa saved = service.criar(request);

        assertEquals(new BigDecimal("1300.00"), saved.getSaldoFinalPrevisto());
        assertEquals(new BigDecimal("1200.00"), saved.getSaldoFinalReal());

        ArgumentCaptor<FluxoCaixa> captor = ArgumentCaptor.forClass(FluxoCaixa.class);
        verify(repository).save(captor.capture());
        assertEquals(LocalDate.of(2026, 5, 31), captor.getValue().getDataReferencia());
    }

    @Test
    void deveBloquearFluxoDeCaixaSemDataReferencia() {
        FluxoCaixaRequestDTO request = new FluxoCaixaRequestDTO(
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de referencia e obrigatoria", exception.getMessage());
    }

    @Test
    void deveAtualizarFluxoDeCaixaExistente() {
        FluxoCaixa entity = new FluxoCaixa();
        entity.setId(1);

        FluxoCaixaRequestDTO request = new FluxoCaixaRequestDTO(
                LocalDate.of(2026, 6, 1),
                new BigDecimal("200.00"),
                new BigDecimal("100.00"),
                new BigDecimal("50.00"),
                new BigDecimal("80.00"),
                new BigDecimal("20.00")
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(FluxoCaixa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FluxoCaixa updated = service.atualizar(1, request);

        assertEquals(new BigDecimal("250.00"), updated.getSaldoFinalPrevisto());
        assertEquals(new BigDecimal("260.00"), updated.getSaldoFinalReal());
    }
}
