package com.example.backend.portal.sessoes;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
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
class SessoesServiceTest {

    @Mock
    private SessoesRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private SessoesService service;

    @Test
    void deveCriarSessaoComDataLoginAutomatica() throws Exception {
        SessoesRequestDTO request = new SessoesRequestDTO(
                1,
                " token-123 ",
                InetAddress.getByName("127.0.0.1"),
                " browser ",
                null,
                null,
                LocalDateTime.now().plusHours(2)
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Sessoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sessoes saved = service.criar(request);

        assertEquals("token-123", saved.getTokenSessao());
        assertEquals("browser", saved.getUserAgent());
        assertNotNull(saved.getDataLogin());

        ArgumentCaptor<Sessoes> captor = ArgumentCaptor.forClass(Sessoes.class);
        verify(repository).save(captor.capture());
        assertEquals(InetAddress.getByName("127.0.0.1"), captor.getValue().getIpAddress());
    }

    @Test
    void deveBloquearCriacaoDeSessaoQuandoUsuarioJaTemSessaoAtiva() throws Exception {
        SessoesRequestDTO request = new SessoesRequestDTO(
                1,
                "token-123",
                InetAddress.getByName("127.0.0.1"),
                "browser",
                LocalDateTime.now(),
                null,
                LocalDateTime.now().plusHours(2)
        );

        when(repository.existsByUsuarioIdAndDataLogoutIsNull(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Usuario ja possui sessao ativa", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeSessao() {
        Sessoes entity = new Sessoes();
        entity.setId(10);

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Sessao nao pode ser excluida", exception.getMessage());
        verify(repository, never()).delete(any(Sessoes.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Usuario");
        return usuario;
    }
}
