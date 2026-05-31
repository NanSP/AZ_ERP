package com.example.backend.ps.tarefas;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefasServiceTest {

    @Mock
    private TarefasRepository repository;
    @Mock
    private ProjetosRepository projetosRepository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private TarefasService service;

    @Test
    void deveCriarTarefaEAtualizarProjetoParaEmAndamento() {
        Projetos projeto = criarProjeto(1);
        TarefasRequestDTO request = new TarefasRequestDTO(
                1,
                null,
                " Implementar modulo ",
                " backend ",
                2,
                LocalDate.of(2026, 6, 1),
                null,
                new BigDecimal("10"),
                null,
                null,
                null,
                null
        );

        when(projetosRepository.findById(1)).thenReturn(Optional.of(projeto));
        when(usuariosRepository.findById(2)).thenReturn(Optional.of(criarResponsavel(2)));
        when(repository.save(any(Tarefas.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.countByProjetoId(1)).thenReturn(1L);
        when(repository.existsByProjetoIdAndStatus(1, "em_andamento")).thenReturn(false);
        when(repository.existsByProjetoIdAndStatus(1, "pendente")).thenReturn(true);
        when(repository.existsByProjetoIdAndStatus(1, "concluida")).thenReturn(false);
        when(repository.existsByProjetoIdAndStatusNot(1, "concluida")).thenReturn(true);
        when(repository.existsByProjetoIdAndStatusNot(1, "cancelada")).thenReturn(true);

        Tarefas saved = service.criar(request);

        assertEquals("Implementar modulo", saved.getTitulo());
        assertEquals("pendente", saved.getStatus());
        assertEquals(1, saved.getPrioridade());
        assertEquals("em_andamento", projeto.getStatus());
    }

    @Test
    void deveBloquearTarefaConcluidaSemPercentualCem() {
        TarefasRequestDTO request = new TarefasRequestDTO(
                1,
                null,
                "Implementar modulo",
                null,
                2,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                null,
                null,
                90,
                "concluida",
                2
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Tarefa concluida deve ter percentual concluido igual a 100", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeTarefaComSubtarefas() {
        Tarefas entity = new Tarefas();
        entity.setId(10);
        entity.setSubtarefas(List.of(new Tarefas()));

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir tarefa que possui subtarefas", exception.getMessage());
        verify(repository, never()).delete(any(Tarefas.class));
    }

    private Projetos criarProjeto(Integer id) {
        Projetos projeto = new Projetos();
        projeto.setId(id);
        return projeto;
    }

    private Usuarios criarResponsavel(Integer id) {
        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        return usuario;
    }
}
