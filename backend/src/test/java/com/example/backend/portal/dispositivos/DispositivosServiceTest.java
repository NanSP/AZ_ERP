package com.example.backend.portal.dispositivos;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispositivosServiceTest {

    @Mock
    private DispositivosRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;
    @Mock
    private SessoesRepository sessoesRepository;

    @InjectMocks
    private DispositivosService service;

    @Test
    void deveCriarDispositivoComPlatformNormalizada() {
        DispositivosRequestDTO request = new DispositivosRequestDTO(
                1,
                " device-1 ",
                " iPhone ",
                " IOS ",
                " push-1 ",
                null,
                null
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario()));
        when(repository.save(any(Dispositivos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dispositivos saved = service.criar(request);

        assertEquals("device-1", saved.getDeviceId());
        assertEquals("ios", saved.getDevicePlatform());
        assertEquals(Boolean.TRUE, saved.getAtivo());

        ArgumentCaptor<Dispositivos> captor = ArgumentCaptor.forClass(Dispositivos.class);
        verify(repository).save(captor.capture());
        assertEquals("push-1", captor.getValue().getPushToken());
    }

    @Test
    void deveBloquearDispositivoComPushTokenAtivoDuplicado() {
        DispositivosRequestDTO request = new DispositivosRequestDTO(
                1,
                "device-1",
                "iPhone",
                "ios",
                "push-1",
                null,
                true
        );

        when(repository.existsByPushTokenAndAtivoTrue("push-1")).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Ja existe dispositivo ativo com o push token informado", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDeDispositivoComHistoricoDeAcesso() {
        Dispositivos entity = new Dispositivos();
        entity.setId(10);
        entity.setAtivo(false);
        entity.setUltimoAcesso(LocalDateTime.now());

        when(repository.findById(10)).thenReturn(Optional.of(entity));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido excluir dispositivo com historico de acesso", exception.getMessage());
        verify(repository, never()).delete(any(Dispositivos.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Usuario");
        return usuario;
    }
}
