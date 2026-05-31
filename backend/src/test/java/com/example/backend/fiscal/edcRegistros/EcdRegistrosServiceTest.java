package com.example.backend.fiscal.edcRegistros;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcdRegistrosServiceTest {

    @Mock
    private EcdRegistrosRepository repository;

    @InjectMocks
    private EcdRegistrosService service;

    @Test
    void deveCriarRegistroEcdComCodigoNormalizado() {
        EcdRegistrosRequestDTO request = new EcdRegistrosRequestDTO(
                LocalDate.of(2026, 6, 1),
                " i050 ",
                Map.of("campo", "valor")
        );

        when(repository.save(any(EcdRegistros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EcdRegistros saved = service.criar(request);

        assertEquals("I050", saved.getRegistro());

        ArgumentCaptor<EcdRegistros> captor = ArgumentCaptor.forClass(EcdRegistros.class);
        verify(repository).save(captor.capture());
        assertEquals("valor", captor.getValue().getConteudo().get("campo"));
    }

    @Test
    void deveBloquearRegistroEcdComConteudoVazio() {
        EcdRegistrosRequestDTO request = new EcdRegistrosRequestDTO(
                LocalDate.of(2026, 6, 1),
                "I050",
                Map.of()
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Conteudo do registro e obrigatorio", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeRegistroEcdDePeriodoPassado() {
        EcdRegistros entity = new EcdRegistros();
        entity.setId(10L);
        entity.setPeriodo(LocalDate.now().withDayOfMonth(1).minusMonths(1));

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10L));

        assertEquals("Nao e permitido excluir registro ECD de periodo passado", exception.getMessage());
        verify(repository, never()).delete(any(EcdRegistros.class));
    }
}
