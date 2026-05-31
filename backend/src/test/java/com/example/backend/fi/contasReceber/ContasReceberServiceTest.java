package com.example.backend.fi.contasReceber;

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
class ContasReceberServiceTest {

    @Mock
    private ContasReceberRepository repository;
    @Mock
    private EmpresasRepository empresasRepository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private CentrosCustoRepository centrosCustoRepository;

    @InjectMocks
    private ContasReceberService service;

    @Test
    void deveCriarContaReceberComStatusPendente() {
        ContasReceberRequestDTO request = new ContasReceberRequestDTO(
                1,
                2,
                null,
                "FAT-001",
                "Venda realizada",
                new BigDecimal("1500.00"),
                BigDecimal.ZERO,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 20),
                null,
                "Boleto"
        );

        when(empresasRepository.findById(1)).thenReturn(Optional.of(criarEmpresaAtiva()));
        when(parceirosRepository.findById(2)).thenReturn(Optional.of(criarClienteAtivo()));
        when(repository.save(any(ContasReceber.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContasReceber saved = service.criar(request);

        assertEquals("pendente", saved.getStatus());
        assertEquals(new BigDecimal("0"), saved.getValorRecebido());

        ArgumentCaptor<ContasReceber> captor = ArgumentCaptor.forClass(ContasReceber.class);
        verify(repository).save(captor.capture());
        assertEquals("Venda realizada", captor.getValue().getDescricao());
    }

    @Test
    void deveBloquearContaReceberQuandoParceiroNaoForCliente() {
        ContasReceberRequestDTO request = new ContasReceberRequestDTO(
                1,
                2,
                null,
                "FAT-001",
                "Venda realizada",
                new BigDecimal("1500.00"),
                BigDecimal.ZERO,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 20),
                null,
                "Boleto"
        );

        Parceiros parceiro = criarClienteAtivo();
        parceiro.setTipoParceiro("fornecedor");

        when(empresasRepository.findById(1)).thenReturn(Optional.of(criarEmpresaAtiva()));
        when(parceirosRepository.findById(2)).thenReturn(Optional.of(parceiro));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Parceiro informado precisa ser do tipo cliente", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeContaReceberPaga() {
        ContasReceber entity = new ContasReceber();
        entity.setId(10);
        entity.setStatus("pago");

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido editar ou excluir conta a receber com status pago", exception.getMessage());
        verify(repository, never()).delete(any(ContasReceber.class));
    }

    private Empresas criarEmpresaAtiva() {
        Empresas empresa = new Empresas();
        empresa.setId(1);
        empresa.setSituacao("ativo");
        return empresa;
    }

    private Parceiros criarClienteAtivo() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(2);
        parceiro.setSituacao("ativo");
        parceiro.setTipoParceiro("cliente");
        return parceiro;
    }
}
