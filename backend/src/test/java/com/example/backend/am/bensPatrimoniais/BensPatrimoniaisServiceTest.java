package com.example.backend.am.bensPatrimoniais;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BensPatrimoniaisServiceTest {

    @Mock
    private BensPatrimoniaisRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private BensPatrimoniaisService service;

    @Test
    void deveCriarBemPatrimonialComStatusPadraoENormalizacao() {
        BensPatrimoniaisRequestDTO request = new BensPatrimoniaisRequestDTO(
                " PAT-001 ",
                " Notebook ",
                " uso administrativo ",
                " tecnologia ",
                " sala 1 ",
                LocalDate.of(2026, 1, 10),
                new BigDecimal("5000.00"),
                null,
                5,
                new BigDecimal("10.00"),
                null,
                1,
                2,
                null
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarFornecedor(1)));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarResponsavel(2)));
        when(repository.existsByCodigoPatrimonio("PAT-001")).thenReturn(false);
        when(repository.save(any(BensPatrimoniais.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BensPatrimoniais saved = service.criar(request);

        assertEquals("PAT-001", saved.getCodigoPatrimonio());
        assertEquals("Notebook", saved.getNome());
        assertEquals("uso administrativo", saved.getDescricao());
        assertEquals("ativo", saved.getStatus());
        assertEquals(BigDecimal.ZERO, saved.getValorAtual());

        ArgumentCaptor<BensPatrimoniais> captor = ArgumentCaptor.forClass(BensPatrimoniais.class);
        verify(repository).save(captor.capture());
        assertEquals("sala 1", captor.getValue().getLocalizacao());
    }

    @Test
    void deveBloquearValorAtualMaiorQueValorAquisicao() {
        BensPatrimoniaisRequestDTO request = new BensPatrimoniaisRequestDTO(
                "PAT-001",
                "Notebook",
                null,
                null,
                null,
                LocalDate.of(2026, 1, 10),
                new BigDecimal("5000.00"),
                new BigDecimal("5000.01"),
                null,
                null,
                null,
                null,
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Valor atual nao pode ser maior que o valor de aquisicao", exception.getMessage());
    }

    @Test
    void deveBloquearDataDepreciacaoAnteriorAquisicao() {
        BensPatrimoniaisRequestDTO request = new BensPatrimoniaisRequestDTO(
                "PAT-001",
                "Notebook",
                null,
                null,
                null,
                LocalDate.of(2026, 1, 10),
                new BigDecimal("5000.00"),
                new BigDecimal("4000.00"),
                null,
                null,
                LocalDate.of(2026, 1, 9),
                null,
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de depreciacao nao pode ser anterior a data de aquisicao", exception.getMessage());
    }

    private Parceiros criarFornecedor(Integer id) {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(id);
        return parceiro;
    }

    private Colaboradores criarResponsavel(Integer id) {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(id);
        return colaborador;
    }
}
