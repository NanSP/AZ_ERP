package com.example.backend.fiscal.efdRegistros;

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
class EfdRegistrosServiceTest {

    @Mock
    private EfdRegistrosRepository repository;

    @InjectMocks
    private EfdRegistrosService service;

    @Test
    void deveCriarRegistroEfdComCodigoNormalizado() {
        EfdRegistrosRequestDTO request = new EfdRegistrosRequestDTO(
                LocalDate.of(2026, 6, 1),
                " c100 ",
                Map.of("campo", "valor")
        );

        when(repository.save(any(EfdRegistros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EfdRegistros saved = service.criar(request);

        assertEquals("C100", saved.getRegistro());

        ArgumentCaptor<EfdRegistros> captor = ArgumentCaptor.forClass(EfdRegistros.class);
        verify(repository).save(captor.capture());
        assertEquals("valor", captor.getValue().getConteudo().get("campo"));
    }

    @Test
    void deveBloquearRegistroEfdComCodigoInvalido() {
        EfdRegistrosRequestDTO request = new EfdRegistrosRequestDTO(
                LocalDate.of(2026, 6, 1),
                "C10",
                Map.of("campo", "valor")
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Registro deve conter 4 caracteres alfanumericos em maiusculo", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeRegistroEfdDePeriodoPassado() {
        EfdRegistros entity = new EfdRegistros();
        entity.setId(10L);
        entity.setPeriodo(LocalDate.now().withDayOfMonth(1).minusMonths(1));

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10L));

        assertEquals("Nao e permitido excluir registro EFD de periodo passado", exception.getMessage());
        verify(repository, never()).delete(any(EfdRegistros.class));
    }
}