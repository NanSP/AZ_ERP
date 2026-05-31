package com.example.backend.sys.usuarioPerfil;

import com.example.backend.shared.exception.ValidacaoException;
import com.example.backend.sys.perfilPermissao.PerfilPermissaoRepository;
import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPerfilServiceTest {

    @Mock
    private UsuarioPerfilRepository repository;

    @Mock
    private UsuariosRepository usuariosRepository;

    @Mock
    private PerfisRepository perfisRepository;

    @Mock
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @InjectMocks
    private UsuarioPerfilService service;

    @Test
    void deveCriarVinculoQuandoUsuarioEstiverAtivoEPerfilPossuirPermissoes() {
        UsuarioPerfilRequestDTO request = new UsuarioPerfilRequestDTO(1, 2);

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario("ativo")));
        when(perfisRepository.findById(2)).thenReturn(Optional.of(criarPerfil()));
        when(perfilPermissaoRepository.existsByPerfilId(2)).thenReturn(true);
        when(repository.save(any(UsuarioPerfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioPerfil saved = service.criar(request);

        assertEquals(1, saved.getUsuario().getId());
        assertEquals(2, saved.getPerfil().getId());
        assertNotNull(saved.getDataAtribuicao());

        ArgumentCaptor<UsuarioPerfil> captor = ArgumentCaptor.forClass(UsuarioPerfil.class);
        verify(repository).save(captor.capture());
        assertEquals(1, captor.getValue().getUsuario().getId());
    }

    @Test
    void deveBloquearCriacaoQuandoUsuarioNaoEstiverAtivo() {
        UsuarioPerfilRequestDTO request = new UsuarioPerfilRequestDTO(1, 2);

        when(usuariosRepository.findById(1)).thenReturn(Optional.of(criarUsuario("inativo")));
        when(perfisRepository.findById(2)).thenReturn(Optional.of(criarPerfil()));

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.criar(request));

        assertEquals("Nao e permitido vincular perfil a usuario que nao esteja ativo", exception.getMessage());
    }

    @Test
    void deveBloquearExclusaoDoUltimoPerfilDoUsuario() {
        UsuarioPerfil entity = new UsuarioPerfil();
        entity.setId(10);
        entity.setUsuario(criarUsuario("ativo"));
        entity.setPerfil(criarPerfil());

        when(repository.findById(10)).thenReturn(Optional.of(entity));
        when(repository.existsByUsuarioIdAndIdNot(1, 10)).thenReturn(false);

        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> service.excluir(10));

        assertEquals("Nao e permitido remover o ultimo perfil do usuario", exception.getMessage());
        verify(repository, never()).delete(any(UsuarioPerfil.class));
    }

    private Usuarios criarUsuario(String status) {
        Usuarios usuario = new Usuarios();
        usuario.setId(1);
        usuario.setNome("Usuario Teste");
        usuario.setStatus(status);
        return usuario;
    }

    private Perfis criarPerfil() {
        Perfis perfil = new Perfis();
        perfil.setId(2);
        perfil.setNome("Administrador");
        return perfil;
    }
}
