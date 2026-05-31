package com.example.backend.am.manutencoes;

import com.example.backend.am.bensPatrimoniais.BensPatrimoniais;
import com.example.backend.am.bensPatrimoniais.BensPatrimoniaisRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManutencoesServiceTest {

    @Mock
    private ManutencoesRepository repository;
    @Mock
    private BensPatrimoniaisRepository bensPatrimoniaisRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private ManutencoesService service;

    @Test
    void deveCriarManutencaoComCustoTotalCalculado() {
        ManutencoesRequestDTO request = new ManutencoesRequestDTO(
                1,
                " preventiva ",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2),
                " troca de pecas ",
                new BigDecimal("150.00"),
                new BigDecimal("50.00"),
                2
        );

        when(bensPatrimoniaisRepository.findById(1)).thenReturn(Optional.of(criarAtivo(1)));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarTecnico(2)));
        when(repository.save(any(Manutencoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Manutencoes saved = service.criar(request);

        assertEquals("preventiva", saved.getTipoManutencao());
        assertEquals(new BigDecimal("200.00"), saved.getCustoTotal());
        assertEquals("troca de pecas", saved.getDescricao());
    }

    @Test
    void deveBloquearManutencaoExecutadaSemTecnico() {
        ManutencoesRequestDTO request = new ManutencoesRequestDTO(
                1,
                "corretiva",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2),
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tecnico e obrigatorio quando a manutencao estiver executada", exception.getMessage());
    }

    @Test
    void deveBloquearDataExecucaoAnteriorSolicitacao() {
        ManutencoesRequestDTO request = new ManutencoesRequestDTO(
                1,
                "preditiva",
                LocalDate.of(2026, 6, 2),
                LocalDate.of(2026, 6, 1),
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                2
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de execucao nao pode ser anterior a data de solicitacao", exception.getMessage());
    }

    private BensPatrimoniais criarAtivo(Integer id) {
        BensPatrimoniais ativo = new BensPatrimoniais();
        ativo.setId(id);
        return ativo;
    }

    private Colaboradores criarTecnico(Integer id) {
        Colaboradores tecnico = new Colaboradores();
        tecnico.setId(id);
        return tecnico;
    }
}
