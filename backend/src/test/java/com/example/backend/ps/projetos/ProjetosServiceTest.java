package com.example.backend.ps.projetos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ProjetosServiceTest {

    @Mock
    private ProjetosRepository repository;
    @Mock
    private ParceirosRepository parceirosRepository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private ProjetosService service;

    @Test
    void deveCriarProjetoComDefaultsENormalizacao() {
        ProjetosRequestDTO request = new ProjetosRequestDTO(
                " PRJ-001 ",
                " Projeto ERP ",
                " Implantacao ",
                1,
                2,
                LocalDate.of(2026, 6, 1),
                null,
                LocalDate.of(2026, 6, 5),
                LocalDate.of(2026, 6, 30),
                new BigDecimal("10000.00"),
                null,
                null,
                null
        );

        when(parceirosRepository.findById(1)).thenReturn(Optional.of(criarCliente(1)));
        when(usuariosRepository.findById(2)).thenReturn(Optional.of(criarGerente(2)));
        when(repository.save(any(Projetos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projetos saved = service.criar(request);

        assertEquals("PRJ-001", saved.getCodigo());
        assertEquals("Projeto ERP", saved.getNome());
        assertEquals("Implantacao", saved.getDescricao());
        assertEquals("planejado", saved.getStatus());
        assertEquals(1, saved.getPrioridade());
        assertEquals(BigDecimal.ZERO, saved.getOrcamentoGasto());

        ArgumentCaptor<Projetos> captor = ArgumentCaptor.forClass(Projetos.class);
        verify(repository).save(captor.capture());
        assertEquals(new BigDecimal("10000.00"), captor.getValue().getOrcamentoTotal());
    }

    @Test
    void deveBloquearProjetoComOrcamentoGastoMaiorQueTotal() {
        ProjetosRequestDTO request = new ProjetosRequestDTO(
                null,
                "Projeto ERP",
                null,
                null,
                2,
                null,
                null,
                null,
                null,
                new BigDecimal("1000.00"),
                new BigDecimal("1000.01"),
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Orcamento gasto nao pode ser maior que o orcamento total", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeProjetoComTarefas() {
        Projetos entity = new Projetos();
        entity.setId(10);
        entity.setTarefas(List.of(new com.example.backend.ps.tarefas.Tarefas()));

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir projeto que possui tarefas", exception.getMessage());
        verify(repository, never()).delete(any(Projetos.class));
    }

    private Parceiros criarCliente(Integer id) {
        Parceiros parceiro = new Parceiros();
        parceiro.setId(id);
        return parceiro;
    }

    private Usuarios criarGerente(Integer id) {
        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        return usuario;
    }
}
