package com.example.backend.bi.metricas;

import com.example.backend.bi.historicoMetricas.HistoricoMetricasRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricasServiceTest {

    @Mock
    private MetricasRepository repository;

    @Mock
    private HistoricoMetricasRepository historicoMetricasRepository;

    @InjectMocks
    private MetricasService service;

    @Test
    void deveCriarMetricaFinanceiraComFormulaObrigatoria() {
        MetricasRequestDTO request = new MetricasRequestDTO(
                " Receita Mensal ",
                " Indicador financeiro ",
                "financeira",
                "faturamento - devolucoes",
                "BRL",
                new BigDecimal("1000.00")
        );

        when(repository.save(any(Metricas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Metricas saved = service.criar(request);

        assertEquals("Receita Mensal", saved.getNome());
        assertEquals("financeira", saved.getCategoria());
        assertEquals("faturamento - devolucoes", saved.getFormula());
        assertNotNull(saved.getCreatedAt());

        ArgumentCaptor<Metricas> captor = ArgumentCaptor.forClass(Metricas.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("1000.00"), captor.getValue().getMeta());
    }

    @Test
    void deveBloquearCriacaoSemFormulaEmCategoriaFinanceira() {
        MetricasRequestDTO request = new MetricasRequestDTO(
                " Receita Mensal ",
                null,
                "financeira",
                null,
                "BRL",
                new BigDecimal("1000.00")
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Formula da metrica e obrigatoria para a categoria informada", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeMetricaComHistorico() {
        Metricas entity = new Metricas();
        entity.setId(1);
        entity.setNome("Receita Mensal");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(historicoMetricasRepository.existsByMetricaId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir metrica com historico registrado", exception.getMessage());
        verify(repository, never()).delete(any(Metricas.class));
    }
}
