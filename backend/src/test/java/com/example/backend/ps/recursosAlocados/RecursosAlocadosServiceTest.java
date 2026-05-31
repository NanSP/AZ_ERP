package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.ps.tarefas.Tarefas;
import com.example.backend.ps.tarefas.TarefasRepository;
import com.example.backend.shared.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursosAlocadosServiceTest {

    @Mock
    private RecursosAlocadosRepository repository;
    @Mock
    private ProjetosRepository projetosRepository;
    @Mock
    private TarefasRepository tarefasRepository;

    @InjectMocks
    private RecursosAlocadosService service;

    @Test
    void deveCriarRecursoAlocadoComValorTotalCalculado() {
        Projetos projeto = criarProjeto(1);
        Tarefas tarefa = criarTarefa(2, projeto);
        RecursosAlocadosRequestDTO request = new RecursosAlocadosRequestDTO(
                1,
                2,
                " humano ",
                7,
                new BigDecimal("12"),
                new BigDecimal("15.50"),
                LocalDate.of(2026, 6, 1)
        );

        when(projetosRepository.findById(1)).thenReturn(Optional.of(projeto));
        when(tarefasRepository.findById(2)).thenReturn(Optional.of(tarefa));
        when(repository.save(any(RecursosAlocados.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecursosAlocados saved = service.criar(request);

        assertEquals("humano", saved.getTipoRecurso());
        assertEquals(new BigDecimal("186.00"), saved.getValorTotal());
    }

    @Test
    void deveBloquearRecursoSemProjetoOuTarefa() {
        RecursosAlocadosRequestDTO request = new RecursosAlocadosRequestDTO(
                null,
                null,
                "humano",
                7,
                BigDecimal.ONE,
                BigDecimal.ONE,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Projeto ou tarefa deve ser informado", exception.getMessage());
    }

    @Test
    void deveBloquearTarefaDeProjetoDiferente() {
        Projetos projeto = criarProjeto(1);
        Projetos outroProjeto = criarProjeto(99);
        Tarefas tarefa = criarTarefa(2, outroProjeto);
        RecursosAlocadosRequestDTO request = new RecursosAlocadosRequestDTO(
                1,
                2,
                "material",
                7,
                BigDecimal.ONE,
                new BigDecimal("20"),
                null
        );

        when(projetosRepository.findById(1)).thenReturn(Optional.of(projeto));
        when(tarefasRepository.findById(2)).thenReturn(Optional.of(tarefa));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tarefa deve pertencer ao mesmo projeto informado", exception.getMessage());
    }

    private Projetos criarProjeto(Integer id) {
        Projetos projeto = new Projetos();
        projeto.setId(id);
        return projeto;
    }

    private Tarefas criarTarefa(Integer id, Projetos projeto) {
        Tarefas tarefa = new Tarefas();
        tarefa.setId(id);
        tarefa.setProjeto(projeto);
        return tarefa;
    }
}
