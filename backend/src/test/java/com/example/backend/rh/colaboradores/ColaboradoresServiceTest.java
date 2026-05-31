package com.example.backend.rh.colaboradores;

import com.example.backend.rh.beneficios.BeneficiosRepository;
import com.example.backend.rh.controleDePonto.ControleDePontoRepository;
import com.example.backend.rh.dependentes.DependentesRepository;
import com.example.backend.rh.folhaDePagamento.FolhaDePagamentoRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColaboradoresServiceTest {

    @Mock
    private ColaboradoresRepository repository;
    @Mock
    private DependentesRepository dependentesRepository;
    @Mock
    private BeneficiosRepository beneficiosRepository;
    @Mock
    private ControleDePontoRepository controleDePontoRepository;
    @Mock
    private FolhaDePagamentoRepository folhaDePagamentoRepository;

    @InjectMocks
    private ColaboradoresService service;

    @Test
    void deveCriarColaboradorComSituacaoPadrao() {
        ColaboradoresRequestDTO request = new ColaboradoresRequestDTO(
                " COL-01 ",
                " Maria Souza ",
                "52998224725",
                " 123456 ",
                LocalDate.of(1990, 5, 1),
                " F ",
                "solteira",
                "brasileira",
                " pessoal@email.com ",
                " corporativo@email.com ",
                "1111-1111",
                "99999-9999",
                LocalDate.of(2024, 1, 10),
                null,
                "Analista",
                "RH",
                new BigDecimal("3500.00"),
                "clt",
                40,
                null
        );

        when(repository.save(any(Colaboradores.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Colaboradores saved = service.criar(request);

        assertEquals("COL-01", saved.getCodigo());
        assertEquals("Maria Souza", saved.getNome());
        assertEquals("ativo", saved.getSituacao());

        ArgumentCaptor<Colaboradores> captor = ArgumentCaptor.forClass(Colaboradores.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("3500.00"), captor.getValue().getSalario());
    }

    @Test
    void deveBloquearColaboradorComCpfInvalido() {
        ColaboradoresRequestDTO request = new ColaboradoresRequestDTO(
                null,
                "Maria Souza",
                "11111111111",
                null,
                LocalDate.of(1990, 5, 1),
                "f",
                null,
                null,
                null,
                null,
                null,
                null,
                LocalDate.of(2024, 1, 10),
                null,
                null,
                null,
                new BigDecimal("3500.00"),
                null,
                40,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("CPF invalido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeColaboradorComDependentes() {
        Colaboradores entity = new Colaboradores();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(dependentesRepository.existsByColaboradorId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir colaborador que possui dependentes", exception.getMessage());
        verify(repository, never()).delete(any(Colaboradores.class));
    }
}
