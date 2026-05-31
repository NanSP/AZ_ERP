package com.example.backend.sm.atendimentos;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sm.ordensServico.OrdensServico;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtendimentosServiceTest {

    @Mock
    private AtendimentosRepository repository;
    @Mock
    private OrdensServicoRepository ordensServicoRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private AtendimentosService service;

    @Test
    void deveCriarAtendimentoEPromoverOsParaEmAndamento() {
        OrdensServico os = criarOs("aberta", LocalDateTime.of(2026, 6, 1, 8, 0));
        AtendimentosRequestDTO request = new AtendimentosRequestDTO(
                1,
                2,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                " Atendimento inicial ",
                null,
                Map.of("peca", "fusivel")
        );

        when(ordensServicoRepository.findById(1)).thenReturn(Optional.of(os));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarTecnico()));
        when(repository.save(any(Atendimentos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Atendimentos saved = service.criar(request);

        assertEquals("em_andamento", os.getStatus());
        assertEquals(BigDecimal.ZERO, saved.getHorasGastas());

        ArgumentCaptor<Atendimentos> captor = ArgumentCaptor.forClass(Atendimentos.class);
        verify(repository).save(captor.capture());
        assertEquals("atendimento inicial", captor.getValue().getDescricao());
    }

    @Test
    void deveBloquearAtendimentoParaOsCancelada() {
        AtendimentosRequestDTO request = new AtendimentosRequestDTO(
                1,
                2,
                LocalDateTime.of(2026, 6, 1, 9, 0),
                null,
                BigDecimal.ONE,
                null
        );

        when(ordensServicoRepository.findById(1)).thenReturn(Optional.of(criarOs("cancelada", LocalDateTime.of(2026, 6, 1, 8, 0))));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarTecnico()));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido registrar atendimento para ordem de servico cancelada", exception.getMessage());
    }

    @Test
    void deveNormalizarMateriaisVaziosParaNull() {
        OrdensServico os = criarOs("em_andamento", LocalDateTime.of(2026, 6, 1, 8, 0));
        AtendimentosRequestDTO request = new AtendimentosRequestDTO(
                1,
                2,
                LocalDateTime.of(2026, 6, 1, 10, 0),
                "Revisao",
                new BigDecimal("1.50"),
                Map.of()
        );

        when(ordensServicoRepository.findById(1)).thenReturn(Optional.of(os));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarTecnico()));
        when(repository.save(any(Atendimentos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Atendimentos saved = service.criar(request);

        assertNull(saved.getMateriaisUtilizados());
        assertEquals(new BigDecimal("1.50"), saved.getHorasGastas());
    }

    private OrdensServico criarOs(String status, LocalDateTime dataAbertura) {
        OrdensServico os = new OrdensServico();
        os.setId(1);
        os.setStatus(status);
        os.setDataAbertura(dataAbertura);
        return os;
    }

    private Colaboradores criarTecnico() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(2);
        colaborador.setNome("Tecnico");
        return colaborador;
    }
}
