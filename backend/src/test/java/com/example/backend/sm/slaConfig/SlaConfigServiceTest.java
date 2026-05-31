package com.example.backend.sm.slaConfig;

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
class SlaConfigServiceTest {

    @Mock
    private SlaConfigRepository repository;

    @InjectMocks
    private SlaConfigService service;

    @Test
    void deveCriarSlaComPrioridadePadraoENormalizacao() {
        SlaConfigRequestDTO request = new SlaConfigRequestDTO(
                " Suporte ",
                null,
                4,
                24
        );

        when(repository.save(any(SlaConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SlaConfig saved = service.criar(request);

        assertEquals("suporte", saved.getTipoServico());
        assertEquals("normal", saved.getPrioridade());

        ArgumentCaptor<SlaConfig> captor = ArgumentCaptor.forClass(SlaConfig.class);
        verify(repository).save(captor.capture());
        assertEquals(24, captor.getValue().getTempoResolucaoHoras());
    }

    @Test
    void deveBloquearSlaComTempoNegativo() {
        SlaConfigRequestDTO request = new SlaConfigRequestDTO(
                "suporte",
                "alta",
                -1,
                24
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tempo de atendimento nao pode ser negativo", exception.getMessage());
    }

    @Test
    void deveAtualizarSlaExistente() {
        SlaConfig entity = new SlaConfig();
        entity.setId(10);
        entity.setTipoServico("suporte");
        entity.setPrioridade("normal");

        SlaConfigRequestDTO request = new SlaConfigRequestDTO(
                "suporte",
                "critica",
                1,
                8
        );

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(repository.save(any(SlaConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SlaConfig updated = service.atualizar(10, request);

        assertEquals("critica", updated.getPrioridade());
        assertEquals(1, updated.getTempoAtendimentoHoras());
        assertEquals(8, updated.getTempoResolucaoHoras());
    }
}
