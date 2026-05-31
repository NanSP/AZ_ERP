package com.example.backend.pp.apontamentos;

import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.pp.ordemProducao.OrdemProducaoRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApontamentosServiceTest {

    @Mock
    private ApontamentosRepository repository;
    @Mock
    private OrdemProducaoRepository ordemProducaoRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private ApontamentosService service;

    @Test
    void deveCriarApontamentoEAtualizarStatusDaOp() {
        OrdemProducao op = criarOp(1, "planejada", new BigDecimal("10"), BigDecimal.ZERO);
        ApontamentosRequestDTO request = new ApontamentosRequestDTO(
                1,
                5,
                2,
                LocalDateTime.of(2026, 6, 2, 8, 0),
                LocalDateTime.of(2026, 6, 2, 10, 0),
                new BigDecimal("4"),
                new BigDecimal("1"),
                new BigDecimal("0.5"),
                " producao inicial "
        );

        when(ordemProducaoRepository.findById(1)).thenReturn(Optional.of(op));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarOperador(2)));
        when(repository.sumQuantidadeRefugoByOpId(1)).thenReturn(BigDecimal.ZERO, new BigDecimal("1"));
        when(repository.save(any(Apontamentos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Apontamentos saved = service.criar(request);

        assertEquals(new BigDecimal("4"), saved.getQuantidadeProduzida());
        assertEquals("em_producao", op.getStatus());
        assertEquals(new BigDecimal("4"), op.getQuantidadeProduzida());
        assertEquals("producao inicial", saved.getObservacoes());
    }

    @Test
    void deveBloquearApontamentoParaOpCancelada() {
        OrdemProducao op = criarOp(1, "cancelada", new BigDecimal("10"), BigDecimal.ZERO);
        ApontamentosRequestDTO request = new ApontamentosRequestDTO(
                1,
                null,
                2,
                LocalDateTime.of(2026, 6, 2, 8, 0),
                LocalDateTime.of(2026, 6, 2, 9, 0),
                new BigDecimal("1"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null
        );

        when(ordemProducaoRepository.findById(1)).thenReturn(Optional.of(op));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarOperador(2)));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido registrar apontamento para ordem cancelada", exception.getMessage());
    }

    @Test
    void deveBloquearApontamentoQueExcedeQuantidadePlanejada() {
        OrdemProducao op = criarOp(1, "planejada", new BigDecimal("10"), new BigDecimal("2"));
        ApontamentosRequestDTO request = new ApontamentosRequestDTO(
                1,
                null,
                2,
                LocalDateTime.of(2026, 6, 2, 8, 0),
                LocalDateTime.of(2026, 6, 2, 9, 0),
                new BigDecimal("6"),
                new BigDecimal("3"),
                BigDecimal.ZERO,
                null
        );

        when(ordemProducaoRepository.findById(1)).thenReturn(Optional.of(op));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarOperador(2)));
        when(repository.sumQuantidadeRefugoByOpId(1)).thenReturn(new BigDecimal("1"));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Apontamento excede a quantidade planejada da ordem considerando producao e refugo", exception.getMessage());
    }

    private OrdemProducao criarOp(Integer id, String status, BigDecimal planejada, BigDecimal produzida) {
        OrdemProducao op = new OrdemProducao();
        op.setId(id);
        op.setStatus(status);
        op.setQuantidadePlanejada(planejada);
        op.setQuantidadeProduzida(produzida);
        op.setDataEmissao(LocalDate.of(2026, 6, 1));
        op.setDataInicio(LocalDate.of(2026, 6, 1));
        return op;
    }

    private Colaboradores criarOperador(Integer id) {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(id);
        return colaborador;
    }
}
