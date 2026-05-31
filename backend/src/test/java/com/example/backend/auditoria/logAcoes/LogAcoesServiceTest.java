package com.example.backend.auditoria.logAcoes;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogAcoesServiceTest {

    @Mock
    private LogAcoesRepository repository;
    @Mock
    private UsuariosRepository usuariosRepository;

    @InjectMocks
    private LogAcoesService service;

    @Test
    void deveCriarLogAcaoComUsuarioERastreabilidade() throws Exception {
        LogAcoesRequestDTO request = new LogAcoesRequestDTO(
                1,
                "sys",
                "update",
                "usuarios",
                10,
                Map.of("nome", "antigo"),
                Map.of("nome", "novo"),
                InetAddress.getByName("127.0.0.1"),
                " browser "
        );

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario(1)));
        when(repository.save(any(LogAcoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LogAcoes saved = service.criar(request);

        assertEquals("sys", saved.getModulo());
        assertEquals("update", saved.getAcao());
        assertEquals("usuarios", saved.getTabela());
        assertEquals("browser", saved.getUserAgent());
        assertEquals(10, saved.getRegistroId());
    }

    @Test
    void deveBloquearAcaoSemRegistroIdQuandoNaoForSessao() {
        LogAcoesRequestDTO request = new LogAcoesRequestDTO(
                null,
                "sys",
                "update",
                "usuarios",
                null,
                null,
                Map.of("nome", "novo"),
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Registro ID e obrigatorio para esta acao", exception.getMessage());
    }

    @Test
    void deveBloquearUsuarioSemIpNoLogDeAcao() {
        LogAcoesRequestDTO request = new LogAcoesRequestDTO(
                1,
                "auth",
                "login",
                "sessao",
                null,
                null,
                null,
                null,
                null
        );

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("IP deve ser informado quando houver usuario no log de acao", exception.getMessage());
    }

    private Usuarios criarUsuario(Integer id) {
        Usuarios usuario = new Usuarios();
        usuario.setId(id);
        return usuario;
    }
}
