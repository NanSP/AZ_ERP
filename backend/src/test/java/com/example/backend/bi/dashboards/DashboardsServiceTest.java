package com.example.backend.bi.dashboards;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardsServiceTest {

    @Mock
    private DashboardsRepository repository;

    @InjectMocks
    private DashboardsService service;

    @Test
    void deveCriarDashboardComMapasNormalizados() {
        DashboardsRequestDTO request = new DashboardsRequestDTO(
                " Painel Executivo ",
                " Dashboard principal ",
                Map.of("colunas", 3),
                Map.of()
        );

        when(repository.save(any(Dashboards.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dashboards saved = service.criar(request);

        assertEquals("Painel Executivo", saved.getNome());
        assertEquals("Dashboard principal", saved.getDescricao());
        assertEquals(1, saved.getLayout().size());
        assertNull(saved.getConfiguracoes());

        ArgumentCaptor<Dashboards> captor = ArgumentCaptor.forClass(Dashboards.class);
        verify(repository).save(captor.capture());
        assertEquals(3, captor.getValue().getLayout().get("colunas"));
    }

    @Test
    void deveBloquearDashboardComLayoutAcimaDoLimite() {
        java.util.Map<String, Object> layout = new java.util.HashMap<>();
        for (int i = 0; i < 101; i++) {
            layout.put("item" + i, i);
        }

        DashboardsRequestDTO request = new DashboardsRequestDTO(
                "Painel Executivo",
                null,
                layout,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Campo layout excede o limite permitido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeDashboard() {
        Dashboards entity = new Dashboards();
        entity.setId(1);
        entity.setNome("Painel Executivo");

        when(repository.findById(1)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Dashboard nao pode ser excluido", exception.getMessage());
        verify(repository, never()).delete(any(Dashboards.class));
    }
}
