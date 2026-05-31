package com.example.backend.core.empresas;

import com.example.backend.fi.contasPagar.ContasPagarRepository;
import com.example.backend.fi.contasReceber.ContasReceberRepository;
import com.example.backend.mm.estoques.EstoquesRepository;
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
class EmpresasServiceTest {

    @Mock
    private EmpresasRepository repository;
    @Mock
    private ContasPagarRepository contasPagarRepository;
    @Mock
    private ContasReceberRepository contasReceberRepository;
    @Mock
    private EstoquesRepository estoquesRepository;

    @InjectMocks
    private EmpresasService service;

    @Test
    void deveCriarEmpresaComCnpjNormalizado() {
        EmpresasRequestDTO request = new EmpresasRequestDTO(
                " EMP-01 ",
                " Empresa Teste LTDA ",
                " Empresa Teste ",
                "12.345.678/0001-99",
                null,
                null,
                "Simples",
                LocalDate.of(2020, 1, 1),
                null
        );

        when(repository.save(any(Empresas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Empresas saved = service.criar(request);

        assertEquals("EMP-01", saved.getCodigo());
        assertEquals("Empresa Teste LTDA", saved.getRazaoSocial());
        assertEquals("12345678000199", saved.getCnpj());
        assertEquals("ativo", saved.getSituacao());

        ArgumentCaptor<Empresas> captor = ArgumentCaptor.forClass(Empresas.class);
        verify(repository).save(captor.capture());
        assertEquals("Empresa Teste", captor.getValue().getNomeFantasia());
    }

    @Test
    void deveBloquearDataFundacaoNoFuturo() {
        EmpresasRequestDTO request = new EmpresasRequestDTO(
                "EMP-01",
                "Empresa Teste LTDA",
                null,
                "12345678000199",
                null,
                null,
                null,
                LocalDate.now().plusDays(1),
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de fundacao nao pode estar no futuro", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoEmpresaEstiverEmUso() {
        Empresas entity = new Empresas();
        entity.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(estoquesRepository.existsByEmpresaId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir empresa com uso operacional", exception.getMessage());
        verify(repository, never()).delete(any(Empresas.class));
    }
}
