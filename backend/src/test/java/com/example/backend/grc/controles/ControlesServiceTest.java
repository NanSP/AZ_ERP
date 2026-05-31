package com.example.backend.grc.controles;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControlesServiceTest {

    @Mock
    private ControlesRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private ControlesService service;

    @Test
    void deveCriarControleFormalizadoComResponsavel() {
        ControlesRequestDTO request = new ControlesRequestDTO(
                " CTRL-01 ",
                " Controle preventivo ",
                " PREVENTIVO ",
                " MENSAL ",
                1,
                true
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Controles.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Controles saved = service.criar(request);

        assertEquals("CTRL-01", saved.getCodigo());
        assertEquals("preventivo", saved.getTipoControle());
        assertEquals("mensal", saved.getFrequencia());

        ArgumentCaptor<Controles> captor = ArgumentCaptor.forClass(Controles.class);
        verify(repository).save(captor.capture());
        assertEquals(Boolean.TRUE, captor.getValue().getEfetivo());
    }

    @Test
    void deveBloquearControleEfetivoSemResponsavel() {
        ControlesRequestDTO request = new ControlesRequestDTO(
                null,
                "Controle preventivo",
                "preventivo",
                "mensal",
                null,
                true
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Controle efetivo deve possuir responsavel", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeControleFormalizado() {
        Controles entity = new Controles();
        entity.setId(10);
        entity.setCodigo("CTRL-01");

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir controle formalizado com codigo", exception.getMessage());
        verify(repository, never()).delete(any(Controles.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Responsavel");
        return usuario;
    }
}
