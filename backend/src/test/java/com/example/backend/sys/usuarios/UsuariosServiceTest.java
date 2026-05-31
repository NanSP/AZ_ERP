package com.example.backend.sys.usuarios;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.usuarioPerfil.UsuarioPerfilRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuariosServiceTest {

    @Mock
    private UsuariosRepository repository;

    @Mock
    private UsuarioPerfilRepository usuarioPerfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuariosService service;

    @Test
    void deveCriarUsuarioComSenhaHashEValoresNormalizados() {
        UsuariosRequestDTO request = new UsuariosRequestDTO(
                " Usuario Teste ",
                "USER@ERP.COM",
                " User.Login ",
                "senha123",
                "12345678900",
                null,
                null,
                null,
                LocalDate.now().plusDays(30),
                null
        );

        when(passwordEncoder.encode("senha123")).thenReturn("senha-hash");
        when(repository.save(any(Usuarios.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuarios saved = service.criar(request);

        assertEquals("Usuario Teste", saved.getNome());
        assertEquals("USER@ERP.COM", saved.getEmail());
        assertEquals("User.Login", saved.getLogin());
        assertEquals("operador", saved.getTipoUsuario());
        assertEquals("ativo", saved.getStatus());
        assertEquals(0, saved.getTentativasLogin());
        assertEquals("senha-hash", saved.getSenhaHash());
        assertNull(saved.getUltimoAcesso());
    }

    @Test
    void deveBloquearAlteracaoDeStatusQuandoUsuarioPossuirPerfis() {
        Usuarios entity = criarUsuario();
        entity.setStatus("ativo");

        UsuariosRequestDTO request = new UsuariosRequestDTO(
                "Usuario Teste",
                "user@erp.com",
                "user.login",
                null,
                "12345678900",
                "operador",
                "bloqueado",
                null,
                LocalDate.now().plusDays(30),
                0
        );

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(usuarioPerfilRepository.existsByUsuarioId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.atualizar(1, request));

        assertEquals("Nao e permitido alterar o status de usuario que possui perfis vinculados", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoQuandoUsuarioPossuirPerfisVinculados() {
        Usuarios entity = criarUsuario();

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(usuarioPerfilRepository.existsByUsuarioId(1)).thenReturn(true);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(1));

        assertEquals("Nao e permitido excluir usuario que possui perfis vinculados", exception.getMessage());
        verify(repository, never()).delete(any(Usuarios.class));
    }

    private Usuarios criarUsuario() {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Usuario Teste");
        usuario.setEmail("user@erp.com");
        usuario.setLogin("user.login");
        usuario.setTipoUsuario("operador");
        usuario.setStatus("ativo");
        usuario.setTentativasLogin(0);
        return usuario;
    }
}
