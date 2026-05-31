package com.example.backend.bi.historicoMetricas;

import com.example.backend.bi.metricas.Metricas;
import com.example.backend.bi.metricas.MetricasRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoMetricasServiceTest {

    @Mock
    private HistoricoMetricasRepository repository;

    @Mock
    private MetricasRepository metricasRepository;

    @InjectMocks
    private HistoricoMetricasService service;

    @Test
    void deveCriarHistoricoQuandoMetricaEstiverEstruturalmenteValida() {
        HistoricoMetricasRequestDTO request = new HistoricoMetricasRequestDTO(
                1,
                LocalDate.now().minusDays(1),
                new BigDecimal("1500.00")
        );
        Metricas metrica = criarMetrica("financeira", "faturamento - devolucoes", new BigDecimal("1000.00"));

        when(metricasRepository.findById(1)).thenReturn(Optional.of(metrica));
        when(repository.save(any(HistoricoMetricas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistoricoMetricas saved = service.criar(request);

        assertEquals(1, saved.getMetrica().getId());
        assertEquals(new BigDecimal("1500.00"), saved.getValorApurado());
        assertNotNull(saved.getCreatedAt());

        ArgumentCaptor<HistoricoMetricas> captor = ArgumentCaptor.forClass(HistoricoMetricas.class);
        verify(repository).save(captor.capture());
        assertEquals(request.periodo(), captor.getValue().getPeriodo());
    }

    @Test
    void deveBloquearCriacaoDeHistoricoParaMetricaEstrategicaSemMeta() {
        HistoricoMetricasRequestDTO request = new HistoricoMetricasRequestDTO(
                1,
                LocalDate.now().minusDays(1),
                new BigDecimal("1500.00")
        );
        Metricas metrica = criarMetrica("estrategica", null, null);

        when(metricasRepository.findById(1)).thenReturn(Optional.of(metrica));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido registrar historico para metrica estrategica sem meta definida", exception.getMessage());
    }

    @Test
    void deveBloquearAlteracaoDoValorApuradoNoHistorico() {
        Metricas metrica = criarMetrica("financeira", "faturamento - devolucoes", new BigDecimal("1000.00"));
        HistoricoMetricas entity = new HistoricoMetricas();
        entity.setId(10L);
        entity.setMetrica(metrica);
        entity.setPeriodo(LocalDate.of(2026, 5, 1));
        entity.setValorApurado(new BigDecimal("1500.00"));

        HistoricoMetricasRequestDTO request = new HistoricoMetricasRequestDTO(
                1,
                LocalDate.of(2026, 5, 1),
                new BigDecimal("1800.00")
        );

        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(10L, request));

        assertEquals("Nao e permitido alterar o valor apurado do historico", exception.getMessage());
    }

    private Metricas criarMetrica(String categoria, String formula, BigDecimal meta) {
        Metricas metrica = new Metricas();
        metrica.setId(1);
        metrica.setNome("Receita Mensal");
        metrica.setCategoria(categoria);
        metrica.setFormula(formula);
        metrica.setMeta(meta);
        return metrica;
    }
}
