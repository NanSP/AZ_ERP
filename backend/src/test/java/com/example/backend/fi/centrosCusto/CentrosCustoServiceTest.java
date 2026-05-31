package com.example.backend.fi.centrosCusto;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CentrosCustoServiceTest {

    @Mock
    private CentrosCustoRepository repository;

    @InjectMocks
    private CentrosCustoService service;

    @Test
    void deveCriarCentroDeCustoComAtivoPadrao() {
        CentrosCustoRequestDTO request = new CentrosCustoRequestDTO(
                " CC-01 ",
                " Administrativo ",
                " indireto ",
                " Maria ",
                null
        );

        when(repository.save(any(CentrosCusto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CentrosCusto saved = service.criar(request);

        assertEquals("CC-01", saved.getCodigo());
        assertEquals("Administrativo", saved.getNome());
        assertEquals("indireto", saved.getTipo());
        assertEquals("Maria", saved.getResponsavel());
        assertEquals(true, saved.getAtivo());

        ArgumentCaptor<CentrosCusto> captor = ArgumentCaptor.forClass(CentrosCusto.class);
        verify(repository).save(captor.capture());
        assertEquals("CC-01", captor.getValue().getCodigo());
    }

    @Test
    void deveBloquearCriacaoSemCodigo() {
        CentrosCustoRequestDTO request = new CentrosCustoRequestDTO(
                " ",
                "Administrativo",
                null,
                null,
                true
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Codigo e obrigatorio", exception.getMessage());
    }

    @Test
    void deveAtualizarCentroDeCustoExistente() {
        CentrosCusto entity = new CentrosCusto();
        entity.setId(1);

        CentrosCustoRequestDTO request = new CentrosCustoRequestDTO(
                "CC-01",
                "Administrativo",
                "indireto",
                "Joao",
                false
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(CentrosCusto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CentrosCusto updated = service.atualizar(1, request);

        assertEquals(false, updated.getAtivo());
        assertEquals("Joao", updated.getResponsavel());
    }
}
