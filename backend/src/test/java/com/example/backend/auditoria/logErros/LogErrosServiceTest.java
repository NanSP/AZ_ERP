package com.example.backend.auditoria.logErros;

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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogErrosServiceTest {

    @Mock
    private LogErrosRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private LogErrosService service;

    @Test
    void deveCriarLogErroComUrlRelativaEParametros() throws Exception {
        LogErrosRequestDTO request = new LogErrosRequestDTO(
                500,
                "Falha interna",
                "portal",
                1,
                "/api/portal/notificacoes",
                Map.of("id", 10),
                InetAddress.getByName("127.0.0.1")
        );

        when(repository.existsByModuloAndErroMensagemAndUrlAndCreatedAtAfter(
                eq("portal"),
                eq("Falha interna"),
                eq("/api/portal/notificacoes"),
                any()
        )).thenReturn(false);
        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario(1)));
        when(repository.save(any(LogErros.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LogErros saved = service.criar(request);

        assertEquals("Falha interna", saved.getErroMensagem());
        assertEquals("portal", saved.getModulo());
        assertEquals("/api/portal/notificacoes", saved.getUrl());

        ArgumentCaptor<LogErros> captor = ArgumentCaptor.forClass(LogErros.class);
        verify(repository).save(captor.capture());
        assertEquals(500, captor.getValue().getErroCodigo());
    }

    @Test
    void deveBloquearDuplicidadeRecenteDeLogErro() {
        LogErrosRequestDTO request = new LogErrosRequestDTO(
                500,
                "Falha interna",
                "portal",
                null,
                "/api/portal/notificacoes",
                null,
                null
        );

        when(repository.existsByModuloAndErroMensagemAndUrlAndCreatedAtAfter(
                eq("portal"),
                eq("Falha interna"),
                eq("/api/portal/notificacoes"),
                any()
        )).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Ja existe log de erro identico registrado recentemente", exception.getMessage());
    }

    @Test
    void deveBloquearIpSemUrlNoLogDeErro() throws Exception {
        LogErrosRequestDTO request = new LogErrosRequestDTO(
                500,
                "Falha interna",
                "portal",
                null,
                null,
                null,
                InetAddress.getByName("127.0.0.1")
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("URL deve ser informada quando houver IP no log de erro", exception.getMessage());
    }

    private Usuarios criarUsuario(Integer id) {
        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        return usuario;
    }
}
