package com.example.backend.core.parceiros;

import com.example.backend.fi.contasPagar.ContasPagarRepository;
import com.example.backend.fi.contasReceber.ContasReceberRepository;
import com.example.backend.fiscal.documentos.DocumentosRepository;
import com.example.backend.sd.clientes.ClientesRepository;
import com.example.backend.sd.contratos.ContratosRepository;
import com.example.backend.sd.oportunidades.OportunidadesRepository;
import com.example.backend.sd.pedidos.PedidosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
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
class ParceirosServiceTest {

    @Mock
    private ParceirosRepository repository;
    @Mock
    private ContasPagarRepository contasPagarRepository;
    @Mock
    private ContasReceberRepository contasReceberRepository;
    @Mock
    private PedidosRepository pedidosRepository;
    @Mock
    private ContratosRepository contratosRepository;
    @Mock
    private OportunidadesRepository oportunidadesRepository;
    @Mock
    private ClientesRepository clientesRepository;
    @Mock
    private OrdensServicoRepository ordensServicoRepository;
    @Mock
    private DocumentosRepository documentosRepository;

    @InjectMocks
    private ParceirosService service;

    @Test
    void deveCriarParceiroComDocumentoNormalizado() {
        ParceirosRequestDTO request = new ParceirosRequestDTO(
                "cliente",
                " PARC-01 ",
                " Parceiro Teste ",
                " Fantasia ",
                "12345678000199",
                "j",
                null,
                new BigDecimal("1000.00"),
                30,
                " obs "
        );

        when(repository.save(any(Parceiros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Parceiros saved = service.criar(request);

        assertEquals("cliente", saved.getTipoParceiro());
        assertEquals("PARC-01", saved.getCodigo());
        assertEquals("Parceiro Teste", saved.getNome());
        assertEquals("12345678000199", saved.getDocumento());
        assertEquals("J", saved.getTipoPessoa());
        assertEquals("ativo", saved.getSituacao());

        ArgumentCaptor<Parceiros> captor = ArgumentCaptor.forClass(Parceiros.class);
        verify(repository).save(captor.capture());
        assertEquals(30, captor.getValue().getDiasPrazo());
    }

    @Test
    void deveBloquearDocumentoSemTipoPessoa() {
        ParceirosRequestDTO request = new ParceirosRequestDTO(
                "cliente",
                null,
                "Parceiro Teste",
                null,
                "12345678901",
                null,
                null,
                null,
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tipo de pessoa e obrigatorio quando houver documento", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoParceiroEstiverEmUso() {
        Parceiros entity = new Parceiros();
        entity.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(contasReceberRepository.existsByClienteId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir parceiro com uso operacional", exception.getMessage());
        verify(repository, never()).delete(any(Parceiros.class));
    }
}
