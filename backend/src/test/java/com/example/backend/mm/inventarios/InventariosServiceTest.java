package com.example.backend.mm.inventarios;

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
class InventariosServiceTest {

    @Mock
    private InventariosRepository repository;

    @InjectMocks
    private InventariosService service;

    @Test
    void deveCriarInventarioPlanejadoComCamposNormalizados() {
        InventariosRequestDTO request = new InventariosRequestDTO(
                LocalDate.of(2026, 6, 1),
                null,
                " ANUAL ",
                null,
                " contagem geral "
        );

        when(repository.save(any(Inventarios.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inventarios saved = service.criar(request);

        assertEquals("anual", saved.getTipoInventario());
        assertEquals("planejado", saved.getStatus());
        assertEquals("contagem geral", saved.getObservacoes());

        ArgumentCaptor<Inventarios> captor = ArgumentCaptor.forClass(Inventarios.class);
        verify(repository).save(captor.capture());
        assertEquals(LocalDate.of(2026, 6, 1), captor.getValue().getDataInicio());
    }

    @Test
    void deveBloquearInventarioEmAndamentoNoFuturo() {
        InventariosRequestDTO request = new InventariosRequestDTO(
                LocalDate.now().plusDays(2),
                null,
                "rotativo",
                "em_andamento",
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Inventario em andamento nao pode iniciar no futuro", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeInventarioEmAndamento() {
        Inventarios entity = new Inventarios();
        entity.setId(10);
        entity.setStatus("em_andamento");

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir inventario em andamento", exception.getMessage());
        verify(repository, never()).delete(any(Inventarios.class));
    }
}
