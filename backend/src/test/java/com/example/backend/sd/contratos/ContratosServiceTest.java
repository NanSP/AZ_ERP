package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
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
class ContratosServiceTest {

    @Mock
    private ContratosRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private ClientesRepository clientesRepository;

    @InjectMocks
    private ContratosService service;

    @Test
    void deveCriarContratoVigenteComCamposNormalizados() {
        ContratosRequestDTO request = new ContratosRequestDTO(
                1,
                " CTR-001 ",
                " licenciamento anual ",
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 6, 1),
                null,
                null
        );

        when(clientesRepository.existsByParceiroId(1)).thenReturn(true);
        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarCliente()));
        when(repository.save(any(Contratos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Contratos saved = service.criar(request);

        assertEquals("CTR-001", saved.getNumeroContrato());
        assertEquals("licenciamento anual", saved.getObjeto());
        assertEquals("vigente", saved.getStatus());

        ArgumentCaptor<Contratos> captor = ArgumentCaptor.forClass(Contratos.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("5000.00"), captor.getValue().getValorTotal());
    }

    @Test
    void deveBloquearContratoDeClienteNaoComercial() {
        ContratosRequestDTO request = new ContratosRequestDTO(
                1,
                "CTR-001",
                null,
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 6, 1),
                null,
                "vigente"
        );

        when(clientesRepository.existsByParceiroId(1)).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Cliente informado precisa estar cadastrado no CRM", exception.getMessage());
    }

    @Test
    void deveBloquearContratoEncerradoSemDataFim() {
        ContratosRequestDTO request = new ContratosRequestDTO(
                1,
                "CTR-001",
                null,
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 6, 1),
                null,
                "encerrado"
        );

        when(clientesRepository.existsByParceiroId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data fim e obrigatoria para contrato encerrado ou cancelado", exception.getMessage());
    }

    private Parceiros criarCliente() {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(1);
        parceiro.setNome("Cliente");
        return parceiro;
    }
}
