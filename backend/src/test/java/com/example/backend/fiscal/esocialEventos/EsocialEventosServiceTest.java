package com.example.backend.fiscal.esocialEventos;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EsocialEventosServiceTest {

    @Mock
    private EsocialEventosRepository repository;

    @InjectMocks
    private EsocialEventosService service;

    @Test
    void deveCriarEventoEsocialComStatusPadrao() {
        EsocialEventosRequestDTO request = new EsocialEventosRequestDTO(
                LocalDate.of(2026, 6, 1),
                " S1200 ",
                null,
                "<evento>ok</evento>",
                null
        );

        when(repository.save(any(EsocialEventos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EsocialEventos saved = service.criar(request);

        assertEquals("s1200", saved.getTipoEvento());
        assertEquals("gerado", saved.getStatus());

        ArgumentCaptor<EsocialEventos> captor = ArgumentCaptor.forClass(EsocialEventos.class);
        verify(repository).save(captor.capture());
        assertEquals("<evento>ok</evento>", captor.getValue().getConteudo());
    }

    @Test
    void deveBloquearEventoEnviadoSemEventoId() {
        EsocialEventosRequestDTO request = new EsocialEventosRequestDTO(
                LocalDate.of(2026, 6, 1),
                "s1200",
                null,
                "<evento>ok</evento>",
                "enviado"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Evento ID e obrigatorio para evento enviado ou processado", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeEventoEsocialProcessado() {
        EsocialEventos entity = new EsocialEventos();
        entity.setId(10L);
        entity.setStatus("processado");

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10L));

        assertEquals("Nao e permitido excluir evento eSocial em status avancado", exception.getMessage());
        verify(repository, never()).delete(any(EsocialEventos.class));
    }
}
