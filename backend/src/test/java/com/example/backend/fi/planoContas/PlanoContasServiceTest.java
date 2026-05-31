package com.example.backend.fi.planoContas;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanoContasServiceTest {

    @Mock
    private PlanoContasRepository repository;

    @InjectMocks
    private PlanoContasService service;

    @Test
    void deveCriarPlanoDeContasComContaPaiValida() {
        PlanoContas contaPai = new PlanoContas();
        contaPai.setId(10);

        PlanoContasRequestDTO request = new PlanoContasRequestDTO(
                " 1.1.01 ",
                " Caixa ",
                "analitica",
                "devedora",
                10,
                null
        );

        when(repository.findById(10)).thenReturn(Optional.of(contaPai));
        when(repository.save(any(PlanoContas.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlanoContas saved = service.criar(request);

        assertEquals("1.1.01", saved.getCodigo());
        assertEquals("Caixa", saved.getNome());
        assertEquals("analitica", saved.getTipoConta());
        assertEquals("ativo", saved.getSituacao());
        assertEquals(10, saved.getContaPai().getId());

        ArgumentCaptor<PlanoContas> captor = ArgumentCaptor.forClass(PlanoContas.class);
        verify(repository).save(captor.capture());
        assertEquals("devedora", captor.getValue().getNatureza());
    }

    @Test
    void deveBloquearCicloHierarquicoNoPlanoDeContas() {
        PlanoContas contaAtual = new PlanoContas();
        contaAtual.setId(1);

        PlanoContas contaPai = new PlanoContas();
        contaPai.setId(2);
        contaPai.setContaPai(contaAtual);

        PlanoContasRequestDTO request = new PlanoContasRequestDTO(
                "1.1.01",
                "Caixa",
                "analitica",
                "devedora",
                2,
                "ativo"
        );

        when(repository.findById(1)).thenReturn(Optional.of(contaAtual));
        when(repository.findById(2)).thenReturn(Optional.of(contaPai));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(1, request));

        assertEquals("Nao e permitido criar ciclo na hierarquia do plano de contas", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeContaComFilhas() {
        PlanoContas entity = new PlanoContas();
        entity.setId(1);
        entity.setContasFilhas(new ArrayList<>());
        entity.getContasFilhas().add(new PlanoContas());

        when(repository.findById(1)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir conta que possui contas filhas", exception.getMessage());
        verify(repository, never()).delete(any(PlanoContas.class));
    }
}
