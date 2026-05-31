package com.example.backend.bi.relatorios;

import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatoriosServiceTest {

    @Mock
    private RelatoriosRepository repository;

    @InjectMocks
    private RelatoriosService service;

    @Test
    void deveCriarRelatorioComQuerySelectValida() {
        RelatoriosRequestDTO request = new RelatoriosRequestDTO(
                " Receita por mes ",
                " Relatorio financeiro ",
                "tabela",
                " SELECT * FROM receitas ",
                Map.of("ano", 2026)
        );

        when(repository.save(any(Relatorios.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Relatorios saved = service.criar(request);

        assertEquals("Receita por mes", saved.getNome());
        assertEquals("tabela", saved.getTipoRelatorio());
        assertEquals("SELECT * FROM receitas", saved.getQuerySql());

        ArgumentCaptor<Relatorios> captor = ArgumentCaptor.forClass(Relatorios.class);
        verify(repository).save(captor.capture());
        assertEquals(1, captor.getValue().getParametros().size());
    }

    @Test
    void deveBloquearRelatorioComQueryNaoPermitida() {
        RelatoriosRequestDTO request = new RelatoriosRequestDTO(
                " Receita por mes ",
                null,
                "tabela",
                "DELETE FROM receitas",
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("A query SQL do relatorio deve iniciar com SELECT", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeRelatorio() {
        Relatorios entity = new Relatorios();
        entity.setId(1);
        entity.setNome("Receita por mes");

        when(repository.findById(1)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Relatorio nao pode ser excluido", exception.getMessage());
        verify(repository, never()).delete(any(Relatorios.class));
    }
}
