package com.example.backend.grc.auditorias;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriasServiceTest {

    @Mock
    private AuditoriasRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private AuditoriasService service;

    @Test
    void deveCriarAuditoriaPlanejadaComCamposNormalizados() {
        AuditoriasRequestDTO request = new AuditoriasRequestDTO(
                " Auditoria interna ",
                " INTERNA ",
                " financeiro ",
                LocalDate.of(2026, 6, 10),
                null,
                null,
                null
        );

        when(repository.save(any(Auditorias.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Auditorias saved = service.criar(request);

        assertEquals("Auditoria interna", saved.getTitulo());
        assertEquals("interna", saved.getTipoAuditoria());
        assertEquals("planejada", saved.getStatus());

        ArgumentCaptor<Auditorias> captor = ArgumentCaptor.forClass(Auditorias.class);
        verify(repository).save(captor.capture());
        assertEquals("financeiro", captor.getValue().getEscopo());
    }

    @Test
    void deveBloquearAuditoriaEmAndamentoSemResponsavel() {
        AuditoriasRequestDTO request = new AuditoriasRequestDTO(
                "Auditoria interna",
                "interna",
                null,
                LocalDate.of(2026, 6, 10),
                null,
                null,
                "em_andamento"
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Auditoria em execucao deve possuir responsavel", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeAuditoriaConcluida() {
        Auditorias entity = new Auditorias();
        entity.setId(10);
        entity.setStatus("concluida");

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir auditoria que ja entrou em ciclo de execucao", exception.getMessage());
        verify(repository, never()).delete(any(Auditorias.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Responsavel");
        return usuario;
    }
}
