package com.example.backend.rh.controleDePonto;

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
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControleDePontoServiceTest {

    @Mock
    private ControleDePontoRepository repository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private ControleDePontoService service;

    @Test
    void deveCriarControleDePontoCalculandoHorasExtrasEAtrasos() {
        ControleDePontoRequestDTO request = new ControleDePontoRequestDTO(
                1,
                LocalDate.of(2026, 6, 2),
                LocalTime.of(8, 30),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(18, 30)
        );

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(criarColaboradorAtivo()));
        when(repository.save(any(ControleDePonto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ControleDePonto saved = service.criar(request);

        assertEquals(new BigDecimal("9.00"), saved.getHorasTrabalhadas());
        assertEquals(new BigDecimal("1.00"), saved.getHorasExtras());
        assertEquals(30, saved.getAtrasos());

        ArgumentCaptor<ControleDePonto> captor = ArgumentCaptor.forClass(ControleDePonto.class);
        verify(repository).save(captor.capture());
        assertEquals(LocalTime.of(18, 30), captor.getValue().getHoraSaida());
    }

    @Test
    void deveBloquearSequenciaInvalidaDeHorarios() {
        ControleDePontoRequestDTO request = new ControleDePontoRequestDTO(
                1,
                LocalDate.of(2026, 6, 2),
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                LocalTime.of(11, 0),
                LocalTime.of(18, 0)
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Hora de retorno do almoco nao pode ser anterior a saida para almoco", exception.getMessage());
    }

    @Test
    void deveBloquearRegistroDePontoParaColaboradorInativo() {
        ControleDePontoRequestDTO request = new ControleDePontoRequestDTO(
                1,
                LocalDate.of(2026, 6, 2),
                LocalTime.of(8, 0),
                null,
                null,
                LocalTime.of(17, 0)
        );

        Colaboradores colaborador = criarColaboradorAtivo();
        colaborador.setSituacao("desligado");

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(colaborador));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido registrar ponto para colaborador inativo", exception.getMessage());
    }

    private Colaboradores criarColaboradorAtivo() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(1);
        colaborador.setSituacao("ativo");
        colaborador.setJornadaSemanal(40);
        return colaborador;
    }
}
