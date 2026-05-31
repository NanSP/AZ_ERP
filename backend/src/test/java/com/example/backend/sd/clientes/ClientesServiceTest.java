package com.example.backend.sd.clientes;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.oportunidades.OportunidadesRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientesServiceTest {

    @Mock
    private ClientesRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private OportunidadesRepository oportunidadesRepository;

    @InjectMocks
    private ClientesService service;

    @Test
    void deveCriarClienteComClassificacaoPadrao() {
        ClientesRequestDTO request = new ClientesRequestDTO(
                1,
                null,
                " Indicacao ",
                " https://cliente.com ",
                new BigDecimal("1000.00"),
                12
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarParceiro()));
        when(repository.save(any(Clientes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Clientes saved = service.criar(request);

        assertEquals("lead", saved.getClassificacao());
        assertEquals("Indicacao", saved.getOrigem());
        assertEquals("https://cliente.com", saved.getWebsite());

        ArgumentCaptor<Clientes> captor = ArgumentCaptor.forClass(Clientes.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("1000.00"), captor.getValue().getFaturamentoAnual());
    }

    @Test
    void deveBloquearClienteComFaturamentoNegativo() {
        ClientesRequestDTO request = new ClientesRequestDTO(
                1,
                "cliente",
                null,
                null,
                new BigDecimal("-1.00"),
                10
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Faturamento anual nao pode ser negativo", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoClientePossuirOportunidades() {
        Clientes entity = new Clientes();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(oportunidadesRepository.existsByClienteId(10)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir cliente que possui oportunidades vinculadas", exception.getMessage());
        verify(repository, never()).delete(any(Clientes.class));
    }

    private Parceiros criarParceiro() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(1);
        parceiro.setNome("Cliente Teste");
        return parceiro;
    }
}
