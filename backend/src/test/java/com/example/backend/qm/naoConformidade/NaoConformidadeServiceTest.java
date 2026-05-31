package com.example.backend.qm.naoConformidade;

import com.example.backend.qm.inspecoes.Inspecoes;
import com.example.backend.qm.inspecoes.InspecoesRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NaoConformidadeServiceTest {

    @Mock
    private NaoConformidadeRepository repository;
    @Mock
    private InspecoesRepository inspecoesRepository;
    @Mock
    private ColaboradoresRepository colaboradoresRepository;

    @InjectMocks
    private NaoConformidadeService service;

    @Test
    void deveCriarNaoConformidadeComStatusPadrao() {
        NaoConformidadeRequestDTO request = new NaoConformidadeRequestDTO(
                1,
                " dimensional ",
                " desvio encontrado ",
                " maquina desregulada ",
                " segregacao ",
                " calibracao ",
                2,
                LocalDate.of(2026, 6, 1),
                null,
                null
        );

        when(inspecoesRepository.findById(1)).thenReturn(Optional.of(criarInspecao()));
        when(colaboradoresRepository.findById(2)).thenReturn(Optional.of(criarResponsavel()));
        when(repository.save(any(NaoConformidade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NaoConformidade saved = service.criar(request);

        assertEquals("aberta", saved.getStatus());
        assertEquals("dimensional", saved.getTipoNaoConformidade());

        ArgumentCaptor<NaoConformidade> captor = ArgumentCaptor.forClass(NaoConformidade.class);
        verify(repository).save(captor.capture());
        assertEquals("desvio encontrado", captor.getValue().getDescricao());
    }

    @Test
    void deveBloquearNaoConformidadeResolvidaSemDataResolucao() {
        NaoConformidadeRequestDTO request = new NaoConformidadeRequestDTO(
                1,
                "dimensional",
                "desvio encontrado",
                null,
                null,
                null,
                2,
                LocalDate.of(2026, 6, 1),
                null,
                "resolvida"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de resolucao e obrigatoria quando a nao conformidade estiver resolvida", exception.getMessage());
    }

    @Test
    void deveBloquearDataResolucaoQuandoStatusNaoForResolvida() {
        NaoConformidadeRequestDTO request = new NaoConformidadeRequestDTO(
                1,
                "dimensional",
                "desvio encontrado",
                null,
                null,
                null,
                2,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2),
                "aberta"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Data de resolucao so deve ser informada quando a nao conformidade estiver resolvida", exception.getMessage());
    }

    private Inspecoes criarInspecao() {
        Inspecoes inspecao = new Inspecoes();
        inspecao.setId(1);
        return inspecao;
    }

    private Colaboradores criarResponsavel() {
        Colaboradores colaborador = new Colaboradores();
        colaborador.setId(2);
        colaborador.setNome("Responsavel");
        return colaborador;
    }
}
