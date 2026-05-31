package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.rh.folhaDePagamento.FolhaDePagamentoRepository;
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
class DependentesServiceTest {

    @Mock
    private DependentesRepository repository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;
    @Mock
    private FolhaDePagamentoRepository folhaDePagamentoRepository;

    @InjectMocks
    private DependentesService service;

    @Test
    void deveCriarDependenteComCpfNormalizado() {
        DependentesRequestDTO request = new DependentesRequestDTO(
                1,
                " Filho Um ",
                LocalDate.of(2015, 3, 20),
                " FILHO ",
                "529.982.247-25"
        );

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(criarColaborador()));
        when(repository.save(any(Dependentes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dependentes saved = service.criar(request);

        assertEquals("Filho Um", saved.getNome());
        assertEquals("filho", saved.getParentesco());
        assertEquals("52998224725", saved.getCpf());

        ArgumentCaptor<Dependentes> captor = ArgumentCaptor.forClass(Dependentes.class);
        verify(repository).save(captor.capture());
        assertEquals(LocalDate.of(2015, 3, 20), captor.getValue().getDataNascimento());
    }

    @Test
    void deveBloquearDependenteComParentescoInvalido() {
        DependentesRequestDTO request = new DependentesRequestDTO(
                1,
                "Filho Um",
                LocalDate.of(2015, 3, 20),
                "irmao",
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Parentesco invalido", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeDependenteComHistoricoDeFolha() {
        Colaboradores colaborador = criarColaborador();
        colaborador.setId(1);

        Dependentes entity = new Dependentes();
        entity.setId(10);
        entity.setColaborador(colaborador);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(folhaDePagamentoRepository.existsByColaboradorId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir dependente de colaborador com historico de folha de pagamento", exception.getMessage());
        verify(repository, never()).delete(any(Dependentes.class));
    }

    private Colaboradores criarColaborador() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(1);
        colaborador.setNome("Maria");
        return colaborador;
    }
}
