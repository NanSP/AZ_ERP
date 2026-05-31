package com.example.backend.fi.contasPagar;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.fi.centrosCusto.CentrosCustoRepository;
import com.example.backend.shared.exception.RegraNegocioException;
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
class ContasPagarServiceTest {

    @Mock
    private ContasPagarRepository repository;
    @Mock
    private EmpresasRepository empresasRepository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private CentrosCustoRepository centrosCustoRepository;

    @InjectMocks
    private ContasPagarService service;

    @Test
    void deveCriarContaPagarComStatusParcial() {
        ContasPagarRequestDTO request = new ContasPagarRequestDTO(
                1,
                2,
                null,
                "NF-001",
                "Compra de material",
                new BigDecimal("1000.00"),
                new BigDecimal("400.00"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 8),
                "PIX"
        );

        when(empresasRepository.findById(1)).thenReturn(Optional.of(criarEmpresaAtiva()));
        when(parceirosRepository.findById(2)).thenReturn(Optional.of(criarFornecedorAtivo()));
        when(repository.save(any(ContasPagar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContasPagar saved = service.criar(request);

        assertEquals("parcial", saved.getStatus());
        assertEquals(new BigDecimal("400.00"), saved.getValorPago());

        ArgumentCaptor<ContasPagar> captor = ArgumentCaptor.forClass(ContasPagar.class);
        verify(repository).save(captor.capture());
        assertEquals("Compra de material", captor.getValue().getDescricao());
    }

    @Test
    void deveBloquearContaPagarQuandoParceiroNaoForFornecedor() {
        ContasPagarRequestDTO request = new ContasPagarRequestDTO(
                1,
                2,
                null,
                "NF-001",
                "Compra de material",
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 10),
                null,
                "PIX"
        );

        Parceiros parceiro = criarFornecedorAtivo();
        parceiro.setTipoParceiro("cliente");

        when(empresasRepository.findById(1)).thenReturn(Optional.of(criarEmpresaAtiva()));
        when(parceirosRepository.findById(2)).thenReturn(Optional.of(parceiro));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Parceiro informado precisa ser do tipo fornecedor", exception.getMessage());
    }

    @Test
    void deveBloquearAtualizacaoDeContaPaga() {
        ContasPagar entity = new ContasPagar();
        entity.setId(10);
        entity.setStatus("pago");

        ContasPagarRequestDTO request = new ContasPagarRequestDTO(
                1,
                2,
                null,
                "NF-001",
                "Compra de material",
                new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 8),
                "PIX"
        );

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.atualizar(10, request));

        assertEquals("Nao e permitido editar ou excluir conta a pagar com status pago", exception.getMessage());
        verify(repository, never()).save(any(ContasPagar.class));
    }

    private Empresas criarEmpresaAtiva() {
        Empresas empresa = new Empresas();
        empresa.setId(1);
        empresa.setSituacao("ativo");
        return empresa;
    }

    private Parceiros criarFornecedorAtivo() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(2);
        parceiro.setSituacao("ativo");
        parceiro.setTipoParceiro("fornecedor");
        return parceiro;
    }
}
