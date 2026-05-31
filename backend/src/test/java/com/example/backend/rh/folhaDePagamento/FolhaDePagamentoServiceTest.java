package com.example.backend.rh.folhaDePagamento;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.rh.controleDePonto.ControleDePontoRepository;
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
class FolhaDePagamentoServiceTest {

    @Mock
    private FolhaDePagamentoRepository repository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;
    @Mock
    private ControleDePontoRepository controleDePontoRepository;
    @Mock
    private FolhaDePagamentoCalculator calculator;

    @InjectMocks
    private FolhaDePagamentoService service;

    @Test
    void deveCriarFolhaUsandoHorasDoPontoQuandoNaoInformadas() {
        FolhaDePagamentoRequestDTO request = new FolhaDePagamentoRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("2200.00"),
                null,
                null,
                new BigDecimal("100.00"),
                new BigDecimal("50.00"),
                null,
                null
        );

        FolhaCalculadaDTO calculada = new FolhaCalculadaDTO(
                new BigDecimal("10.00"),
                new BigDecimal("1600.00"),
                new BigDecimal("150.00"),
                new BigDecimal("1850.00"),
                new BigDecimal("1800.00")
        );

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(criarColaboradorAtivo()));
        when(controleDePontoRepository.sumHorasTrabalhadasByColaboradorIdAndPeriodo(
                1,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        )).thenReturn(new BigDecimal("170.00"));
        when(controleDePontoRepository.sumHorasExtrasByColaboradorIdAndPeriodo(
                1,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        )).thenReturn(new BigDecimal("10.00"));
        when(calculator.calcular(
                new BigDecimal("2200.00"),
                new BigDecimal("160.00"),
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                new BigDecimal("50.00")
        )).thenReturn(calculada);
        when(repository.save(any(FolhaDePagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FolhaDePagamento saved = service.criar(request);

        assertEquals(new BigDecimal("160.00"), saved.getHorasNormais());
        assertEquals(new BigDecimal("10.00"), saved.getHorasExtras());
        assertEquals("calculado", saved.getStatus());
        assertEquals(new BigDecimal("1800.00"), saved.getValorLiquido());

        ArgumentCaptor<FolhaDePagamento> captor = ArgumentCaptor.forClass(FolhaDePagamento.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("1850.00"), captor.getValue().getValorBruto());
    }

    @Test
    void deveBloquearFolhaPagaSemDataPagamento() {
        FolhaDePagamentoRequestDTO request = new FolhaDePagamentoRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("2200.00"),
                new BigDecimal("160.00"),
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                "pago"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de pagamento e obrigatoria para folha com status pago", exception.getMessage());
    }

    @Test
    void deveBloquearFolhaAposMesDeDemissao() {
        Colaboradores colaborador = criarColaboradorAtivo();
        colaborador.setDataDemissao(LocalDate.of(2026, 5, 20));

        FolhaDePagamentoRequestDTO request = new FolhaDePagamentoRequestDTO(
                1,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("2200.00"),
                new BigDecimal("160.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                null
        );

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(colaborador));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido gerar folha apos o mes de demissao do colaborador", exception.getMessage());
    }

    private Colaboradores criarColaboradorAtivo() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(1);
        colaborador.setDataAdmissao(LocalDate.of(2024, 1, 10));
        colaborador.setSituacao("ativo");
        return colaborador;
    }
}
