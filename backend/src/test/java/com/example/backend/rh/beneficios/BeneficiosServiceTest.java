package com.example.backend.rh.beneficios;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeneficiosServiceTest {

    @Mock
    private BeneficiosRepository repository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private BeneficiosService service;

    @Test
    void deveCriarBeneficioAtivoComTipoNormalizado() {
        BeneficiosRequestDTO request = new BeneficiosRequestDTO(
                1,
                " VALE_TRANSPORTE ",
                new BigDecimal("300.00"),
                LocalDate.now().plusDays(1),
                null,
                null
        );

        when(colaboradoresRepository.findById(1)).thenReturn(Optional.of(criarColaboradorAtivo()));
        when(repository.save(any(Beneficios.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Beneficios saved = service.criar(request);

        assertEquals("vale_transporte", saved.getTipoBeneficio());
        assertEquals(Boolean.TRUE, saved.getAtivo());

        ArgumentCaptor<Beneficios> captor = ArgumentCaptor.forClass(Beneficios.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("300.00"), captor.getValue().getValor());
    }

    @Test
    void deveBloquearBeneficioAtivoSemDataInicio() {
        BeneficiosRequestDTO request = new BeneficiosRequestDTO(
                1,
                "vale_transporte",
                new BigDecimal("300.00"),
                null,
                null,
                true
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de inicio e obrigatoria para beneficio ativo", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeBeneficioAtivo() {
        Beneficios entity = new Beneficios();
        entity.setId(10);
        entity.setAtivo(true);

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir beneficio ativo; desative-o antes", exception.getMessage());
        verify(repository, never()).delete(any(Beneficios.class));
    }

    private Colaboradores criarColaboradorAtivo() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(1);
        colaborador.setSituacao("ativo");
        colaborador.setDataAdmissao(LocalDate.of(2024, 1, 10));
        return colaborador;
    }
}
