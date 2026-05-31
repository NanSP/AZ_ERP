package com.example.backend.portal.notificacoes;

import com.example.backend.portal.sessoes.SessoesRepository;
import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacoesServiceTest {

    @Mock
    private NotificacoesRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;
    @Mock
    private SessoesRepository sessoesRepository;

    @InjectMocks
    private NotificacoesService service;

    @Test
    void deveCriarNotificacaoComTipoPadrao() {
        NotificacoesRequestDTO request = new NotificacoesRequestDTO(
                1,
                " Titulo ",
                " Mensagem ",
                null,
                false,
                null
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Notificacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notificacoes saved = service.criar(request);

        assertEquals("Titulo", saved.getTitulo());
        assertEquals("Mensagem", saved.getMensagem());
        assertEquals("info", saved.getTipo());
        assertEquals(Boolean.FALSE, saved.getLida());

        ArgumentCaptor<Notificacoes> captor = ArgumentCaptor.forClass(Notificacoes.class);
        verify(repository).save(captor.capture());
        assertEquals(null, captor.getValue().getDataLeitura());
    }

    @Test
    void devePermitirCriarNotificacaoLidaComSessaoAtiva() {
        NotificacoesRequestDTO request = new NotificacoesRequestDTO(
                1,
                "Titulo",
                "Mensagem",
                "alerta",
                true,
                LocalDateTime.now()
        );

        when(sessoesRepository.existsByUsuarioIdAndDataLogoutIsNull(1)).thenReturn(true);
        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Notificacoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notificacoes saved = service.criar(request);

        assertEquals(Boolean.TRUE, saved.getLida());
        assertNotNull(saved.getDataLeitura());
    }

    @Test
    void deveBloquearExclusaoDeNotificacaoLida() {
        Notificacoes entity = new Notificacoes();
        entity.setId(10);
        entity.setLida(true);
        entity.setDataLeitura(LocalDateTime.now());

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir notificacao que ja foi lida", exception.getMessage());
        verify(repository, never()).delete(any(Notificacoes.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Usuario");
        return usuario;
    }
}
